/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gmx.iderc.geoserver.tjs;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** @author root */
public class GmxTJS {

    public GmxTJS() {}

    public void findRoute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.getOutputStream().println("find route service");
    }
}
