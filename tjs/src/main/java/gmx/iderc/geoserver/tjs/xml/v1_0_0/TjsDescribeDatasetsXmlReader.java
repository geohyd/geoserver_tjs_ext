/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * (c) 2020 Antea Group - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.xml.v1_0_0;

import java.io.Reader;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.opengis.tjs10.DescribeDatasetsType;
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
public class TjsDescribeDatasetsXmlReader extends XmlRequestReader {
    Configuration configuration;
    private EntityResolverProvider resolverProvider;

    public TjsDescribeDatasetsXmlReader(
            String element, String version, EntityResolverProvider resolverProvider) {
        super(
                new QName(gmx.iderc.geoserver.tjs.xml.TJS.NAMESPACE, element),
                new Version(version),
                "tjs");
        // this.configuration = new WCSEOConfiguration();
        this.resolverProvider = resolverProvider;
    }

    @SuppressWarnings("rawtypes")
    public Object read(Object request, Reader reader, Map kvp) throws Exception {
        Document document = parse(reader);
        Tjs10Factory tjsFactory = Tjs10FactoryImpl.init();

        DescribeDatasetsType describeDatasetsType = tjsFactory.createDescribeDatasetsType();
        // describeDatasetsType.setFrameworkURI("localhost/test/zones4326");

        Node rootNode = document.getDocumentElement();
        Node nodeGetMap = rootNode;
        NamedNodeMap atts = nodeGetMap.getAttributes();
        if (getNodeValue(nodeGetMap, "FrameworkURI") != null) {
            describeDatasetsType.setFrameworkURI(getNodeValue(nodeGetMap, "FrameworkURI"));
        }
        if (getNodeValue(nodeGetMap, "DatasetURI") != null) {
            describeDatasetsType.setDatasetURI(getNodeValue(nodeGetMap, "DatasetURI"));
        }

        return describeDatasetsType;
    }

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

    private static Document parse(Reader reader) {
        DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        try {
            DocumentBuilder parser = xmlFactory.newDocumentBuilder();
            return parser.parse(new InputSource(reader));
        } catch (Exception exception) {
            throw new ServiceException("Error parsing XML POST GetFeature request.");
        }
    }
}
