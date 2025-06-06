/* (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.xml;

import org.geoserver.wfs.xml.FeatureTypeSchemaBuilder;
import org.geotools.xsd.XSD;

/**
 * XSD object for GeoServer WFS 1.1.
 *
 * <p>This object is not a singleton in the conventional java sense as the other XSD subclasses
 * (GML,OGC,OWS,etc..) are. It is a singleton, but managed as such by the spring container. The
 * reason being that it requires the catalog to operate and build the underlying schema.
 */
public class TJS extends XSD {
    /** @generated */
    public static final String NAMESPACE = "http://www.opengis.net/tjs/1.0";

    /** schema type builder */
    FeatureTypeSchemaBuilder schemaBuilder;

    public TJS(FeatureTypeSchemaBuilder schemaBuilder) {
        this.schemaBuilder = schemaBuilder;
    }

    public FeatureTypeSchemaBuilder getSchemaBuilder() {
        return schemaBuilder;
    }

    /** Returns 'http://www.opengis.net/wfs' */
    public String getNamespaceURI() {
        return NAMESPACE;
    }

    /** Returns the location of 'wfs.xsd' */
    public String getSchemaLocation() {
        return gmx.iderc.geoserver.tjs.xml.TJS.class.getResource("tjsAll.xsd").toString();
    }
}
