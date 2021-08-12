/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmx.iderc.geoserver.tjs.data.jdbc.postgre;

import gmx.iderc.geoserver.tjs.data.TJSDataStore;
import gmx.iderc.geoserver.tjs.data.jdbc.JDBC_TJSDataStoreFactory;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.IOException;
import java.util.Map;

/**
 * @author root
 */
public class Postgre_TJSDataStoreFactory extends JDBC_TJSDataStoreFactory {

    @Override
    protected String getDatabaseID() {
        return "PostgreSQL";
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    public TJSDataStore createDataStore(Map params) throws IOException {
        BasicDataSource datasource = createDataSource(params);
        params.put(DATASOURCE.key, datasource);
        Postgre_TJSDataStore pgDataStore = new Postgre_TJSDataStore(params, this);
        return pgDataStore;
    }

    public String getDescription() {
        return "PostgreSQL database";
    }

    @Override
    protected String getJDBCUrl(Map params) throws IOException {
        String host = (String) HOST.lookUp(params);
        String db = (String) DATABASE.lookUp(params);
        int port = ((Integer) PORT.lookUp(params)).intValue();
        
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        
        String schema = (String) SCHEMA.lookUp(params);
        if (schema != null){
            url += "?currentSchema=" + schema;
        }
        return url;
    }

}
