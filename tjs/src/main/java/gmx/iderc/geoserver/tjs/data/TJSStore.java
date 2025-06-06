package gmx.iderc.geoserver.tjs.data;

import gmx.iderc.geoserver.tjs.TJSExtension;
import gmx.iderc.geoserver.tjs.catalog.impl.TJSCatalogFactoryImpl;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.StoreInfoImpl;
import org.geotools.api.data.DataAccess;
import org.geotools.api.feature.Feature;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.util.ProgressListener;

/**
 * Created with IntelliJ IDEA. User: capote Date: 10/8/12 Time: 9:34 PM To change this template use
 * File | Settings | File Templates.
 */
public class TJSStore extends StoreInfoImpl implements DataStoreInfo, Serializable {

    transient TJS_1_0_0_DataStore store;
    transient Catalog catalog;
    String id;
    String type;
    String name;
    String description;
    boolean enable;

    public TJSStore(TJS_1_0_0_DataStore store, Catalog catalog) {
        this.store = store;
        this.catalog = catalog;
        this.id = TJSCatalogFactoryImpl.getIdForObject(this);
        this.type = this.getType();
        this.name = store.getFrameworkInfo().getName();
        this.description = store.getFrameworkInfo().getDescription();
        this.enable = store.getFrameworkInfo().getEnabled();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("FrameworkId", store.getFrameworkInfo().getId());
        this.connectionParameters = params;
    }

    public DataAccess<? extends FeatureType, ? extends Feature> getDataStore(
            ProgressListener listener) throws IOException {
        return store;
    }

    public TJS_1_0_0_DataStore getStore() {
        return store;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public String getName() {
        // Thijs: trying to avoid NPEs when reloading the config.xml
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        // Thijs: trying to avoid NPEs when reloading the config.xml
        return this.description;
    }

    public void setDescription(String description) {

        // TODO: how to deal with this, since it is not about the framework?
        store.getFrameworkInfo().setDescription(description);
    }

    public String getType() {
        // TODO: make this a configurable name, constant
        return "TJSJoinedData";
    }

    public void setType(String type) {
        // TODO: implement this?
    }

    public MetadataMap getMetadata() {
        return null;
    }

    public boolean isEnabled() {
        // Thijs: trying to avoid NPEs when reloading the config.xml
        return this.enable;
    }

    public void setEnabled(boolean enabled) {

        store.getFrameworkInfo().setEnabled(enabled);
    }

    // TODO: the try/catch is a workaround to avoid an NPE when reloading he confog.
    // TODO: refactor this, to make sure the workspaceinfo could be found
    public WorkspaceInfo getWorkspace() {
        WorkspaceInfo wi = null;
        try {
            wi = catalog.getWorkspaceByName(TJSExtension.TJS_TEMP_WORKSPACE);
        } catch (Exception e) {

        }
        return wi;
    }

    public void setWorkspace(WorkspaceInfo workspace) {}

    public Map<String, Serializable> getConnectionParameters() {
        return this.connectionParameters;
    }

    public Throwable getError() {
        return null;
    }

    public void setError(Throwable t) {}

    public <T> T getAdapter(Class<T> adapterClass, Map<?, ?> hints) {
        return null;
    }

    public void accept(CatalogVisitor visitor) {}

    public String getId() {
        return id;
    }
}
