package gmx.iderc.geoserver.tjs.web.framework;

import java.io.Serializable;

/**
 * Make this class for create model for the name of the Framework when you create or edit. I choose
 * to set the same name of the selected associated WMS for create a frameworkURI match with the
 * frameworkURI of the wms service source. Thanks to this, when we send a DescribeFrameworks
 * request, with the frameworkURI you can find the Service. Otherwise, I don't find who can I do
 * this. Maybe this is a mistake to do this ?
 */
public class FrameworkNameModel implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
