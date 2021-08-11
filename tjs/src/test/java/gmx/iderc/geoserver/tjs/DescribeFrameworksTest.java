package gmx.iderc.geoserver.tjs;

import junit.framework.Test;
import org.w3c.dom.Document;

public class DescribeFrameworksTest extends TJSTestSupport {
    /**
     * This is a READ ONLY TEST so we can use one time setup
     */
    public static Test suite() {
        return new OneTimeTestSetup(new DescribeFrameworksTest());
    }

    public void testGet() throws Exception {
        Document doc = getAsDOM("tjs?service=TJS&request=describeFrameworks&version=1.0.0");
        print(doc);
        assertEquals("FrameworkDescriptions", doc.getDocumentElement().getNodeName());
    }

//    public void testPost() throws Exception {
//        String xml = "<GetCapabilities service=\"WFS\" version=\"1.0.0\""
//                + " xmlns=\"http://www.opengis.net/wfs\" "
//                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
//                + " xsi:schemaLocation=\"http://www.opengis.net/wfs "
//                + " http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\"/>";
//        Document doc = postAsDOM("wfs", xml);
//
//        assertEquals("WFS_Capabilities", doc.getDocumentElement().getNodeName());
//
//    }

//    public void testFrameworkCount() throws Exception {
//        // filter on an existing namespace
//        Document doc = getAsDOM("tjs?service=TJS&version=1.0.0&request=getCapabilities");
//        Element e = doc.getDocumentElement();
//        assertEquals("TJS_Capabilities", e.getLocalName());
//
//        XpathEngine xpath = XMLUnit.newXpathEngine();
//
//        final List<FrameworkInfo> enabledTypes = getTjsCatalog().getFrameworks();
//        for (Iterator<FrameworkInfo> it = enabledTypes.iterator(); it.hasNext();) {
//            FrameworkInfo ft = it.next();
//            if (!ft.getEnabled()) {
//                it.remove();
//            }
//        }
//        final int enabledCount = enabledTypes.size();
//
//        assertEquals(enabledCount, xpath.getMatchingNodes(
//                "/tjs:Capabilities/wfs:FeatureTypeList/wfs:FeatureType", doc).getLength());
//    }

}
