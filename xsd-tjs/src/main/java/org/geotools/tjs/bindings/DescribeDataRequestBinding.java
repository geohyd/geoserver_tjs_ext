package org.geotools.tjs.bindings;


import net.opengis.tjs10.DescribeDataRequestType;
import net.opengis.tjs10.Tjs10Factory;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the element http://www.opengis.net/tjs/1.0:DescribeDataRequest.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:element name="DescribeDataRequest" type="tjs:DescribeDataRequestType"&gt;
 *      &lt;xsd:annotation&gt;
 *          &lt;xsd:documentation&gt;URL reference to the DescribeData request for this dataset.&lt;/xsd:documentation&gt;
 *      &lt;/xsd:annotation&gt;
 *  &lt;/xsd:element&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class DescribeDataRequestBinding extends AbstractComplexEMFBinding {

    public DescribeDataRequestBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.DescribeDataRequest;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return DescribeDataRequestType.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
            throws Exception {

        //TODO: implement and remove call to super
        return super.parse(instance, node, value);
    }

}
