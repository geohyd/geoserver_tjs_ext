package org.geotools.tjs.bindings;


import net.opengis.tjs10.FailedType;
import net.opengis.tjs10.Tjs10Factory;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/tjs/1.0:FailedType.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:complexType ecore:name="FailedType" name="FailedType"/&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class FailedTypeBinding extends AbstractComplexEMFBinding {

    public FailedTypeBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.FailedType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return FailedType.class;
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
