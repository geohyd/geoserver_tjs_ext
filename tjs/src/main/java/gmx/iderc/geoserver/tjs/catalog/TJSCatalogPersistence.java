/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs.catalog;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import gmx.iderc.geoserver.tjs.catalog.impl.TJSCatalogImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;

/** @author root */
public class TJSCatalogPersistence {

    static final Logger logger = Logger.getLogger(TJSCatalogPersistence.class.getName());

    private static File getPersistenceFile(File geoserverDD) {
        String strCfgFile = geoserverDD.toString() + "/gmxtjs";
        File cfgFile = new File(strCfgFile);
        if (!cfgFile.exists()) {
            cfgFile.mkdir();
        }
        strCfgFile += "/config.xml";
        cfgFile = new File(strCfgFile);
        return cfgFile;
    }

    public static File getGeoserverDataDirectory() {
        // TODO: how to get the Geoserverdatadriectory properly? Seems to be changed for Geoserver
        // 2.6.x or earlier
        // return GeoserverDataDirectory.getGeoserverDataDirectory();
        Catalog cat = (Catalog) GeoServerExtensions.bean("catalog");
        GeoServerDataDirectory dd = new GeoServerDataDirectory(cat.getResourceLoader());
        return dd.root();
    }

    public static TJSCatalog load(File dataDirectory) {
        try {
            File persistenceFile = getPersistenceFile(dataDirectory);
            logger.log(Level.INFO, "Cargando configuracion desde: " + persistenceFile.toString());
            if (!persistenceFile.exists()) {
                logger.log(
                        Level.INFO,
                        "No existe archivo de configuracion, escribiendo valores por defecto");
                persistenceFile.createNewFile();
                TJSCatalogImpl catalog = new TJSCatalogImpl();
                // catalog.loadDefault();
                save(catalog, persistenceFile);
                return catalog;
            } else {
                FileInputStream fis;
                fis = new FileInputStream(persistenceFile);
                XStream xs = new XStream();
                xs.addPermission(AnyTypePermission.ANY);
                String xmlStringContent = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
                try {
                    TJSCatalog catalog = (TJSCatalog) xs.fromXML(xmlStringContent);
                    fis.close();
                    catalog.init();
                    return catalog;
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Ocurrio una excepcion", ex);
                    throw ex;
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Ocurrio una excepcion", ex);
        }
        return null;
    }

    public static TJSCatalog load() {
        return load(getGeoserverDataDirectory());
    }

    public static void save(TJSCatalog catalog, File persistenceFile) {
        try {
            FileWriter fw = new FileWriter(persistenceFile, false);
            XStream xs = new XStream();
            String xml = xs.toXML(catalog);
            fw.write(xml);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(TJSCatalogPersistence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void save(TJSCatalog catalog) {
        File pf = getPersistenceFile(getGeoserverDataDirectory());
        save(catalog, pf);
    }
}
