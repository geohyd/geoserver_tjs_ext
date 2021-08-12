/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs;

import gmx.iderc.geoserver.tjs.catalog.*;
import gmx.iderc.geoserver.tjs.catalog.impl.JoinedMapInfoImpl;
import gmx.iderc.geoserver.tjs.data.TJSFeatureSource;
import gmx.iderc.geoserver.tjs.data.TJSStore;
import gmx.iderc.geoserver.tjs.data.TJS_1_0_0_DataStore;
import gmx.iderc.geoserver.tjs.data.gdas.GDAS_DatasetInfo;
import gmx.iderc.geoserver.tjs.data.xml.ClassToXSDMapper;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import net.opengis.tjs10.*;
import org.apache.log4j.lf5.util.StreamUtils;
import org.geoserver.catalog.*;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.geotools.data.*;
import org.geotools.data.util.NullProgressListener;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureImpl.*;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.v1_0.OGC;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.v1_1.OWS;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.referencing.CRS;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.tjs.TJS;
import org.geotools.tjs.TJSConfiguration;
import org.geotools.xlink.XLINK;
import org.geotools.xml.styling.SLDParser;
import org.geotools.xml.transform.TransformerBase;
import org.geotools.xml.transform.Translator;
import org.geotools.xsd.StreamingParser;
import org.opengis.filter.capability.FunctionName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Based on the <code>org.geotools.xml.transform</code> framework, does the job of encoding a WFS
 * 1.0 Capabilities document.
 *
 * @author Gabriel Roldan, Axios Engineering
 * @author Chris Holmes
 * @author Justin Deoliveira TODO: Mention GeoCuba developers as authors
 * @author Thijs Brentjens, for TJS
 * @version $Id: CapabilitiesTransformer.java 16404 2011-10-06 18:36:00Z jdeolive $
 */
public abstract class JoinDataTransformer extends TransformerBase {

    /** logger */
    private static final Logger LOGGER =
            org.geotools.util.logging.Logging.getLogger(
                    JoinDataTransformer.class.getPackage().getName());
    /** identifer of a http get + post request */
    private static final String HTTP_GET = "Get";

    private static final String HTTP_POST = "Post";
    /** wfs namespace */
    protected static final String TJS_PREFIX = "tjs";

    protected static final String TJS_URI = "http://www.opengis.net/tjs";
    /** xml schema namespace + prefix */
    protected static final String XSI_PREFIX = "xsi";

    protected static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    /** filter namesapce + prefix */
    protected static final String OGC_PREFIX = "ogc";

    protected static final String OGC_URI = OGC.NAMESPACE;
    /** wfs service */
    protected TJSInfo tjs;
    /** catalog */
    protected TJSCatalog catalog;

    /** Creates a new CapabilitiesTransformer object. */
    public JoinDataTransformer(TJSInfo tjs, TJSCatalog catalog) {
        super();
        setNamespaceDeclarationEnabled(false);

        this.tjs = tjs;
        this.catalog = catalog;
    }

    Set<FunctionName> getAvailableFunctionNames() {
        // Sort them up for easier visual inspection
        SortedSet sortedFunctions =
                new TreeSet(
                        new Comparator() {

                            public int compare(Object o1, Object o2) {
                                String n1 = ((FunctionName) o1).getName();
                                String n2 = ((FunctionName) o2).getName();

                                return n1.toLowerCase().compareTo(n2.toLowerCase());
                            }
                        });

        Set<FunctionFactory> factories = CommonFactoryFinder.getFunctionFactories(null);
        for (FunctionFactory factory : factories) {
            sortedFunctions.addAll(factory.getFunctionNames());
        }

        return sortedFunctions;
    }

    /**
     * Transformer for TJS JoinData document.
     *
     * @author GeoCuba
     * @author Thijs Brentjens, Brentjens Geo-ICT
     */
    public static class TJS1_0 extends JoinDataTransformer {

        public TJS1_0(TJSInfo tjs, TJSCatalog catalog) {
            super(tjs, catalog);
        }

        public Translator createTranslator(ContentHandler handler) {
            return new JoinDataTranslator(handler);
        }

        class JoinDataTranslator extends TranslatorSupport {

            // private static final String GML_3_1_1_FORMAT = "text/xml; subtype=gml/3.1.1";
            JoinDataType request;

            protected String getBaseURL() {
                try {
                    Request owsRequest = ((ThreadLocal<Request>) Dispatcher.REQUEST).get();
                    if (owsRequest != null) {
                        return owsRequest.getHttpRequest().getRequestURL().toString();
                    } else {
                        return tjs.getTjsServerBaseURL();
                    }
                } catch (Exception ex) {
                    return tjs.getTjsServerBaseURL();
                }
            }

            public JoinDataTranslator(ContentHandler handler) {
                super(handler, null, null);
            }

            AttributesImpl attributes(String[] nameValues) {
                AttributesImpl atts = new AttributesImpl();

                for (int i = 0; i < nameValues.length; i += 2) {
                    String name = nameValues[i];
                    String valu = nameValues[i + 1];

                    atts.addAttribute(null, null, name, null, valu);
                }

                return atts;
            }

            Catalog geoserverCatalog;
            HashMap<String, WMSStoreInfo> layerStoreMap = new HashMap<String, WMSStoreInfo>();

            private Catalog getGeoserverCatalog() {
                if (geoserverCatalog == null) {
                    geoserverCatalog = tjs.getGeoServer().getCatalog();
                }
                return geoserverCatalog;
            }

            private WorkspaceInfo createTempWorkspace() {
                WorkspaceInfo workspaceInfo =
                        getGeoserverCatalog().getWorkspaceByName(TJSExtension.TJS_TEMP_WORKSPACE);
                if (workspaceInfo == null) {
                    NamespaceInfo namespaceInfo =
                            getGeoserverCatalog()
                                    .getNamespaceByPrefix(TJSExtension.TJS_TEMP_WORKSPACE);
                    if (namespaceInfo == null) {
                        namespaceInfo = new NamespaceInfoImpl();
                        namespaceInfo.setPrefix(TJSExtension.TJS_TEMP_WORKSPACE);
                        namespaceInfo.setURI("http://www.iderc.co.cu/geomix/tjs/temp");
                        getGeoserverCatalog().add(namespaceInfo);
                    }
                    workspaceInfo = new WorkspaceInfoImpl();
                    workspaceInfo.setName(TJSExtension.TJS_TEMP_WORKSPACE);
                    getGeoserverCatalog().add(workspaceInfo);
                }
                return workspaceInfo;
            }

            public void setUpGetDataURL() throws IOException {
                URL url = new URL(request.getAttributeData().getGetDataURL());
                InputStream is = url.openStream();
                // is = copy(is);
                if (is != null) {
                    TJSConfiguration tjsConfiguration = new TJSConfiguration();
                    try {
                        StreamingParser parser =
                                new StreamingParser(tjsConfiguration, is, TJS.GDAS);
                        GDASType gdas = (GDASType) parser.parse();
                        if (gdas == null) {
                            return;
                        }
                        String newStyleName = null;

                        GDAS_DatasetInfo gdas_datasetInfo =
                                new GDAS_DatasetInfo(
                                        gdas, catalog, request.getAttributeData().getGetDataURL());

                        if (request.getMapStyling() != null) {
                            newStyleName =
                                    handleMapStyling(gdas_datasetInfo, request.getMapStyling());
                        }
                        /**
                         * AnteaGroup : We need this for set a name contains the stylename When you
                         * joindata with same getdata but different style, we want 2 layers set, you
                         * need to override the name with the stylename. I choose this solution for
                         * do this.
                         */
                        if (newStyleName != null && newStyleName.isEmpty() == false) {
                            // Paste of HSQLDB_GDAS_Cache.getSafeTableName function
                            String newDSIName = gdas_datasetInfo.getName() + "_" + newStyleName;
                            newDSIName = newDSIName.toUpperCase();
                            newDSIName = newDSIName.replaceAll("[^A-Z0-9_]", "");
                            /*if (newDSIName.length() >= 32) {
                                newDSIName = newDSIName.substring(0, 32);
                            }*/
                            gdas_datasetInfo.setName(newDSIName);
                        }

                        String frameworkURI = gdas.getFramework().getFrameworkURI();
                        FrameworkInfo frameworkInfo = catalog.getFrameworkByUri(frameworkURI);
                        if (frameworkInfo == null) {
                            throw new TJSException(
                                    "This version only supports hosted framework's URI");
                        }
                        handleFramework(frameworkInfo);

                        start("JoinedOutputs");

                        // Thijs: create a WMS and WFS mechanism here. For output in all kinds of
                        // formats.
                        setUpWFSandWMSMechanism(frameworkInfo, gdas_datasetInfo, newStyleName);

                        end("JoinedOutputs");

                    } catch (ParserConfigurationException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage());
                    } catch (SAXException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage());
                    }
                }
            }

            private String handleMapStyling(DatasetInfo datasetInfo, MapStylingType mapStyling) {
                if (mapStyling.getStylingURL() == null) {
                    return null;
                }
                String newStyleName = null;
                if (!mapStyling.getStylingURL().startsWith("http")) {
                    // Consider this is a GeoServer Style.
                    newStyleName = mapStyling.getStylingURL();
                    StyleInfo styleFromCatalog =
                            getGeoserverCatalog().getFacade().getStyleByName(newStyleName);
                    if (styleFromCatalog != null) {
                        datasetInfo.setDefaultStyle(newStyleName);
                    }
                } else {
                    URL url = null;
                    try {
                        url = new URL(mapStyling.getStylingURL());
                        InputStream is = url.openStream();

                        // hago una copia del SLD y cierro la conexión
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        StreamUtils.copy(is, out);
                        out.close();
                        is.close();

                        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                        // valido que el SLD esté correcto para no trabajar por gusto
                        SLDParser sldParser = new SLDParser(CommonFactoryFinder.getStyleFactory());
                        sldParser.setInput(in);
                        StyledLayerDescriptor styledLayerDescriptor = sldParser.parseSLD();

                        // Antea Group : Create stylename with the hash of the url instead of
                        // currentTimeMillis
                        // So, if you use the same url sld, only one style with create, and update
                        // each time.
                        // newStyleName = "GDASStyle" + String.valueOf(System.currentTimeMillis());
                        newStyleName =
                                "GDASStyle" + String.valueOf(mapStyling.getStylingURL().hashCode());
                        StyleInfo newStyleInfo = getGeoserverCatalog().getStyleByName(newStyleName);
                        if (newStyleInfo == null) {
                            // First time, create new style
                            newStyleInfo = getGeoserverCatalog().getFactory().createStyle();
                            newStyleInfo.setName(newStyleName);
                            String styleFileName = newStyleInfo.getName() + ".sld";
                            newStyleInfo.setFilename(styleFileName);
                            getGeoserverCatalog().add(newStyleInfo);
                        }
                        // hago persistente el estilo en el catálogo para que se pueda trabajar con
                        // él
                        in.reset();
                        // Always update the style with the source
                        getGeoserverCatalog().getResourcePool().writeStyle(newStyleInfo, in);
                        in.close();

                        datasetInfo.setDefaultStyle(newStyleName);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return newStyleName;
            }

            JoinedMapInfo makeJoinedMapByGetDataURL(
                    String getDataURL, String frameworkURI, String datasetURI) {
                // supongo que si llegamos aquí es porque el mapa se creó correctamente, Alvaro
                // Javier
                // así que vamos a guardar la información correspondiente que permita manipular este
                // mapa
                // en un futuro, sobre to.do reconstruirlo
                JoinedMapInfo joinedMap;

                final List<JoinedMapInfo> joinedMapsByGetDataURL =
                        catalog.getJoinedMapsByGetDataURL(getDataURL);
                if (joinedMapsByGetDataURL != null && joinedMapsByGetDataURL.size() > 0) {
                    joinedMap = joinedMapsByGetDataURL.get(0);
                } else {
                    joinedMap = new JoinedMapInfoImpl(catalog);
                }
                // joinedMap.setGetDataURL(request.getAttributeData().getGetDataURL());
                joinedMap.setGetDataURL(getDataURL);
                joinedMap.setFrameworkURI(frameworkURI); // ?
                joinedMap.setCreationTime(System.currentTimeMillis());
                joinedMap.setServerURL(getServerURL());
                joinedMap.setDatasetUri(datasetURI);
                // joinedMap.setLifeTime(60*60*1000);//una hora
                catalog.save(joinedMap);
                return joinedMap;
            }

            void handleShapeMechanism() {
                start(TJS.Mechanism.getLocalPart());
                element(TJS.Identifier.getLocalPart(), "shapefile");
                element(TJS.Title.getLocalPart(), "ESRI shapefile");
                element(TJS.Abstract.getLocalPart(), "ESRI shapefile with spatial dataset");
                element("Reference", "ESRI shapefile with spatial dataset");
                end(TJS.Mechanism.getLocalPart());
            }

            // setup WMS output
            void handleWMSMechanism() {
                start(TJS.Mechanism.getLocalPart());
                element(TJS.Identifier.getLocalPart(), "WMS");
                element(TJS.Title.getLocalPart(), "WMS Server v1.1.1");
                element(
                        TJS.Abstract.getLocalPart(),
                        "The OpenGIS® Web Map Service Interface Standard (WMS) provides a simple HTTP interface for requesting geo-registered map images from one or more distributed geospatial databases. A WMS request defines the geographic layer(s) and area of interest to be processed. The response to the request is one or more geo-registered map images (returned as JPEG, PNG, etc) that can be displayed in a browser application. The interface also supports the ability to specify whether the returned images should be transparent so that layers from multiple servers can be combined or no");
                element("Reference", "http://schemas.opengis.net/wms/1.1.1/");
                end(TJS.Mechanism.getLocalPart());
            }

            // Thijs: setup WFS output
            void handleWFSMechanism() {
                start(TJS.Mechanism.getLocalPart());
                element(TJS.Identifier.getLocalPart(), "WFS");
                element(TJS.Title.getLocalPart(), "WFS Server v2.0");
                element(
                        TJS.Abstract.getLocalPart(),
                        "The Web Feature Service (WFS) represents a change in the way geographic information is created, modified and exchanged on the Internet. Rather than sharing geographic information at the file level using File Transfer Protocol (FTP), for example, the WFS offers direct fine-grained access to geographic information at the feature and feature property level. Web feature services allow clients to only retrieve or modify the data they are seeking, rather than retrieving a file that contains the data they are seeking and possibly much more. That data can then be used for a wide variety of purposes, including purposes other than their producers' intended ones.");
                element("Reference", "http://schemas.opengis.net/wfs/2.0/");
                end(TJS.Mechanism.getLocalPart());
            }

            // TODO: Need to refactor code, to extract the datastore and featuretype creation code

            private void setUpWFSandWMSMechanism(
                    FrameworkInfo frameworkInfo, DatasetInfo datasetInfo, String newStyleName)
                    throws IOException {
                try {

                    WorkspaceInfo tempWorkspaceInfo = createTempWorkspace();
                    Catalog gsCatalog = getGeoserverCatalog();
                    String newFeatureTypeName = datasetInfo.getName();

                    // already existing layers in datastore:
                    Boolean ftExists =
                            catalog.getDataset(
                                            datasetInfo.getDataStore().getId(), newFeatureTypeName)
                                    != null;
                    // If the dataset is already known and the featuretype already exists, then
                    // remove the layer and featuretypeinfo from
                    // Geoserver and the TJS catalog
                    Boolean inCatalog =
                            catalog.getDatasetByUri(datasetInfo.getDatasetUri()) != null;
                    if (inCatalog && ftExists) {
                        // AnteaGroup : We try/catch each remove test.
                        // We need to remove the most things as possible
                        // If the user delete the layer from Geoserver UI,
                        // we cannot remove the layer here from GSCat but need to remove this from
                        // TJS Cat
                        try {
                            LayerInfo layerInfo = gsCatalog.getLayerByName(newFeatureTypeName);
                            gsCatalog.remove(layerInfo);
                        } catch (Exception e) {
                            Logger.getLogger(JoinDataTransformer.class.getName())
                                    .log(Level.SEVERE, "Cannot remove LayerInfo from GS Catalog");
                        }
                        try {
                            FeatureTypeInfo ftInfo =
                                    gsCatalog.getFeatureTypeByName(newFeatureTypeName);
                            gsCatalog.remove(ftInfo);
                        } catch (Exception e) {
                            Logger.getLogger(JoinDataTransformer.class.getName())
                                    .log(
                                            Level.SEVERE,
                                            "Cannot remove FeatureTypeInfo from GS Catalog");
                        }
                        try {
                            // remove the datasetInfo, because we get some new info this time
                            catalog.remove(datasetInfo.getDataStore());
                        } catch (Exception e) {
                            Logger.getLogger(JoinDataTransformer.class.getName())
                                    .log(
                                            Level.SEVERE,
                                            "Cannot remove DatasetInfo from TJS Catalog");
                        }
                    }

                    CatalogBuilder builder = new CatalogBuilder(gsCatalog);

                    TJS_1_0_0_DataStore tjs100DataStore = createTJSDataStore(frameworkInfo);

                    TJSStore tempTJSStore = new TJSStore(tjs100DataStore, gsCatalog);
                    tempTJSStore.setWorkspace(tempWorkspaceInfo);

                    catalog.add(datasetInfo);

                    // if the temp datastore does not exist, create a new one

                    List<DataStoreInfo> tjsTempDataStores =
                            gsCatalog.getDataStoresByWorkspace(
                                    tempWorkspaceInfo); // TJSExtension.TJS_TEMP_WORKSPACE

                    String tempDataStoreName = tempTJSStore.getName();

                    DataStoreInfo dsInfoNew =
                            getTempDatastoreIfExists(tjsTempDataStores, tempDataStoreName);
                    // add a datastore if there does not seem to be one already
                    if (dsInfoNew == null) {
                        builder.setWorkspace(tempWorkspaceInfo);
                        dsInfoNew = builder.buildDataStore(TJSExtension.TJS_TEMP_WORKSPACE);
                        dsInfoNew.setName(tempDataStoreName);
                        try {
                            gsCatalog.add((DataStoreInfo) tempTJSStore);
                        } catch (Exception ex) {
                            Logger.getLogger(JoinDataTransformer.class.getName())
                                    .log(Level.SEVERE, ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                    // Create a full Geoserver datastore and layer, if the featuretype is not
                    // available yet

                    List<FeatureTypeInfo> featureTypes =
                            gsCatalog.getResourcesByStore(dsInfoNew, FeatureTypeInfo.class);
                    FeatureTypeInfo featureTypeInfo =
                            getFeatureTypeInfoIfExists(featureTypes, newFeatureTypeName);

                    if (featureTypeInfo == null) {

                        builder.setStore(tempTJSStore);

                        FeatureSource featureSource =
                                (FeatureSource)
                                        tjs100DataStore.getFeatureSource(newFeatureTypeName);
                        featureTypeInfo = builder.buildFeatureType(featureSource);
                        CoordinateReferenceSystem crs = ((TJSFeatureSource) featureSource).getCRS();
                        ReferencedEnvelope bounds = featureSource.getBounds();
                        featureTypeInfo.setNativeBoundingBox(bounds);

                        // TODO: what if we don't have a CRS / crs==null. What to do? Stop adding
                        // the featuretype?
                        featureTypeInfo.setNativeCRS(crs);
                        if (crs != null) {
                            featureTypeInfo.setLatLonBoundingBox(
                                    bounds.transform(
                                            CRS.decode("EPSG:4326"), true)); // true: lenient?
                        }

                        // explicitly add the SRS code
                        featureTypeInfo.setSRS(((TJSFeatureSource) featureSource).getSRS());
                        gsCatalog.add(featureTypeInfo);
                        builder.setWorkspace(tempWorkspaceInfo);
                        builder.setStore(tempTJSStore);

                        builder.setupBounds(featureTypeInfo, featureSource);
                        builder.initFeatureType(featureTypeInfo);
                        LayerInfo layer = builder.buildLayer(featureTypeInfo);

                        if (newStyleName != null) {
                            StyleInfo defaultStyle = gsCatalog.getStyleByName(newStyleName);
                            layer.setDefaultStyle(defaultStyle);
                        } else {
                            // get the style of the originating layer
                            try {
                                String sourceName = frameworkInfo.getFeatureType().getNativeName();
                                StyleInfo sourceStyle =
                                        gsCatalog.getLayerByName(sourceName).getDefaultStyle();
                                layer.setDefaultStyle(sourceStyle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // set Advertised (published) at false. So, the tjs layer does not appear in
                        // the getcapabilities
                        // So as not to pollute the getCap. You can see the layer only if you know
                        // here name, this is done in the joindata reponse.
                        layer.setAdvertised(false);
                        gsCatalog.add(layer);
                    } else {
                        // reload layer?
                    }

                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.SEVERE, "START OUTPUT WMS");
                    // }
                    // output for WMS
                    start("Output");

                    handleWMSMechanism();

                    start("Resource");

                    String getTempWMSUrl = getTempWMSUrl(tempWorkspaceInfo);
                    element(
                            "URL",
                            getTempWMSUrl + "?request=GetCapabilities&service=WMS"); // has to be
                    // including the
                    // GetCapabilities
                    // parameters,
                    // according to the
                    // TJS spec

                    AttributesImpl attributes = attributes(new String[] {"name", "domainName"});
                    element("Parameter", getTempWMSUrl, attributes);

                    // for WMS this shall be layers
                    attributes = attributes(new String[] {"name", "layers"});
                    element("Parameter", newFeatureTypeName, attributes);

                    end("Resource");
                    end("Output");

                    // output for WFS
                    start("Output");
                    handleWFSMechanism();

                    start("Resource");
                    String tempWFSUrl = getTempWFSUrl(tempWorkspaceInfo);
                    element("URL", tempWFSUrl + "?request=GetCapabilities&service=WFS");

                    attributes = attributes(new String[] {"name", "domainName"});
                    element("Parameter", tempWFSUrl, attributes);

                    attributes = attributes(new String[] {"name", "typeName"});
                    element("Parameter", newFeatureTypeName, attributes);

                    end("Resource");
                    end("Output");

                } catch (Exception ex) {
                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.SEVERE, ex.getMessage());
                    ex.printStackTrace();
                }
            }

            protected void clone(WMSStoreInfo source, WMSStoreInfo target) {
                target.setDescription(source.getDescription());
                target.setEnabled(source.isEnabled());
                target.setName(source.getName());
                target.setType(source.getType());
                target.setCapabilitiesURL(source.getCapabilitiesURL());
                target.setWorkspace(source.getWorkspace());
                target.setUsername(source.getUsername());
                target.setPassword(source.getPassword());
                target.setUseConnectionPooling(source.isUseConnectionPooling());
                target.setMaxConnections(source.getMaxConnections());
                target.setConnectTimeout(source.getConnectTimeout());
                target.setReadTimeout(source.getReadTimeout());
            }

            private boolean equalWMSStore(WMSStoreInfo s1, WMSStoreInfo s2) {
                try {
                    WebMapServer wms = s1.getWebMapServer(new NullProgressListener());
                    WebMapServer wms2 = s2.getWebMapServer(new NullProgressListener());
                    return wms.equals(wms2);
                } catch (IOException ex) {
                    return false;
                }
            }

            public void setUpGetDataXML() throws IOException {
                GetDataXMLType getDataXML = request.getAttributeData().getGetDataXML();
                String frameworkURI = getDataXML.getFrameworkURI();
                FrameworkInfo frameworkInfo = catalog.getFrameworkByUri(frameworkURI);
                if (frameworkInfo == null) {
                    throw new TJSException("This version only support hosted framework's URI");
                }
                handleFramework(frameworkInfo);

                start("JoinedOutputs");

                // Thijs: setupWMSMechanism is deprecated, with the new setup of WFS and WMS at once
                // setUpWMSMechanism(frameworkInfo, getDataXML.getDatasetURI());

                end("JoinedOutputs");
            }

            public void encode(Object object) throws IllegalArgumentException {
                request = (JoinDataType) object;
                AttributesImpl attributes =
                        attributes(
                                new String[] {
                                    "version",
                                    "1.0",
                                    "lang",
                                    "es",
                                    "service",
                                    "TJS",
                                    "capabilities",
                                    "http://sis.agr.gc.ca/pls/meta/tjs_1x0_getcapabilities",
                                    "xmlns:xsi",
                                    XSI_URI,
                                    "xmlns",
                                    TJS_URI,
                                    "xmlns:ows",
                                    OWS.NAMESPACE, // "xmlns:gml", GML.NAMESPACE,
                                    "xmlns:ogc",
                                    OGC.NAMESPACE,
                                    "xmlns:xlink",
                                    XLINK.NAMESPACE,
                                    "xsi:schemaLocation",
                                    TJS.NAMESPACE
                                            + " "
                                            + "http://schemas.opengis.net/tjs/1.0/tjsDescribeDatasets_response.xsd"
                                });

                List<NamespaceInfo> namespaces = catalog.getNamespaces();
                for (NamespaceInfo namespace : namespaces) {
                    String prefix = namespace.getPrefix();
                    String uri = namespace.getURI();

                    // ignore xml prefix
                    if ("xml".equals(prefix)) {
                        continue;
                    }

                    String prefixDef = "xmlns:" + prefix;

                    attributes.addAttribute("", prefixDef, prefixDef, "", uri);
                }

                start(TJS.JoinDataResponse.getLocalPart(), attributes);
                attributes =
                        attributes(
                                new String[] {
                                    "xlink:href",
                                    "http://www.iderc.co.cu/geomix/tjs",
                                    "creationTime",
                                    (new Date().toString())
                                });
                start("Status", attributes);
                start("completed");
                end("completed");
                end("Status");
                // Grandes cambios
                if (request.getAttributeData().getGetDataURL() != null) {
                    try {
                        setUpGetDataURL();
                    } catch (IOException ex) {
                        Logger.getLogger(JoinDataTransformer.class.getName())
                                .log(Level.SEVERE, ex.getMessage());
                    }
                } else if (request.getAttributeData().getGetDataXML() != null) {
                    try {
                        setUpGetDataXML();
                    } catch (IOException ex) {
                        Logger.getLogger(JoinDataTransformer.class.getName())
                                .log(Level.SEVERE, ex.getMessage());
                    }
                }
                end(TJS.JoinDataResponse.getLocalPart());
            }

            void handleFrameworkKey(AttributeTypeInfo frameworkKey) {
                if (frameworkKey == null) {
                    return;
                }
                start(TJS.FrameworkKey.getLocalPart());
                //   <Column name="ecozone" type="http://www.w3.org/TR/xmlschema-2/#integer"
                // length="2" decimals="0" />
                AttributesImpl attributes =
                        attributes(
                                new String[] {
                                    "name", frameworkKey.getName(),
                                    "type", ClassToXSDMapper.map(frameworkKey.getBinding()),
                                    "length", String.valueOf(frameworkKey.getLength()),
                                    "decimals", "0"
                                });
                element("Column", "", attributes);
                end(TJS.FrameworkKey.getLocalPart());
            }

            void handleBoundingCoordinates(ReferencedEnvelope envelope) {
                if (envelope == null) {
                    return;
                }
                start(TJS.BoundingCoordinates.getLocalPart());
                element("North", String.valueOf(envelope.getMaxY()));
                element("South", String.valueOf(envelope.getMinY()));
                element("East", String.valueOf(envelope.getMaxX()));
                element("West", String.valueOf(envelope.getMinX()));
                end(TJS.BoundingCoordinates.getLocalPart());
            }

            void handleFramework(FrameworkInfo framework) {
                start(TJS.Framework.getLocalPart());
                element(TJS.FrameworkURI.getLocalPart(), framework.getUri());
                element(TJS.Organization.getLocalPart(), framework.getOrganization());
                element(TJS.Title.getLocalPart(), framework.getName());
                element(TJS.Abstract.getLocalPart(), framework.getDescription());
                if (framework.getRefererenceDate() != null) {
                    element(
                            TJS.ReferenceDate.getLocalPart(),
                            framework.getRefererenceDate().toString());
                }
                element(TJS.Version.getLocalPart(), String.valueOf(framework.getVersion()));
                element(TJS.Documentation.getLocalPart(), framework.getDocumentation());
                handleFrameworkKey(framework.getFrameworkKey());
                handleBoundingCoordinates(framework.getBoundingCoordinates());
                element(
                        TJS.DescribeDatasetsRequest.getLocalPart(),
                        getBaseURL()
                                + "?request=DescribeDatasets&Service=TJS&Version=1.0.0&FrameworkURI="
                                + framework.getUri());
                end(TJS.Framework.getLocalPart());
            }

            private String replaceIgnoreCase(String base, String what, String newContent) {
                int index = base.toUpperCase().indexOf(what.toUpperCase());
                if (index >= 0) {
                    String before = base.substring(0, index);
                    String after = base.substring(index + what.length(), base.length());

                    return before + newContent + after;
                }
                return base;
            }

            private String replaceLastIgnoreCase(String string, String from, String to) {
                int lastIndex = string.toUpperCase().lastIndexOf(from.toUpperCase());
                if (lastIndex < 0) return string;
                String tail =
                        string.toUpperCase()
                                .substring(lastIndex)
                                .replaceFirst(from.toUpperCase(), to);
                return string.substring(0, lastIndex) + tail;
            }

            private String getLocalWMSUrl(String workspace) {
                String baseURL = getBaseURL();
                String wms;

                // so not replaceIgnoreCase
                if (baseURL.toUpperCase().endsWith("/OWS")) {
                    wms = replaceLastIgnoreCase(baseURL, "/OWS", "/" + workspace + "/wms");
                } else {
                    if (baseURL.toUpperCase().endsWith("/TJS")) {
                        // only the last part should be replaced, not all parts
                        wms = replaceLastIgnoreCase(baseURL, "/TJS", "/" + workspace + "/wms");
                    } else {
                        wms = getBaseURL().concat("/" + workspace + "/wms");
                    }
                }
                return wms + "?request=GetCapabilities&service=WMS";
            }

            private String getTempWMSUrl(WorkspaceInfo workspaceInfo) {
                String baseURL = getBaseURL();
                String wms;
                if (baseURL.toUpperCase().endsWith("/OWS")) {
                    wms =
                            replaceLastIgnoreCase(
                                    baseURL,
                                    "/OWS",
                                    "/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wms");
                } else {
                    if (baseURL.toUpperCase().endsWith("/TJS")) {
                        wms =
                                replaceLastIgnoreCase(
                                        baseURL,
                                        "/TJS",
                                        "/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wms");
                    } else {
                        wms = getBaseURL().concat("/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wms");
                    }
                }
                return wms;
            }

            // Thijs: TODO: refactor for WFS and WMS
            private String getTempWFSUrl(WorkspaceInfo workspaceInfo) {
                String baseURL = getBaseURL();
                Logger.getLogger(JoinDataTransformer.class.getName())
                        .log(Level.FINE, "BaseURL for the service: " + baseURL);
                String wfs;
                if (baseURL.toUpperCase().endsWith("/OWS")) {
                    wfs =
                            replaceLastIgnoreCase(
                                    baseURL,
                                    "/OWS",
                                    "/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wfs");
                } else {
                    if (baseURL.toUpperCase().endsWith("/TJS")) {
                        wfs =
                                replaceLastIgnoreCase(
                                        baseURL,
                                        "/TJS",
                                        "/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wfs");
                    } else {
                        wfs = getBaseURL().concat("/" + TJSExtension.TJS_TEMP_WORKSPACE + "/wfs");
                    }
                }
                return wfs;
            }

            //            private WebMapServer createWebMapServer(FrameworkInfo frameworkInfo) {
            //                try {
            //                    LayerInfo layer = frameworkInfo.getAssociatedWMS();
            //                    //esto me paece que es una fuente de error!, Alvaro Javier Fuentes
            // Suarez
            //                    //String prefixedLayerName =
            // layer.getResource().getPrefixedName();
            //                    //se hace así, Alvaro Javier Fuentes Suarez
            //                    String layerName = layer.getName();
            //                    String prefix = layer.getResource().getNamespace().getPrefix();
            //
            //                    CatalogBuilder builder = new
            // CatalogBuilder(getGeoserverCatalog());
            //                    URL wmsServerUrl = new URL(getLocalWMSUrl(prefix));
            //                    //WebMapServer wms = null;
            //                    //Si no existe el store en el catalogo lo creo
            //                    WMSStoreInfo wmsStoreInfo =
            // getGeoserverCatalog().getStoreByName(prefix, WMSStoreInfo.class);
            //                    if (wmsStoreInfo == null) {
            //                        wmsStoreInfo = builder.buildWMSStore(prefix);
            //                        wmsStoreInfo.setCapabilitiesURL(wmsServerUrl.toString());
            //                        wmsStoreInfo.setWorkspace(createTempWorkspace());
            //                        getGeoserverCatalog().add(wmsStoreInfo);
            //                    }
            //                    builder.setStore(wmsStoreInfo);
            //                    //no usar el prefixed!, Alvaro Javier Fuentes Suarez
            //                    //WMSLayerInfo wmsLayerInfo =
            // builder.buildWMSLayer(prefixedLayerName);
            //                    //use reste!, Alvaro Javier Fuentes Suarez
            //                    WMSLayerInfo wmsLayerInfo = builder.buildWMSLayer(layerName);
            //                    WMSLayerInfo exists =
            // getGeoserverCatalog().getResourceByStore(wmsStoreInfo,
            // frameworkInfo.getAssociatedWMS().getName(), WMSLayerInfo.class);
            //                    if (exists != null) {
            //                        builder.updateWMSLayer(exists, wmsLayerInfo);
            //                    } else {
            //                        LayerInfo layerInfo = builder.buildLayer(wmsLayerInfo);
            //                        getGeoserverCatalog().add(wmsLayerInfo);
            //                        getGeoserverCatalog().add(layerInfo);
            //                    }
            //                    return wmsStoreInfo.getWebMapServer(new NullProgressListener());
            //                } catch (MalformedURLException ex) {
            //
            //                } catch (IOException ex) {
            //
            //                }
            //                return null;
            //            }

            // TODO: move to factory?

            private TJS_1_0_0_DataStore createTJSDataStore(FrameworkInfo frameworkInfo) {
                try {
                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.FINE, "Creating TJS Data store for WFS");
                    // LayerInfo layer = frameworkInfo.getAssociatedWMS();
                    FeatureTypeInfo featureTypeInfo = frameworkInfo.getFeatureType();
                    // CatalogBuilder builder = new CatalogBuilder(getGeoserverCatalog());

                    // we have a WFS Datastore already  for the featuretype
                    DataStore featureDataStore =
                            (DataStore) featureTypeInfo.getStore().getDataStore(null);
                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.FINE, "Datastore: " + featureDataStore.toString());

                    // should be a TJS Catalog here...
                    TJSCatalog tjsCatalog = TJSExtension.getTJSCatalog();
                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.FINE, "TJS Catalog: " + tjsCatalog.toString());

                    return new TJS_1_0_0_DataStore(tjsCatalog, featureDataStore, frameworkInfo);
                    // return wmsStoreInfo.getWebMapServer(new NullProgressListener());
                } catch (Exception ex) {
                    Logger.getLogger(JoinDataTransformer.class.getName())
                            .log(Level.SEVERE, "TJS Datastore error: " + ex.getMessage());
                }
                return null;
            }

            private WMSLayerInfo getIfExists(List<WMSLayerInfo> wmsLayers, String layerName) {
                for (WMSLayerInfo layerInfo : wmsLayers) {
                    if (layerInfo.getName().equalsIgnoreCase(layerName)) {
                        return layerInfo;
                    }
                }
                return null;
            }

            private FeatureTypeInfo getFeatureTypeInfoIfExists(
                    List<FeatureTypeInfo> featureTypes, String typeName) {
                for (FeatureTypeInfo featureType : featureTypes) {
                    if (featureType.getName().equalsIgnoreCase(typeName)) {
                        return featureType;
                    }
                }
                return null;
            }

            private DataStoreInfo getTempDatastoreIfExists(
                    List<DataStoreInfo> dataStores, String storeName) {
                if (dataStores != null) {
                    for (DataStoreInfo dataStoreInfo : dataStores) {
                        if (dataStoreInfo.getName().equalsIgnoreCase(storeName)) {
                            return dataStoreInfo;
                        }
                    }
                }
                return null;
            }

            private InputStream copy(InputStream source) {
                try {
                    File file = File.createTempFile("gdas", ".xml");
                    FileOutputStream fos = new FileOutputStream(file);
                    StreamUtils.copy(source, fos);
                    fos.close();
                    return new FileInputStream(file);
                } catch (IOException ex) {
                    // TODO: proper logging
                }
                return null;
            }

            public String getServerURL() {
                String res = null;
                try {
                    URL handy = new URL(getBaseURL());
                    final String[] pathSegs = handy.getPath().split("/");
                    res =
                            handy.getProtocol()
                                    + "://"
                                    + handy.getHost()
                                    + (handy.getPort() != -1 ? ":" + handy.getPort() : "")
                                    + "/"
                                    + (pathSegs.length > 0 ? pathSegs[1] : "");
                } catch (MalformedURLException e) {;
                }
                return res;
            }
        }
    }
}
