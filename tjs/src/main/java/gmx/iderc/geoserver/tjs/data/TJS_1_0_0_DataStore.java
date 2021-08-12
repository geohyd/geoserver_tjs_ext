package gmx.iderc.geoserver.tjs.data;

import gmx.iderc.geoserver.tjs.catalog.ColumnInfo;
import gmx.iderc.geoserver.tjs.catalog.DatasetInfo;
import gmx.iderc.geoserver.tjs.catalog.FrameworkInfo;
import gmx.iderc.geoserver.tjs.catalog.TJSCatalog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.geotools.data.*;
import org.geotools.data.Query;
import org.geotools.data.store.ContentDataStore;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

/**
 * Created with IntelliJ IDEA. User: capote Date: 9/22/12 Time: 11:07 AM To change this template use
 * File | Settings | File Templates.
 */
public class TJS_1_0_0_DataStore
        //    extends AbstractDataStore { // AbstractDataStore or ContentDataStore?
        extends ContentDataStore {

    static final Logger LOGGER =
            Logging.getLogger("gmx.iderc.geoserver.tjs.data.TJS_1_0_0_DataStore");

    // WFSDataStore wfsDataStore;
    DataStore featureDataStore;
    FrameworkInfo frameworkInfo;
    TJSCatalog catalog;
    HashMap<String, SimpleFeatureType> storeTypeNames = new HashMap<String, SimpleFeatureType>();
    TJSFeatureSource myTJSFeatureSource = null;

    // HashMap<String, FeatureReader<SimpleFeatureType, SimpleFeature>> featureReaders = new
    // HashMap<String, FeatureReader<SimpleFeatureType, SimpleFeature>>();
    HashMap<String, Transaction> transactions = new HashMap<String, Transaction>();

    // This datastore does not allow writing features
    protected boolean isWritable = false;

    public List<Name> createTypeNames() throws IOException {
        List<Name> typeNames = new ArrayList<Name>();
        if (catalog != null && frameworkInfo != null) {
            List<DatasetInfo> datasets = catalog.getDatasetsByFramework(frameworkInfo.getId());
            for (DatasetInfo dataset : datasets) {
                typeNames.add(dataset.getQualifiedName());
            }
        }
        return typeNames;
    }

    public FrameworkInfo getFrameworkInfo() {
        return frameworkInfo;
    }

    public TJS_1_0_0_DataStore(
            TJSCatalog catalog, DataStore featureDataStore, FrameworkInfo frameworkInfo) {
        this.catalog = catalog;
        this.featureDataStore = featureDataStore;
        this.frameworkInfo = frameworkInfo;
    }

    /*public String[] getTypeNames() throws IOException {
        List<String> typeNames = new ArrayList<String>();
        if (catalog != null && frameworkInfo != null) {
            List<DatasetInfo> datasets = catalog.getDatasetsByFramework(frameworkInfo.getId());
            for (DatasetInfo dataset : datasets) {
                typeNames.add(dataset.getName());
            }
        }
        String[] typeNamesArray = new String[typeNames.size()];
        typeNames.toArray(typeNamesArray);
        return typeNamesArray;
    }*/

    // interface method of AbstractDataStore
    public String[] getFeatureTypes() throws IOException {
        return this.getTypeNames();
    }

    public SimpleFeatureType getSchema_override(String typeName) {
        if (storeTypeNames.containsKey(typeName)) {
            return storeTypeNames.get(typeName);
        }

        if (frameworkInfo != null) {
            String wfsTypeName = frameworkInfo.getFeatureType().getNativeName();
            SimpleFeatureType wfsFeatureType = null;
            try {
                wfsFeatureType = featureDataStore.getSchema(wfsTypeName);
            } catch (IOException ex) {
                LOGGER.severe(
                        "TJS_1_0_0_DataStore ERROR on featureDataStore.getSchema(wfsTypeName) : ");
            }

            SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
            featureTypeBuilder.setName(typeName);

            if (wfsFeatureType != null) {
                featureTypeBuilder.addAll(wfsFeatureType.getAttributeDescriptors());
            }
            DatasetInfo datasetInfo =
                    catalog.getDatasetByFramework(frameworkInfo.getId(), typeName);

            for (ColumnInfo column : datasetInfo.getColumns()) {
                Class attrClass = String.class;
                // TODO: this class mapping is a workaround to avoid cutting off Strings (because
                // the SQL Class Binding seems to be java.lang.Character)
                if (column.getSQLClassBinding().equals((Class) java.lang.Character.class)) {
                    attrClass = String.class;
                } else {
                    // assume the rest of the classes and bindings is correct now..
                    attrClass = column.getSQLClassBinding();
                }
                featureTypeBuilder.add(column.getName(), attrClass);
            }
            // TODO: deal with the namespace. Set it in the current namespace? Is this needed?
            featureTypeBuilder.setNamespaceURI("");
            SimpleFeatureType newFt = featureTypeBuilder.buildFeatureType();
            storeTypeNames.put(typeName, newFt);
            return storeTypeNames.get(typeName);
        } else {
            // TODO: how to deal with this?
            return null;
        }
    }

    // getSchema for a Name
    public SimpleFeatureType getSchema(Name name) throws IOException {
        // TODO: is local part okay? For now it works, not sure what happens if we have the same
        // typenames, in different namespaces
        return getSchema(name.getLocalPart());
    }

    // or  create a feature source here
    @Override
    public TJSFeatureSource getFeatureSource(String typeName) {
        // return new TJSFeatureSource(this.getEntry(typeName), new Query(typeName));
        if (this.myTJSFeatureSource != null) {
            return this.myTJSFeatureSource;
        }
        // return new TJSFeatureSource(this.getEntry(new NameImpl(typeName)), new Query(typeName));
        NameImpl name = new NameImpl(typeName);
        ContentEntry entry = new ContentEntry(this, name);
        SimpleFeatureType featureType = getSchema_override(typeName);
        TJSFeatureSource TJSFeatureSrc = new TJSFeatureSource(entry, new Query(typeName));
        TJSFeatureSrc.setFeatureType(featureType);
        this.entries.put(new NameImpl(typeName), entry);
        boolean found = this.entries.containsKey(name);
        boolean unqualifiedSearch = name.getNamespaceURI() == null;
        this.myTJSFeatureSource = TJSFeatureSrc;
        return TJSFeatureSrc;
    }

    @Override
    public ContentFeatureSource getFeatureSource(Name typeName, Transaction tx) throws IOException {
        return getFeatureSource(typeName.getLocalPart());
        // ContentEntry entry = ensureEntry(typeName);

        // ContentFeatureSource featureSource = createFeatureSource(entry);
        // featureSource.setTransaction(tx);

        //        if ( tx != Transaction.AUTO_COMMIT ) {
        //            //setup the transaction state
        //            synchronized (tx) {
        //                if ( tx.getState( typeName ) == null ) {
        //                    tx.putState( typeName, createTransactionState(featureSource) );
        //                }
        //            }
        //        }

        // return featureSource;
    }

    // dummy method?
    protected ContentFeatureSource createFeatureSource(ContentEntry entry) throws IOException {
        TJSFeatureSource TJSFeatureSrc =
                new TJSFeatureSource(entry, new Query(entry.getTypeName()));
        TJSFeatureSrc.setFeatureType(getSchema_override(entry.getTypeName()));
        return TJSFeatureSrc;
    }

    // ANTEA JDU : Used for close the transation for solved a idle connection
    public void closeTransaction(String typeName) {
        if (transactions.containsKey(typeName)) {
            if (transactions.containsKey(typeName)) {
                Transaction myTrans = transactions.get(typeName);
                try {
                    myTrans.close();
                } catch (IOException io) {
                    LOGGER.severe("cannot close transaction for: " + typeName);
                } finally {
                    transactions.remove(typeName);
                }
            }
        }
    }

    // Thijs: was protected, but need it elsewhere.
    // Is this appropriate?  Should this be implemented somewhere else?
    // TODO: create a hashmap, containing cached featurereaders for each typeName?
    // ANTEA JDU : No, we cached the Transaction instead of featurereaders
    // for close it after make all hasNext in TJSFeatureSource
    // OtherWhise, idle connection blocking
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName)
            throws IOException {

        /* if (featureReaders.containsKey(typeName)) {
        	// try to connect
        	FeatureReader<SimpleFeatureType, SimpleFeature> fr = featureReaders.get(typeName);
        	if (fr != null) {
         	LOGGER.severe("featurereader for: " + typeName);
         	LOGGER.severe("Using cached featureReader... ");
         	return featureReaders.get(typeName);
        	}
        } */
        String wfsTypeName = frameworkInfo.getFeatureType().getNativeName();
        // Transaction myTrans = new DefaultTransaction();
        Transaction myTrans = null;
        if (transactions.containsKey(typeName) && transactions.get(typeName) != null) {
            myTrans = transactions.get(typeName);
        } else {
            myTrans = new DefaultTransaction();
            transactions.put(typeName, myTrans);
        }
        FeatureReader<SimpleFeatureType, SimpleFeature> wfsFeatureReader =
                featureDataStore.getFeatureReader(new Query(wfsTypeName), myTrans);
        DatasetInfo datasetInfo = catalog.getDatasetByFramework(frameworkInfo.getId(), typeName);
        TJSFeatureReader featureReader =
                new TJSFeatureReader(getSchema(typeName), wfsFeatureReader, datasetInfo);
        // featureReaders.put(typeName, featureReader);
        return featureReader;
    }
}
