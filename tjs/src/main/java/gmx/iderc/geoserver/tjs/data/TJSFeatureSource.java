package gmx.iderc.geoserver.tjs.data;

import java.io.IOException;
import java.util.logging.Logger;
import org.geotools.api.data.*;
import org.geotools.api.data.Query;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.logging.Logging;

/**
 * Created with IntelliJ IDEA. User: thijsb Date: 3/25/14 Time: 11:45 AM To change this template use
 * File | Settings | File Templates.
 */
public class TJSFeatureSource
        //        extends AbstractFeatureSource { // not ContentFeatureSource  ?
        // AbstractFeatureSource
        extends ContentFeatureSource {

    static final Logger LOGGER = Logging.getLogger("gmx.iderc.geoserver.tjs.data.TJSFeatureSource");

    TJS_1_0_0_DataStore store;
    String typeName;
    SimpleFeatureType featureType;
    // private static final SimpleFeatureType featureType = createSchema();
    ReferencedEnvelope cacheBounds = null;

    public TJSFeatureSource(ContentEntry entry, Query query) {
        super(entry, query);
        this.store = (TJS_1_0_0_DataStore) entry.getDataStore();
        this.typeName = query.getTypeName();

        this.queryCapabilities =
                new QueryCapabilities() {
                    public boolean isUseProvidedFIDSupported() {
                        return false;
                    }
                };
    }

    public void setFeatureType(SimpleFeatureType featureType) {
        this.featureType = featureType;
        this.schema = featureType;
    }

    public TJS_1_0_0_DataStore getDataStore() {
        return this.store;
    }

    @Override
    protected SimpleFeatureType buildFeatureType() throws IOException {
        return featureType;
    }

    protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(Query query)
            throws IOException {
        // Note we ignore 'query' because querying/filtering is handled in superclasses.
        // get the reader from the TJSFeatureReader

        // just support one featuretype?

        return getDataStore().getFeatureReader(query.getTypeName());
        // return new TJSFeatureReader( getState() );
    }

    /**
     * Implementation that generates the total bounds (many file formats record this information in
     * the header)
     */
    protected ReferencedEnvelope getBoundsInternal(Query query) throws IOException {
        // Just ignore the query for now
        // TODO: implement
        return this.getBounds_override();
    }

    public final ReferencedEnvelope getBounds_override() throws IOException {
        CoordinateReferenceSystem crs = getCRS();
        // Thijs: using the cacheBounds seems to help in the heap space errors
        if (crs != null) {
            if (cacheBounds != null) {
                return cacheBounds;
            } else {
                ReferencedEnvelope bounds = new ReferencedEnvelope(crs);
                FeatureReader<SimpleFeatureType, SimpleFeature> featureReader =
                        store.getFeatureReader(this.typeName);
                try {
                    while (featureReader.hasNext()) {
                        SimpleFeature feature = featureReader.next();
                        bounds.include(feature.getBounds());
                    }
                } finally {
                    featureReader.close();
                    // ANTEA JDU : close for preserve idle connection
                    store.closeTransaction(this.typeName);
                }
                cacheBounds = bounds;
                return bounds;
            }
        } else {
            return null;
        }
    }

    public final CoordinateReferenceSystem getCRS() {
        CoordinateReferenceSystem crs = getSchema().getCoordinateReferenceSystem();
        // TODO: the same as the declared crs from the framework?
        if (crs == null) {
            // first try the declared CRS, if that is not available, try to use the native CRS
            try {
                crs = store.getFrameworkInfo().getFeatureType().getCRS();
                if (crs == null) {
                    crs =
                            store.featureDataStore
                                    .getSchema(
                                            store.getFrameworkInfo()
                                                    .getFeatureType()
                                                    .getNativeName())
                                    .getCoordinateReferenceSystem();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                LOGGER.severe(ex.getMessage());
            }
        }
        return crs;
    }

    // Return the SRS string as declared in the featuretype of the framework
    public final String getSRS() {
        return store.getFrameworkInfo().getFeatureType().getSRS();
    }

    protected int getCountInternal(Query query) throws IOException {
        SimpleFeatureCollection sfc = this.getFeatures(query);
        int count = 0;
        SimpleFeatureIterator iter = sfc.features();
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        iter.close();
        return count;
    }
}
