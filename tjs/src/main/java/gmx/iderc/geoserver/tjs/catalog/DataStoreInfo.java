/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs.catalog;

import gmx.iderc.geoserver.tjs.data.TJSDataStore;
import java.io.Serializable;
import java.util.Map;
import org.geoserver.catalog.MetadataMap;
import org.geotools.api.util.ProgressListener;

/** @author capote */
public interface DataStoreInfo extends TJSCatalogObject {

    TJSCatalog getCatalog();

    String getType();

    void setType(String type);

    MetadataMap getMetadata();

    TJSDataStore getTJSDataStore(ProgressListener listener);

    void reloadTJSDataStore();

    Map<String, Serializable> getConnectionParameters();

    Throwable getError();

    void setError(Throwable t);
}
