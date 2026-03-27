
package de.bsi.testbedutils.bouncycertgen.xml.cv;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse f�r certType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="certType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="profileId" type="{https://www.bsi.bund.de}profileIdType"/>
 *         &lt;element name="certAuthRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="publicKey" type="{https://www.bsi.bund.de}publicKeyType"/>
 *         &lt;element name="certHolderRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="certHolderAuth" type="{https://www.bsi.bund.de}certHolderAuthType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="effDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *           &lt;element name="effDateOffset" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="expDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *           &lt;element name="expDateOffset" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;/choice>
 *         &lt;element name="extensions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="description" type="{https://www.bsi.bund.de}descriptionType" minOccurs="0"/>
 *                   &lt;element name="terminalSector" type="{https://www.bsi.bund.de}terminalSectorType" maxOccurs="2" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="signKey" type="{https://www.bsi.bund.de}signKeyType"/>
 *         &lt;element name="outputFile" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="createAdditionalHexFile" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certType", namespace = "https://www.bsi.bund.de", propOrder = {
    "profileId",
    "certAuthRef",
    "publicKey",
    "certHolderRef",
    "certHolderAuth",
    "effDate",
    "effDateOffset",
    "expDate",
    "expDateOffset",
    "extensions",
    "signKey",
    "outputFile"
})
public class CertType {

    @XmlElement(namespace = "https://www.bsi.bund.de", required = true)
    protected BigInteger profileId;
    @XmlElement(namespace = "https://www.bsi.bund.de", required = true)
    protected String certAuthRef;
    @XmlElement(namespace = "https://www.bsi.bund.de", required = true)
    protected PublicKeyType publicKey;
    @XmlElement(namespace = "https://www.bsi.bund.de", required = true)
    protected String certHolderRef;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    protected CertHolderAuthType certHolderAuth;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effDate;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    protected BigInteger effDateOffset;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expDate;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    protected BigInteger expDateOffset;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    protected CertType.Extensions extensions;
    @XmlElement(namespace = "https://www.bsi.bund.de", required = true)
    protected SignKeyType signKey;
    @XmlElement(namespace = "https://www.bsi.bund.de")
    protected CertType.OutputFile outputFile;

    /**
     * Ruft den Wert der profileId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getProfileId() {
        return profileId;
    }

    /**
     * Legt den Wert der profileId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setProfileId(BigInteger value) {
        this.profileId = value;
    }

    /**
     * Ruft den Wert der certAuthRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertAuthRef() {
        return certAuthRef;
    }

    /**
     * Legt den Wert der certAuthRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertAuthRef(String value) {
        this.certAuthRef = value;
    }

    /**
     * Ruft den Wert der publicKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PublicKeyType }
     *     
     */
    public PublicKeyType getPublicKey() {
        return publicKey;
    }

    /**
     * Legt den Wert der publicKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicKeyType }
     *     
     */
    public void setPublicKey(PublicKeyType value) {
        this.publicKey = value;
    }

    /**
     * Ruft den Wert der certHolderRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertHolderRef() {
        return certHolderRef;
    }

    /**
     * Legt den Wert der certHolderRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertHolderRef(String value) {
        this.certHolderRef = value;
    }

    /**
     * Ruft den Wert der certHolderAuth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertHolderAuthType }
     *     
     */
    public CertHolderAuthType getCertHolderAuth() {
        return certHolderAuth;
    }

    /**
     * Legt den Wert der certHolderAuth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertHolderAuthType }
     *     
     */
    public void setCertHolderAuth(CertHolderAuthType value) {
        this.certHolderAuth = value;
    }

    /**
     * Ruft den Wert der effDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffDate() {
        return effDate;
    }

    /**
     * Legt den Wert der effDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffDate(XMLGregorianCalendar value) {
        this.effDate = value;
    }

    /**
     * Ruft den Wert der effDateOffset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEffDateOffset() {
        return effDateOffset;
    }

    /**
     * Legt den Wert der effDateOffset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEffDateOffset(BigInteger value) {
        this.effDateOffset = value;
    }

    /**
     * Ruft den Wert der expDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpDate() {
        return expDate;
    }

    /**
     * Legt den Wert der expDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpDate(XMLGregorianCalendar value) {
        this.expDate = value;
    }

    /**
     * Ruft den Wert der expDateOffset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExpDateOffset() {
        return expDateOffset;
    }

    /**
     * Legt den Wert der expDateOffset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExpDateOffset(BigInteger value) {
        this.expDateOffset = value;
    }

    /**
     * Ruft den Wert der extensions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertType.Extensions }
     *     
     */
    public CertType.Extensions getExtensions() {
        return extensions;
    }

    /**
     * Legt den Wert der extensions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertType.Extensions }
     *     
     */
    public void setExtensions(CertType.Extensions value) {
        this.extensions = value;
    }

    /**
     * Ruft den Wert der signKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignKeyType }
     *     
     */
    public SignKeyType getSignKey() {
        return signKey;
    }

    /**
     * Legt den Wert der signKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignKeyType }
     *     
     */
    public void setSignKey(SignKeyType value) {
        this.signKey = value;
    }

    /**
     * Ruft den Wert der outputFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertType.OutputFile }
     *     
     */
    public CertType.OutputFile getOutputFile() {
        return outputFile;
    }

    /**
     * Legt den Wert der outputFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertType.OutputFile }
     *     
     */
    public void setOutputFile(CertType.OutputFile value) {
        this.outputFile = value;
    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="description" type="{https://www.bsi.bund.de}descriptionType" minOccurs="0"/>
     *         &lt;element name="terminalSector" type="{https://www.bsi.bund.de}terminalSectorType" maxOccurs="2" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description",
        "terminalSector"
    })
    public static class Extensions {

        @XmlElement(namespace = "https://www.bsi.bund.de")
        protected DescriptionType description;
        @XmlElement(namespace = "https://www.bsi.bund.de")
        protected List<TerminalSectorType> terminalSector;

        /**
         * Ruft den Wert der description-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link DescriptionType }
         *     
         */
        public DescriptionType getDescription() {
            return description;
        }

        /**
         * Legt den Wert der description-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link DescriptionType }
         *     
         */
        public void setDescription(DescriptionType value) {
            this.description = value;
        }

        /**
         * Gets the value of the terminalSector property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the terminalSector property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTerminalSector().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TerminalSectorType }
         * 
         * 
         */
        public List<TerminalSectorType> getTerminalSector() {
            if (terminalSector == null) {
                terminalSector = new ArrayList<TerminalSectorType>();
            }
            return this.terminalSector;
        }

    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="createAdditionalHexFile" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class OutputFile {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "createAdditionalHexFile")
        protected Boolean createAdditionalHexFile;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der createAdditionalHexFile-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isCreateAdditionalHexFile() {
            return createAdditionalHexFile;
        }

        /**
         * Legt den Wert der createAdditionalHexFile-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setCreateAdditionalHexFile(Boolean value) {
            this.createAdditionalHexFile = value;
        }

    }

}
