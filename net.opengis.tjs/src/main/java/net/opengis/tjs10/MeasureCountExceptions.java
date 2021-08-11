/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.tjs10;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Measure Count Exceptions</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link net.opengis.tjs10.MeasureCountExceptions#getNull <em>Null</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='MeasureCountExceptions' kind='elementOnly'"
 * @generated
 * @see net.opengis.tjs10.Tjs10Package#getMeasureCountExceptions()
 */
public interface MeasureCountExceptions extends EObject {
    /**
     * Returns the value of the '<em><b>Null</b></em>' containment reference list.
     * The list contents are of type {@link net.opengis.tjs10.NullType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Valid null values.
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Null</em>' containment reference list.
     * @model type="net.opengis.tjs10.NullType" containment="true" required="true"
     * extendedMetaData="kind='element' name='Null' namespace='##targetNamespace'"
     * @generated
     * @see net.opengis.tjs10.Tjs10Package#getMeasureCountExceptions_Null()
     */
    EList getNull();

} // MeasureCountExceptions
