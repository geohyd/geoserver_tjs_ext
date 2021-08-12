package org.geotools.tjs.bindings;


import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractSimpleBinding;
import org.geotools.xsd.InstanceComponent;

import javax.xml.namespace.QName;

/**
 * Binding object for the element http://www.opengis.net/tjs/1.0:Organization.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:element name="Organization" type="xsd:string"&gt;
 *      &lt;xsd:annotation&gt;
 *          &lt;xsd:documentation&gt;Human-readable name of the organization responsible for maintaining this object.&lt;/xsd:documentation&gt;
 *      &lt;/xsd:annotation&gt;
 *  &lt;/xsd:element&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class OrganizationBinding extends AbstractSimpleBinding {

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.Organization;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return String.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(InstanceComponent instance, Object value)
            throws Exception {

        //TODO: implement and remove call to super
        return value.toString();
    }

}
