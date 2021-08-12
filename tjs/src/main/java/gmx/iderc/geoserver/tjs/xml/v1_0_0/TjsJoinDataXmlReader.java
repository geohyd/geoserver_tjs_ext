/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * (c) 2020 Antea Group - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.xml.v1_0_0;

import gmx.iderc.geoserver.tjs.TJSException;
import java.io.Reader;
import java.util.*;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.opengis.tjs10.AttributeDataType;
import net.opengis.tjs10.GetDataXMLType;
import net.opengis.tjs10.JoinDataType;
import net.opengis.tjs10.MapStylingType;
import net.opengis.tjs10.Tjs10Factory;
import net.opengis.tjs10.impl.Tjs10FactoryImpl;
import org.geoserver.ows.XmlRequestReader;
import org.geoserver.platform.ServiceException;
import org.geoserver.util.EntityResolverProvider;
import org.geotools.util.Version;
import org.geotools.xsd.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Xml reader for tjs 1.0.0 xml requests.
 *
 * @author Jonathan Durand, Antea Group
 */
public class TjsJoinDataXmlReader extends XmlRequestReader {
    Configuration configuration;
    private EntityResolverProvider resolverProvider;

    public TjsJoinDataXmlReader(
            String element, String version, EntityResolverProvider resolverProvider) {
        super(
                new QName(gmx.iderc.geoserver.tjs.xml.TJS.NAMESPACE, element),
                new Version(version),
                "tjs");
        this.resolverProvider = resolverProvider;
    }

    @SuppressWarnings("rawtypes")
    public Object read(Object request, Reader reader, Map kvp) throws Exception {
        Document document = parse(reader);
        Tjs10Factory tjsFactory = Tjs10FactoryImpl.init();
        Node rootNode = document.getDocumentElement();
        Node nodeGetMap = rootNode;
        NamedNodeMap atts = nodeGetMap.getAttributes();
        JoinDataType joinDataType = tjsFactory.createJoinDataType();
        AttributeDataType adt = Tjs10Factory.eINSTANCE.createAttributeDataType();
        if (getNodeValue(nodeGetMap, "AttributeData") != null) {
            Node attributeDataNode = getNode(nodeGetMap, "AttributeData");
            if (getNodeValue(attributeDataNode, "GetDataURL") != null) {
                adt.setGetDataURL(getNodeValue(attributeDataNode, "GetDataURL"));
            } else {
                if (getNodeValue(attributeDataNode, "GetDataXML") != null) {

                    Node GetDataXMLNode = getNode(attributeDataNode, "GetDataXML");
                    GetDataXMLType getDataXMLType = tjsFactory.createGetDataXMLType();

                    if (getNodeValue(GetDataXMLNode, "FrameworkURI") != null) {
                        getDataXMLType.setFrameworkURI(
                                getNodeValue(GetDataXMLNode, "FrameworkURI"));
                    }
                    if (getNodeValue(GetDataXMLNode, "DatasetURI") != null) {
                        getDataXMLType.setDatasetURI(getNodeValue(GetDataXMLNode, "DatasetURI"));
                    }
                    if (getNodeValue(GetDataXMLNode, "Attributes") != null) {
                        getDataXMLType.setAttributes(getNodeValue(GetDataXMLNode, "Attributes"));
                    }
                    if (getNodeValue(GetDataXMLNode, "LinkageKeys") != null) {
                        getDataXMLType.setLinkageKeys(getNodeValue(GetDataXMLNode, "LinkageKeys"));
                    }
                    if (getAttributeValue(GetDataXMLNode, "getDataHost") != null) {
                        getDataXMLType.setGetDataHost(
                                getAttributeValue(GetDataXMLNode, "getDataHost"));
                    }
                    if (getAttributeValue(GetDataXMLNode, "language") != null) {
                        getDataXMLType.setLanguage(getAttributeValue(GetDataXMLNode, "language"));
                    }

                    adt.setGetDataXML(getDataXMLType);
                } else {
                    throw new TJSException("Request must define a GetDataURL element");
                }
            }
        }
        joinDataType.setAttributeData(adt);
        // MapStyling
        if (getNodeValue(nodeGetMap, "MapStyling") != null) {
            Node mapStylingNode = getNode(nodeGetMap, "MapStyling");
            if (getNodeValue(mapStylingNode, "StylingURL") != null) {
                String styleUrl = getNodeValue(mapStylingNode, "StylingURL");
                MapStylingType mapStylingType = Tjs10Factory.eINSTANCE.createMapStylingType();
                mapStylingType.setStylingURL(styleUrl);
                joinDataType.setMapStyling(mapStylingType);
            }
        }

        return joinDataType;
    }

    private static Document parse(Reader reader) {
        DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        try {
            DocumentBuilder parser = xmlFactory.newDocumentBuilder();
            InputSource is = new InputSource(reader);
            return parser.parse(new InputSource(reader));
        } catch (Exception exception) {
            throw new ServiceException("Error parsing XML POST GetFeature request.");
        }
    }

    /**
     * Give a node and the name of a child of that node, find its (string) value. This doesnt do
     * anything complex.
     *
     * @param parentNode
     * @param wantedChildName
     */
    public String getNodeValue(Node parentNode, String wantedChildName) {
        NodeList children = parentNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ((child == null) || (child.getNodeType() != Node.ELEMENT_NODE)) {
                continue;
            }
            String childName = child.getLocalName();
            if (childName == null) {
                childName = child.getNodeName();
            }
            if (childName.equalsIgnoreCase(wantedChildName)) {
                return child.getChildNodes().item(0).getNodeValue();
            }
        }
        return null;
    }

    public String getAttributeValue(Node node, String attribute) {
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }

    /**
     * Give a node and the name of a child of that node, find its (string) value. This doesnt do
     * anything complex.
     *
     * @param parentNode
     * @param wantedChildName
     */
    public Node getNode(Node parentNode, String wantedChildName) {
        NodeList children = parentNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ((child == null) || (child.getNodeType() != Node.ELEMENT_NODE)) {
                continue;
            }
            String childName = child.getLocalName();
            if (childName == null) {
                childName = child.getNodeName();
            }
            if (childName.equalsIgnoreCase(wantedChildName)) {
                return child;
            }
        }
        return null;
    }
}
