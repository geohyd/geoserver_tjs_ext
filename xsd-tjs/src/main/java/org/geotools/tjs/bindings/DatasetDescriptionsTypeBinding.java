package org.geotools.tjs.bindings;


import net.opengis.tjs10.DatasetDescriptionsType;
import net.opengis.tjs10.Tjs10Factory;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/tjs/1.0:DatasetDescriptionsType.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:complexType ecore:name="DatasetDescriptionsType" name="DatasetDescriptionsType"&gt;
 *      &lt;xsd:sequence&gt;
 *          &lt;xsd:element ecore:name="framework" form="qualified"
 *              maxOccurs="unbounded" name="Framework" type="tjs:Framework4Type"&gt;
 *              &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;Spatial framework for which attribute data is housed on this server.&lt;/xsd:documentation&gt;
 *              &lt;/xsd:annotation&gt;
 *          &lt;/xsd:element&gt;
 *      &lt;/xsd:sequence&gt;
 *      &lt;xsd:attribute name="capabilities" type="xsd:string" use="required"&gt;
 *          &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;GetCapabilities URL of the TJS server.&lt;/xsd:documentation&gt;
 *          &lt;/xsd:annotation&gt;
 *      &lt;/xsd:attribute&gt;
 *      &lt;xsd:attribute ref="xml:lang" use="required"&gt;
 *          &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;RFC 4646 language code of the human-readable text (e.g. "en-CA").&lt;/xsd:documentation&gt;
 *          &lt;/xsd:annotation&gt;
 *      &lt;/xsd:attribute&gt;
 *      &lt;xsd:attribute ecore:default="TJS" name="service"
 *          type="xsd:anySimpleType" use="required"&gt;
 *          &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;Service type identifier&lt;/xsd:documentation&gt;
 *          &lt;/xsd:annotation&gt;
 *      &lt;/xsd:attribute&gt;
 *      &lt;xsd:attribute ecore:default="1.0" name="version"
 *          type="xsd:anySimpleType" use="required"&gt;
 *          &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;Version of the TJS interface specification implemented by the server (1.0)&lt;/xsd:documentation&gt;
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
public class DatasetDescriptionsTypeBinding extends AbstractComplexEMFBinding {

    public DatasetDescriptionsTypeBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.DatasetDescriptionsType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return DatasetDescriptionsType.class;
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
