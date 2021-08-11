Example data for TJS
====================

If the sample data of Geoserver is available (most notably the states of the USA data), you could try the TJS by following these steps:

1) add the directory "gmxtjs" to the geoserver data-dir. This directory contains an example configruration for the states layer as TJS SpatialFramework
2) (re)start geoserver

After reloading, the TJS should be available. 

Test if the service publishes Capabilities:
http://localhost:8080/geoserver/tjs?request=GetCapabilities

Check if the framework StatesUSA is available:
http://localhost:8080/geoserver/tjs?request=DescribeFrameworks

The response XML should contain a Framework element, containing data on the Framework with URI: http://www.openplans.org/topp/StatesUSA

Explore the available keys:
http://localhost:8080/geoserver/tjs?request=DescribeKey&frameworkURI=http://www.openplans.org/topp/StatesUSA

If you have GDAS encoded data (for example some statistics, without geometry, in a tabular format), you could join this data to the spatial framework.
This GDAS format is specifically designed for TJS (but could be used for other things as well). If you have a CSV file, you need to convert that to GDAS.

How to do this, is described later. 

JoinData request demo:

http://localhost:8080/geoserver/tjs?request=JoinData&FrameworkURI=http://www.openplans.org/topp/StatesUSA&GetdataURL=http://localhost:8080/geoserver/www/GDAS-sampledata.xml
