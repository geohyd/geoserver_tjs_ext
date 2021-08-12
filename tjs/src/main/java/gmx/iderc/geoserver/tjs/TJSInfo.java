/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs;

import java.util.Arrays;
import java.util.List;
import org.geoserver.config.ServiceInfo;

/** @author root */
public interface TJSInfo extends ServiceInfo {

    static enum Version {
        V_10;

        public static Version get(String v) {
            if (v.startsWith("1.0")) {
                return V_10;
            }
            // if ( v.startsWith( "1.1") ) {
            //      return V_11;
            // }
            return null;
        }
    };

    static enum Operation {
        GETCAPABILITIES {
            public int getCode() {
                return 0;
            }
        },
        DESCRIBEFRAMEWORKS {
            public int getCode() {
                return 1;
            }
        },
        DESCRIBEDATASETS {
            public int getCode() {
                return 2;
            }
        },
        DESCRIBEDATA {
            public int getCode() {
                return 4;
            }
        },
        GETDATA {
            public int getCode() {
                return 8;
            }
        },
        DESCRIBEJOINTABILITIES {
            public int getCode() {
                return 16;
            }
        },
        DESCRIBEKEY {
            public int getCode() {
                return 32;
            }
        },
        JOINTDATA {
            public int getCode() {
                return 64;
            }
        };

        public abstract int getCode();
    }

    static enum ServiceLevel {
        BASIC {
            public int getCode() {
                return 1;
            }

            public List<Operation> getOps() {
                return Arrays.asList(
                        Operation.GETCAPABILITIES,
                        Operation.DESCRIBEFRAMEWORKS,
                        Operation.DESCRIBEKEY,
                        Operation.DESCRIBEDATASETS,
                        Operation.DESCRIBEDATA,
                        Operation.GETDATA);
            }
        },
        COMPLETE {
            public int getCode() {
                return 15;
            }

            public List<Operation> getOps() {
                return Arrays.asList(
                        Operation.GETCAPABILITIES,
                        Operation.DESCRIBEFRAMEWORKS,
                        Operation.DESCRIBEKEY,
                        Operation.DESCRIBEDATASETS,
                        Operation.DESCRIBEDATA,
                        Operation.GETDATA,
                        Operation.DESCRIBEJOINTABILITIES,
                        Operation.JOINTDATA);
            }
        };

        public abstract int getCode();

        public abstract List<Operation> getOps();

        boolean contains(ServiceLevel other) {
            return getOps().containsAll(other.getOps());
        }

        public static ServiceLevel get(int code) {
            for (ServiceLevel s : values()) {
                if (s.getCode() == code) {
                    return s;
                }
            }

            return null;
        }
    };

    /**
     * Get the flag that determines the encoding of the TJS schemaLocation. True if the TJS
     * schemaLocation should refer to the canonical location, false if the TJS schemaLocation should
     * refer to a copy served by GeoServer.
     */
    boolean isCanonicalSchemaLocation();

    /**
     * Set the flag that determines the encoding of the TJS schemaLocation. True if the TJS
     * schemaLocation should refer to the canonical location, false if the TJS schemaLocation should
     * refer to a copy served by GeoServer.
     */
    void setCanonicalSchemaLocation(boolean canonicalSchemaLocation);

    /** The level of service provided by the TJS. */
    ServiceLevel getServiceLevel();

    /** Sets the level of service provided by the TJS. */
    void setServiceLevel(ServiceLevel serviceLevel);

    public String getTjsServerBaseURL();

    public void setTjsServerBaseURL(String tjsServerBaseURL);
}
