/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmx.iderc.geoserver.tjs.data.jdbc.hsql;

import gmx.iderc.geoserver.tjs.data.TJSDataStore;
import gmx.iderc.geoserver.tjs.data.jdbc.JDBC_TJSDataStoreFactory;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.IOException;
import java.util.Map;

/**
 * @author root
 */
public class HSQLDB_TJSDataStoreFactory extends JDBC_TJSDataStoreFactory {

    @Override
    protected String getDatabaseID() {
        return "HSQLDB";
    }

    @Override
    protected String getDriverClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    public TJSDataStore createDataStore(Map params) throws IOException {
        BasicDataSource datasource = createDataSource(params);
        params.put(DATASOURCE.key, datasource);
        HSQLDB_TJSDataStore pgDataStore = new HSQLDB_TJSDataStore(params, this);
        return pgDataStore;
    }

    public String getDescription() {
        return "HSQLDB database";
    }

    @Override
    protected String getJDBCUrl(Map params) throws IOException {
        //String host = (String) HOST.lookUp(params);
        String db = (String) DATABASE.lookUp(params);
        //int port = ((Integer) PORT.lookUp(params)).intValue();
        //jdbc:hsqldb:file:</path/to/file>
        return "jdbc:hsqldb:file:/" + db;
    }

}
