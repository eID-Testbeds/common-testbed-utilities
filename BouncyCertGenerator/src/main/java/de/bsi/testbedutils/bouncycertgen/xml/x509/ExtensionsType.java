
package de.bsi.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r extensionsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="extensionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="basicConstraints" type="{https://www.bsi.bund.de}basicConstraintsType"/>
 *         &lt;element name="keyUsage" type="{https://www.bsi.bund.de}keyUsageType" minOccurs="0"/>
 *         &lt;element name="extendedKeyUsage" type="{https://www.bsi.bund.de}extendedKeyUsageType" minOccurs="0"/>
 *         &lt;element name="subjectAltName" type="{https://www.bsi.bund.de}altNameType" minOccurs="0"/>
 *         &lt;element name="issuerAltName" type="{https://www.bsi.bund.de}altNameType" minOccurs="0"/>
 *         &lt;element name="cRLDistributionPoints" type="{https://www.bsi.bund.de}cRLDistributionPointsType" minOccurs="0"/>
 *         &lt;element name="signatureAlgorithms" type="{https://www.bsi.bund.de}cRLDistributionPointsType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extensionsType", propOrder = {

})
public class ExtensionsType {

    @XmlElement(required = true)
    protected BasicConstraintsType basicConstraints;
    protected KeyUsageType keyUsage;
    protected ExtendedKeyUsageType extendedKeyUsage;
    protected AltNameType subjectAltName;
    protected AltNameType issuerAltName;
    @XmlElement(name = "cRLDistributionPoints")
    protected CRLDistributionPointsType crlDistributionPoints;
    protected CRLDistributionPointsType signatureAlgorithms;

    /**
     * Ruft den Wert der basicConstraints-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BasicConstraintsType }
     *     
     */
    public BasicConstraintsType getBasicConstraints() {
        return basicConstraints;
    }

    /**
     * Legt den Wert der basicConstraints-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicConstraintsType }
     *     
     */
    public void setBasicConstraints(BasicConstraintsType value) {
        this.basicConstraints = value;
    }

    /**
     * Ruft den Wert der keyUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KeyUsageType }
     *     
     */
    public KeyUsageType getKeyUsage() {
        return keyUsage;
    }

    /**
     * Legt den Wert der keyUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyUsageType }
     *     
     */
    public void setKeyUsage(KeyUsageType value) {
        this.keyUsage = value;
    }

    /**
     * Ruft den Wert der extendedKeyUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedKeyUsageType }
     *     
     */
    public ExtendedKeyUsageType getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    /**
     * Legt den Wert der extendedKeyUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedKeyUsageType }
     *     
     */
    public void setExtendedKeyUsage(ExtendedKeyUsageType value) {
        this.extendedKeyUsage = value;
    }

    /**
     * Ruft den Wert der subjectAltName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AltNameType }
     *     
     */
    public AltNameType getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * Legt den Wert der subjectAltName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AltNameType }
     *     
     */
    public void setSubjectAltName(AltNameType value) {
        this.subjectAltName = value;
    }

    /**
     * Ruft den Wert der issuerAltName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AltNameType }
     *     
     */
    public AltNameType getIssuerAltName() {
        return issuerAltName;
    }

    /**
     * Legt den Wert der issuerAltName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AltNameType }
     *     
     */
    public void setIssuerAltName(AltNameType value) {
        this.issuerAltName = value;
    }

    /**
     * Ruft den Wert der crlDistributionPoints-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public CRLDistributionPointsType getCRLDistributionPoints() {
        return crlDistributionPoints;
    }

    /**
     * Legt den Wert der crlDistributionPoints-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public void setCRLDistributionPoints(CRLDistributionPointsType value) {
        this.crlDistributionPoints = value;
    }

    /**
     * Ruft den Wert der signatureAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public CRLDistributionPointsType getSignatureAlgorithms() {
        return signatureAlgorithms;
    }

    /**
     * Legt den Wert der signatureAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public void setSignatureAlgorithms(CRLDistributionPointsType value) {
        this.signatureAlgorithms = value;
    }

}
