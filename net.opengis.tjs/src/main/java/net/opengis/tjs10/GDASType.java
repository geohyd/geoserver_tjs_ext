/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.tjs10;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>GDAS Type</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link net.opengis.tjs10.GDASType#getFramework <em>Framework</em>}</li>
 * <li>{@link net.opengis.tjs10.GDASType#getCapabilities <em>Capabilities</em>}</li>
 * <li>{@link net.opengis.tjs10.GDASType#getLang <em>Lang</em>}</li>
 * <li>{@link net.opengis.tjs10.GDASType#getService <em>Service</em>}</li>
 * <li>{@link net.opengis.tjs10.GDASType#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='GDAS_._type' kind='elementOnly'"
 * @generated
 * @see net.opengis.tjs10.Tjs10Package#getGDASType()
 */
public interface GDASType extends EObject {
    /**
     * Returns the value of the '<em><b>Framework</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Framework</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Framework</em>' containment reference.
     * @model containment="true" required="true"
     * extendedMetaData="kind='element' name='Framework' namespace='##targetNamespace'"
     * @generated
     * @see #setFramework(FrameworkType3)
     * @see net.opengis.tjs10.Tjs10Package#getGDASType_Framework()
     */
    FrameworkType3 getFramework();

    /**
     * Sets the value of the '{@link net.opengis.tjs10.GDASType#getFramework <em>Framework</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Framework</em>' containment reference.
     * @generated
     * @see #getFramework()
     */
    void setFramework(FrameworkType3 value);

    /**
     * Returns the value of the '<em><b>Capabilities</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * GetCapabilities URL of the TJS server.
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Capabilities</em>' attribute.
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     * extendedMetaData="kind='attribute' name='capabilities'"
     * @generated
     * @see #setCapabilities(String)
     * @see net.opengis.tjs10.Tjs10Package#getGDASType_Capabilities()
     */
    String getCapabilities();

    /**
     * Sets the value of the '{@link net.opengis.tjs10.GDASType#getCapabilities <em>Capabilities</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Capabilities</em>' attribute.
     * @generated
     * @see #getCapabilities()
     */
    void setCapabilities(String value);

    /**
     * Returns the value of the '<em><b>Lang</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * RFC 4646 language code of the human-readable text (e.g. "en-CA").
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Lang</em>' attribute.
     * @model dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType" required="true"
     * extendedMetaData="kind='attribute' name='lang' namespace='http://www.w3.org/XML/1998/namespace'"
     * @generated
     * @see #setLang(Object)
     * @see net.opengis.tjs10.Tjs10Package#getGDASType_Lang()
     */
    Object getLang();

    /**
     * Sets the value of the '{@link net.opengis.tjs10.GDASType#getLang <em>Lang</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Lang</em>' attribute.
     * @generated
     * @see #getLang()
     */
    void setLang(Object value);

    /**
     * Returns the value of the '<em><b>Service</b></em>' attribute.
     * The default value is <code>"TJS"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Service type identifier
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Service</em>' attribute.
     * @model default="TJS" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType" required="true"
     * extendedMetaData="kind='attribute' name='service'"
     * @generated
     * @see #isSetService()
     * @see #unsetService()
     * @see #setService(Object)
     * @see net.opengis.tjs10.Tjs10Package#getGDASType_Service()
     */
    Object getService();

    /**
     * Sets the value of the '{@link net.opengis.tjs10.GDASType#getService <em>Service</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Service</em>' attribute.
     * @generated
     * @see #isSetService()
     * @see #unsetService()
     * @see #getService()
     */
    void setService(Object value);

    /**
     * Unsets the value of the '{@link net.opengis.tjs10.GDASType#getService <em>Service</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #isSetService()
     * @see #getService()
     * @see #setService(Object)
     */
    void unsetService();

    /**
     * Returns whether the value of the '{@link net.opengis.tjs10.GDASType#getService <em>Service</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return whether the value of the '<em>Service</em>' attribute is set.
     * @generated
     * @see #unsetService()
     * @see #getService()
     * @see #setService(Object)
     */
    boolean isSetService();

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * The default value is <code>"1.0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Version of the TJS interface specification implemented by the server (1.0)
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Version</em>' attribute.
     * @model default="1.0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType" required="true"
     * extendedMetaData="kind='attribute' name='version'"
     * @generated
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #setVersion(Object)
     * @see net.opengis.tjs10.Tjs10Package#getGDASType_Version()
     */
    Object getVersion();

    /**
     * Sets the value of the '{@link net.opengis.tjs10.GDASType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Version</em>' attribute.
     * @generated
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #getVersion()
     */
    void setVersion(Object value);

    /**
     * Unsets the value of the '{@link net.opengis.tjs10.GDASType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #isSetVersion()
     * @see #getVersion()
     * @see #setVersion(Object)
     */
    void unsetVersion();

    /**
     * Returns whether the value of the '{@link net.opengis.tjs10.GDASType#getVersion <em>Version</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return whether the value of the '<em>Version</em>' attribute is set.
     * @generated
     * @see #unsetVersion()
     * @see #getVersion()
     * @see #setVersion(Object)
     */
    boolean isSetVersion();

} // GDASType
