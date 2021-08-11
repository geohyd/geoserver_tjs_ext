package org.geotools.tjs.bindings;


import net.opengis.tjs10.DescribeDataRequestType;
import net.opengis.tjs10.Tjs10Factory;
import org.geotools.tjs.TJS;
import org.geotools.xml.AbstractComplexEMFBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/tjs/1.0:DescribeDataRequestType.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:complexType ecore:name="DescribeDataRequestType" name="DescribeDataRequestType"&gt;
 *      &lt;xsd:attribute ref="xlink:href" use="required"/&gt;
 *  &lt;/xsd:complexType&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class DescribeDataRequestTypeBinding extends AbstractComplexEMFBinding {

    public DescribeDataRequestTypeBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.DescribeDataRequestType;
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
