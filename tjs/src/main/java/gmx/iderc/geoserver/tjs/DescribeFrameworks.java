/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs;

import gmx.iderc.geoserver.tjs.catalog.TJSCatalog;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.opengis.tjs10.DescribeFrameworksType;
import net.opengis.tjs10.VersionType2;
import org.geoserver.ows.util.RequestUtils;
import org.geotools.xml.transform.TransformerBase;

/** Web Feature Service DescribeFeatureType operation. */
public class DescribeFrameworks {
    /** Catalog reference */
    private TJSCatalog catalog;

    /** TJS service */
    private TJSInfo tjs;

    public DescribeFrameworks(TJSInfo tjs, TJSCatalog catalog) {
        this.catalog = catalog;
        this.tjs = tjs;
    }

    public TJSInfo getTJS() {
        return tjs;
    }

    public void setTJS(TJSInfo tjs) {
        this.tjs = tjs;
    }

    public TJSCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(TJSCatalog catalog) {
        this.catalog = catalog;
    }

    private List<String> VersionType2ToStringList(List<VersionType2> vtypes) {
        List<String> versions = new ArrayList<String>();
        for (VersionType2 vtype : vtypes) {
            versions.add(vtype.getLiteral());
        }
        return versions;
    }

    public TransformerBase run(DescribeFrameworksType request) throws TJSException {

        List<String> provided = new ArrayList<String>();
        provided.add("1.0.0");
        // provided.add("1.1.0");
        List<String> requested = new ArrayList<String>();

        if (request.getVersion() != null)
            requested.add(request.getVersion()._100_LITERAL.getLiteral());
        else {
            requested.add("1.0.0");
        }
        String version = RequestUtils.getVersionPreOws(provided, requested);

        final DescribeFrameworksTransformer dfTransformer;
        if ("1.0.0".equals(version)) {
            dfTransformer = new DescribeFrameworksTransformer.TJS1_0(tjs, catalog);
        } else {
            throw new TJSException("Could not understand version:" + version);
        }
        try {
            dfTransformer.setEncoding(
                    Charset.forName(tjs.getGeoServer().getGlobal().getSettings().getCharset()));
        } catch (Exception ex) {
            Logger.getLogger(GetCapabilities.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return dfTransformer;
    }
}
