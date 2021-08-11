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
 * A representation of the model object '<em><b>Rowset Type</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link net.opengis.tjs10.RowsetType#getRow <em>Row</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='Rowset_._type' kind='elementOnly'"
 * @generated
 * @see net.opengis.tjs10.Tjs10Package#getRowsetType()
 */
public interface RowsetType extends EObject {
    /**
     * Returns the value of the '<em><b>Row</b></em>' containment reference list.
     * The list contents are of type {@link net.opengis.tjs10.RowType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Database row structure.  Contains data for a feature found in the spatial framework.
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Row</em>' containment reference list.
     * @model type="net.opengis.tjs10.RowType" containment="true" required="true"
     * extendedMetaData="kind='element' name='Row' namespace='##targetNamespace'"
     * @generated
     * @see net.opengis.tjs10.Tjs10Package#getRowsetType_Row()
     */
    EList getRow();

} // RowsetType
