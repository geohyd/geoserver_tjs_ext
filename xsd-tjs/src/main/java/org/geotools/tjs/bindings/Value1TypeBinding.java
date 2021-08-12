package org.geotools.tjs.bindings;


import net.opengis.tjs10.Tjs10Factory;
import net.opengis.tjs10.ValueType1;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/tjs/1.0:Value1Type.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:complexType ecore:name="ValueType1" name="Value1Type"&gt;
 *      &lt;xsd:sequence&gt;
 *          &lt;xsd:element ecore:name="identifier" ref="tjs:Identifier"&gt;
 *              &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;Text string found in the V elements of this attribute&lt;/xsd:documentation&gt;
 *              &lt;/xsd:annotation&gt;
 *          &lt;/xsd:element&gt;
 *          &lt;xsd:element ecore:name="title" ref="tjs:Title"&gt;
 *              &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;Human-readable short description suitable to display on a pick list, legend, and/or on mouseover.  Note that for attributes the unit of measure should not appear in the Title. Instead, it should appear in the UOM element.&lt;/xsd:documentation&gt;
 *              &lt;/xsd:annotation&gt;
 *          &lt;/xsd:element&gt;
 *          &lt;xsd:element ecore:name="abstract" ref="tjs:Abstract"&gt;
 *              &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;One or more paragraphs of human-readable relevant text suitable for display in a pop-up window.&lt;/xsd:documentation&gt;
 *              &lt;/xsd:annotation&gt;
 *          &lt;/xsd:element&gt;
 *          &lt;xsd:element ecore:name="documentation" minOccurs="0" ref="tjs:Documentation"&gt;
 *              &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;URL reference to a web-accessible resource which contains further information describing this object.&lt;/xsd:documentation&gt;
 *              &lt;/xsd:annotation&gt;
 *          &lt;/xsd:element&gt;
 *      &lt;/xsd:sequence&gt;
 *      &lt;xsd:attribute name="color" type="xsd:anySimpleType"&gt;
 *          &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;Hex code for a color that is suggested for cartographic portrayal of this value.  E.g."CCFFCC"&lt;/xsd:documentation&gt;
 *          &lt;/xsd:annotation&gt;
 *      &lt;/xsd:attribute&gt;
 *  &lt;/xsd:complexType&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class Value1TypeBinding extends AbstractComplexEMFBinding {

    public Value1TypeBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.Value1Type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return ValueType1.class;
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
