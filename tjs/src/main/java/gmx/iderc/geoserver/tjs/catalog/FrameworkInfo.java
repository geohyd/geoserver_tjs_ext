/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs.catalog;

import java.util.Date;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;

/** @author root */
public interface FrameworkInfo extends TJSCatalogObject {

    FeatureTypeInfo getFeatureType();

    TJSCatalog getCatalog();

    void setCatalog(TJSCatalog catalog);

    void setFeatureType(FeatureTypeInfo featureType);

    String getUri();

    AttributeTypeInfo getFrameworkKey();

    void setFrameworkKey(AttributeTypeInfo frameworkKey);

    AttributeTypeInfo getFrameworkKeyTitle();

    void setFrameworkKeyTitle(AttributeTypeInfo frameworkKey);

    ReferencedEnvelope getBoundingCoordinates();

    Date getRefererenceDate();

    void setRefererenceDate(Date date);

    String getDocumentation();

    void setDocumentation(String uri);

    int getVersion();

    void setVersion(int version);

    String getOrganization();

    void setOrganization(String organization);

    LayerInfo getAssociatedWMS();

    void setAssociatedWMS(LayerInfo wmsLayer);
}
