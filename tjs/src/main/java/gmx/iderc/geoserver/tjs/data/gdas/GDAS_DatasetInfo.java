package gmx.iderc.geoserver.tjs.data.gdas;

import gmx.iderc.geoserver.tjs.catalog.*;
import gmx.iderc.geoserver.tjs.catalog.impl.DataStoreInfoImpl;
import gmx.iderc.geoserver.tjs.data.ReadonlyDatasetInfo;
import gmx.iderc.geoserver.tjs.data.TJSDataStore;
import gmx.iderc.geoserver.tjs.data.TJSDatasource;
import gmx.iderc.geoserver.tjs.data.jdbc.JDBC_TJSDataStoreFactory;
import gmx.iderc.geoserver.tjs.data.jdbc.hsql.HSQLDB_GDAS_Cache;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.opengis.tjs10.ColumnType1;
import net.opengis.tjs10.ColumnType2;
import net.opengis.tjs10.GDASType;
import org.geotools.data.util.NullProgressListener;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

/**
 * Created with IntelliJ IDEA. User: capote Date: 29/07/13 Time: 11:24 To change this template use
 * File | Settings | File Templates.
 */
public class GDAS_DatasetInfo extends ReadonlyDatasetInfo implements Serializable {

    // avoid that these types are serialized ?
    // use transient or not?
    transient GDASType gdasType;
    DataStoreInfo dataStoreInfo;

    String tableName;
    String name;

    /* public GDAS_DatasetInfo() {
        // dummy implementation to avoid serialization
    }  */

    public GDAS_DatasetInfo(GDASType gdasType, TJSCatalog catalog, String url) {
        this.tjsCatalog = catalog;
        this.gdasType = gdasType;

        tableName = HSQLDB_GDAS_Cache.importGDAS(gdasType, url);
        name = tableName;
        DataStoreInfo dataStore = catalog.getDataStore("gdas_cache");
        if (dataStore == null) {
            DataStoreInfoImpl dataStoreInfo = new DataStoreInfoImpl(catalog);
            dataStoreInfo.setId("gdas_cache");
            dataStoreInfo.setDataStore(HSQLDB_GDAS_Cache.getCacheDataStore());
            setDataStore(dataStoreInfo);
        } else {
            setDataStore(dataStore);
        }
        // Thijs: does this HSQLDB_GDAS_Cache class usage cause a memory leak? Should the conn be
        // closed?
        // In HSQLDB_GDAS_Cache created a method closeConnections:
        // HSQLDB_GDAS_Cache.closeConnections();
        // should we use this somehow?
    }

    @Override
    public void setDataStore(DataStoreInfo dataStoreInfo) {
        this.dataStoreInfo = dataStoreInfo;
    }

    @Override
    public DataStoreInfo getDataStore() {
        return dataStoreInfo;
    }

    @Override
    public FrameworkInfo getFramework() {
        // Thijs: improve because of reloading config.xml
        try {
            return tjsCatalog.getFrameworkByUri(gdasType.getFramework().getFrameworkURI());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getDatasetUri() {
        // Thijs: change the dataseturi: add the framework, this
        String datsetUri =
                gdasType.getFramework()
                        .getFrameworkURI()
                        .concat("?DatasetURI=")
                        .concat(gdasType.getFramework().getDataset().getDatasetURI());
        return datsetUri; // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public String getDatasetName() {
        return gdasType.getFramework().getDataset().getTitle();
    }

    @Override
    public String getGeoKeyField() {
        ColumnType2 column =
                (ColumnType2)
                        gdasType.getFramework()
                                .getDataset()
                                .getColumnset()
                                .getFrameworkKey()
                                .getColumn()
                                .get(0);
        /*ColumnType column = (ColumnType)gdasType.getFramework().getFrameworkKey().getColumn().get(0);*/
        return column.getName();
    }

    @Override
    public String getOrganization() {
        return gdasType.getFramework().getOrganization();
    }

    @Override
    public Date getReferenceDate() {
        // Hay que ver cómo se parsea el valor de ReferenceDate para asignarlo al tipo Date que se
        // requiere
        return new Date(); // gdasType.getFramework().getReferenceDate().;
    }

    @Override
    public String getVersion() {
        return gdasType.getFramework().getVersion();
    }

    @Override
    public String getDocumentation() {
        return gdasType.getFramework().getDocumentation();
    }

    @Override
    public TJSDatasource getTJSDatasource() {
        TJSDataStore tjsDataStore = getDataStore().getTJSDataStore(new NullProgressListener());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(JDBC_TJSDataStoreFactory.DATASOURCENAME.key, tableName);
        return tjsDataStore.getDatasource(tableName, params);
    }

    HashMap<String, ColumnInfo> columns = new HashMap<String, ColumnInfo>();

    @Override
    public List<ColumnInfo> getColumns() {
        if (columns.isEmpty()) {
            for (int index = 0;
                    index
                            < gdasType.getFramework()
                                    .getDataset()
                                    .getColumnset()
                                    .getAttributes()
                                    .getColumn()
                                    .size();
                    index++) {
                ColumnType1 column =
                        (ColumnType1)
                                gdasType.getFramework()
                                        .getDataset()
                                        .getColumnset()
                                        .getAttributes()
                                        .getColumn()
                                        .get(index);
                // if the columnName starts with a number, add a character in front for XML
                // encoding. XML does not allow numbrs as first chracter for an element name
                columns.put(getSafeColumnName(column.getName()), new GDAS_ColumnInfo(column));
            }
        }
        return new ArrayList<ColumnInfo>(columns.values());
    }

    public String getSafeColumnName(String columnName) {
        columnName = columnName.toUpperCase();
        columnName = columnName.replaceAll("[^A-Z0-9_]", "");
        // and cut off ythe length?
        // TODO: if the columnname already exists, then add a number
        if (columnName.length() >= 32) {
            columnName = columnName.substring(0, 31);
        }
        return columnName;
    }

    @Override
    public ColumnInfo getColumn(String name) {
        return columns.get(name);
    }

    @Override
    public String getDefaultStyle() {
        DatasetInfo hostedDataset =
                tjsCatalog.getDatasetByUri(gdasType.getFramework().getDataset().getDatasetURI());
        if (hostedDataset != null) {
            return hostedDataset.getDefaultStyle();
        }
        return null;
    }

    @Override
    public boolean getAutoJoin() {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getId() {
        return ""; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(String id) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * AnteaGroup : We need this for set a name contain the layer stylename. When you joindata with
     * same getdata but different style, we want 2 layers. you need to override the name with the
     * stylename. I choose this solution for do this. Another solution is to add the new style to
     * the same layer ? Maybe can be a good alternative.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return gdasType.getFramework()
                .getDataset()
                .getAbstract()
                .toString(); // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(
                getName()); // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public void accept(TJSCatalogVisitor visitor) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadDefault() {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TJSCatalog getCatalog() {
        return tjsCatalog;
    }
}
