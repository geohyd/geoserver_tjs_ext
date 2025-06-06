package org.geotools.tjs.bindings;


import net.opengis.tjs10.Tjs10Factory;
import net.opengis.tjs10.UncertaintyType;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the element http://www.opengis.net/tjs/1.0:Uncertainty.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:element name="Uncertainty" type="tjs:UncertaintyType"&gt;
 *      &lt;xsd:annotation&gt;
 *          &lt;xsd:documentation&gt;Standard Uncertainty, according to the generally agreed upon definition described at sites like http://physics.nist.gov/cuu/Uncertainty/index.html&lt;/xsd:documentation&gt;
 *      &lt;/xsd:annotation&gt;
 *  &lt;/xsd:element&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class UncertaintyBinding extends AbstractComplexEMFBinding {

    public UncertaintyBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.Uncertainty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return UncertaintyType.class;
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
