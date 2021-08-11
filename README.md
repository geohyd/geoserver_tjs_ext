This is a fork of the `geoserver tjs community extension` based on the excellent work of https://github.com/thijsbrentjens/geoserver/tree/tjs_2.2.x/

Table Joining Services extension
=====================
The community plugin TJS will add support for Table Joining Services v 1.0.0 to Geoserver. Code is based on code of GeoCuba/GeoMix, imported by Thijs Brentjens (for Geonovum) on request of GeoCuba/GeoMix. Support for WFS output and refactoring to imrpove WMS support has been done for Geonovum.

This extension is under development. Please read this carefully. Any feedback is welcome.

Features
========
The TJS extension adds TJS support to Geoserver (2.7.x at the moment). It adds TJS operations to join data and adds web administration options. The web admin allows for management / configuration of:
- spatial frameworks (spatial datasets to join data on) and non-spatial tabular data to be published in GDAS encoding
- pre-configured joins.

The TJS OGC webservice interface of this extension supports almost all TJS operations. It allows for example for:
- GetCapabilities
- getting data in GDAS encoding
- creating joins using JoinData
- output of joins as WMS layers, WFS featuretypes. The TJS extension has (some) support for SLD, but this is untested currently

The above features have initially been developed by GeoCuba/GeoMix. Geonovum has added WFS support and has rewritten parts of the source code.

Work in Progress
==========
The TJS extension is still under development. It has not been tested extensively yet. Testers on different systems / platforms are welcomed. Please contact Thijs with any questions: 

t dot brentjens at geonovum dot nl.

Among things to do are:

- support for management of joined output files and removing joins / clearing the cache after a pre-configured time
- user manual
- fix tests for the postgres connection of data stores, make it file-based

Related projects
===========
For demonstraton and test purposes a basic tool is developed. This tool allows users to upload a CSV-file and join that to a spatial framework (spatial dataset)

See https://github.com/joostvenema/tjs-demonstrator for the demonstrator

The Geodetic institute of Slovenia has created support for TJS in it's STAGE client. A Javascript version is available here: https://bitbucket.org/ginst/jstjs/

Installation
=====================
The libraries for the TJS extension can be build from the community/tjs/ directory, see Compilation below.

Compilation
=====================
Follow the general build instructions of Geoserver.
To build the TJS plugin seperately, go to the directory 

```
src/community/tjs/
```

and build using the regular command:

```
mvn clean install -DskipTests
```

Note: tests will probably fail at the moment because of Postgres connections in some of the tests. Skipping the tests will help for now. This is a workaround, improve tests and test coverage is still necessary.

After compilation, copy the following jars to {geoserver}/WEB-INF/lib/ :

* net.opengis.tjs/target/net.opengis.tjs-{gt.version}.jar
* tjs/target/tjs-core-{tjs.version}.jar
* tjsdata/target/tjsdata-{tjs.version}.jar
* tjs-web/target/tjs-web-{tjs.version}.jar
* xsd-tjs/target/gt-xsd-tjs-{gt.version}.jar


Using Eclipse
=====================
To use the TJS plugin in Eclipse, follow the general guidelines of GeoServer but don't import the projects in Eclipse yet, http://docs.geoserver.org/2.6.x/en/developer/quickstart/index.html

Before importing the projects, you need to configure the TJS plugin for Eclipse as well:

To add the TJS plugin:

1. with a command line, setup the Eclipse project for TJS:

cd src/community/tjs/

mvn eclipse:eclipse

2. Now import the project Geoserver from file, as described http://docs.geoserver.org/2.6.x/en/developer/quickstart/index.html. You should see 

3. Run configuration:
After the first Run configuration with Eclipse, the TJS plugin needs to be added manually to the Run configuration. You can do this by going to Run > Run configurations...
There, add the TJS-projects to the Classpath, as projects:

* net.opengis.tjs
* tjs
* tjsdata
* tjs-web
* xsd-tjs

4. Running Geoserver with TJS should work now. NOTE: at this moment, there seems to be an issue using Eclipse's Run functionality, especially with the data dir configuration for TJS.





