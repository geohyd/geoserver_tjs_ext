/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmx.iderc.geoserver.tjs.data.jdbc;

import gmx.iderc.geoserver.tjs.data.TJSAbstractDataStore;
import gmx.iderc.geoserver.tjs.data.TJSDatasource;
import org.apache.commons.dbcp.BasicDataSource;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author root
 */
public abstract class JDBC_TJSDataStore extends TJSAbstractDataStore {

    static final Logger LOGGER = Logging.getLogger("gmx.iderc.geoserver.tjs.data.jdbc");

    Map params;
    BasicDataSource dataSource;
    JDBC_TJSDataStoreFactory factory;

    Connection connection = null;

    String defaultSchemaName = "public";

    public JDBC_TJSDataStore(Map params, JDBC_TJSDataStoreFactory factory) {
        super(params, factory);
        this.params = params;
        this.factory = factory;
        init();
    }

    private void init() {
        try {
            dataSource = (BasicDataSource) factory.DATASOURCE.lookUp(params);
            if (dataSource == null) {
                dataSource = factory.createDataSource(params);
                getConnection();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    private Connection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            try {
                connection = dataSource.getConnection();
            } catch (Exception error) {
                LOGGER.log(Level.SEVERE, null, error);
            }
        }
        return connection;
    }

    @Override
    public String[] getAllAvaliableDatasources() {
        ArrayList<String> tableList = new ArrayList<String>();
        ResultSet tables = null;
        
        try {
            String schemaName = null;
            String dbtype = "postgres"; // FIX IT -> find the dbtype of the current DS
            try{
                // Check if the DataStore have a data source name.
                // If it not empty, this is a user datastore (postgres)
                // If empty, this is a gdas datastore.
                //System.out.println("getAllAvaliableDatasources - params : " + params);
                //System.out.println("getAllAvaliableDatasources - Data Source Name : " + (String) params.get("Data Source Name"));
                String dataSourceName = (String) params.get("Data Source Name");
                if(dataSourceName != null){ // TODO : Need to fix it. Need a more consistente solution.
                    String paramSchemaName = (String) JDBC_TJSDataStoreFactory.SCHEMA.lookUp(params);
                    if(paramSchemaName != null && paramSchemaName instanceof String){
                        schemaName = paramSchemaName;
                    }else{
                        schemaName = defaultSchemaName;
                    }
                }
            }catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Cannot read SchemaName configuration", ex);
                schemaName = null;
            }
            //tables = getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE", "VIEW"});
            tables = getConnection().getMetaData().getTables(null, schemaName, null, new String[]{"TABLE", "VIEW"});
            while (tables.next()) {
                int ischema = tables.findColumn("TABLE_SCHEM");
                String sschema = tables.getString(ischema);
                int iname = tables.findColumn("TABLE_NAME");
                String sname = tables.getString(iname);
                // Thijs Brentjens: to get the featuretypename working, without the database schema, don't add the schema to the featuretypename
                // Therefore: don't add the schema to the tablelist, but just add the name
                //
                // TODO: improve handling of the schema in tablenames for the GDAS cache?
                /*
                if (sschema == null){
                    // tableList.add(sname.toUpperCase());
                    tableList.add(sname);
                }else{
                    tableList.add(sschema + "." + sname);
                } */
                tableList.add(sname);
            }
            //tables.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                tables.close();
            }catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }catch(Exception ex){
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return tableList.toArray(new String[tableList.size()]);
    }

    protected TJSDatasource createDataSource(Map params) throws Exception {
        JDBC_TJSDatasource ds = new JDBC_TJSDatasource(dataSource, params);
        return ds;
    }

    @Override
    public TJSDatasource getDatasource(String name, Map params) {
        try {
            String paramsDsName = (String)JDBC_TJSDataStoreFactory.DATASOURCENAME.lookUp(params);
            if (!paramsDsName.equalsIgnoreCase(name)){
                params.put(JDBC_TJSDataStoreFactory.DATASOURCENAME.key, name);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, null, ex);
            params.put(JDBC_TJSDataStoreFactory.DATASOURCENAME.key, name);
        }
        return super.getDatasource(name, params);
    }

}
