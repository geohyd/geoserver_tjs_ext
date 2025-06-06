package org.geotools.tjs.bindings;


import net.opengis.tjs10.DataClassType;
import net.opengis.tjs10.Tjs10Factory;
import org.geotools.tjs.TJS;
import org.geotools.xsd.AbstractComplexEMFBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

import javax.xml.namespace.QName;

/**
 * Binding object for the element http://www.opengis.net/tjs/1.0:DataClass.
 * <p/>
 * <p>
 * <pre>
 * 	 <code>
 *  &lt;xsd:element name="DataClass" type="tjs:DataClassType"/&gt;
 *
 * 	  </code>
 * 	 </pre>
 * </p>
 *
 * @generated
 */
public class DataClassBinding extends AbstractComplexEMFBinding {

    public DataClassBinding(Tjs10Factory factory) {
        super(factory);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return TJS.DataClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return DataClassType.class;
    }

    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        return super.parse(instance, node, value);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
