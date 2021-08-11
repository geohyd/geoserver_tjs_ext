/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmx.iderc.geoserver.tjs.response;

import net.opengis.tjs10.DescribeFrameworksType;
import org.geoserver.ows.Response;
import org.geoserver.ows.util.OwsUtils;
import org.geoserver.platform.Operation;
import org.geoserver.platform.ServiceException;
import org.geotools.xml.transform.TransformerBase;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @author root
 */
public class DescribeDatasetsResponse extends Response {

    public DescribeDatasetsResponse() {
        super(TransformerBase.class);
    }

    @Override
    public boolean canHandle(Operation operation) {
        return "DescribeDatasets".equalsIgnoreCase(operation.getId())
                       && operation.getService().getId().equals("tjs");
    }

    @Override
    public String getMimeType(Object value, Operation operation) throws ServiceException {
        DescribeFrameworksType request = (DescribeFrameworksType) OwsUtils.parameter(operation.getParameters(), DescribeFrameworksType.class);

//        if ((request != null) && (request.getAcceptFormats() != null)) {
//            //look for an accepted format
//            List formats = request.getAcceptFormats().getOutputFormat();
//
//            for (Iterator f = formats.iterator(); f.hasNext();) {
//                String format = (String) f.next();
//
//                if (format.endsWith("/xml")) {
//                    return format;
//                }
//            }
//        }

        //default
        return "application/xml";
    }

    @Override
    public void write(Object value, OutputStream output, Operation operation) throws IOException, ServiceException {
        TransformerBase tx = (TransformerBase) value;
        try {
            tx.transform(operation.getParameters()[0], output);
        } catch (TransformerException e) {
            throw (IOException) new IOException().initCause(e);
        }
    }
}
