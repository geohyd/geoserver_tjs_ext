/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs.data;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @author root
 */
public class TJSDataStoreFinderTest extends TestCase {

    public TJSDataStoreFinderTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetDataStore() throws Exception {
    }

    public void testGetAllDataStores() {
        /*for (Iterator<TJSDataStoreFactorySpi> stores = TJSDataStoreFinder.getAllDataStores(); stores.hasNext(); ) {
            TJSDataStoreFactorySpi store = stores.next();
            System.out.println(store.getDisplayName());
            System.out.println(store.getDescription());
        }*/
        Stream<TJSDataStoreFactorySpi> stores = TJSDataStoreFinder.getAllDataStores();
        stores.forEach(item->{
            System.out.println(item.getDisplayName());
            System.out.println(item.getDescription());
        });
    }

    public void testGetAvailableDataStores() {
    }

    public void testScanForPlugins() {
    }

    public void testReset() {
    }

}
