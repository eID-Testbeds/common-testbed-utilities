package de.bsi.testbedutils.bouncycertgen.x509;

import org.bouncycastle.asn1.x509.KeyPurposeId;

import java.util.Arrays;

/**
 * Represents the configuration for generating a certificate, encapsulating all necessary information such as issuer, subject, key algorithm, and certificate algorithm among others.
 * This class is designed to hold the attributes required to generate X509 and CV certificates, supporting various key algorithms, certificate storage formats, and key usages.
 */
public class CertificateRepresentation {

    public final String issuerName;
    public final String subjectName;
    public final String fileName;
    public final String directory;
    public final String keyAlgorithm;
    public final int keySize;
    public final String keyCurve;
    public final String certAlgorithm;
    public final boolean asPEM;
    public final boolean asCRT;
    public final boolean withKey;
    public final boolean asKey;
    public final boolean keyAsPem;
    public final boolean storePublicKey;
    public final boolean extensionCritical;
    public final boolean keyUsageIsCritical;
    public final boolean digitalSignature;
    public final boolean keyEncipherment;
    public final boolean keyAgreement;
    public final boolean certificateSigning;
    public final boolean crlSign;
    public final boolean nonRepudiation;
    public final boolean AKI; // Authority Key Identifier
    public final boolean SKI; // Subject Key Identifier
    public final KeyPurposeId[] EKU;
    public final boolean NCT; // Netscape Certificate Type
    public final String NC; // Netscape Comment
    public final boolean includeSubjectAltName; // Include Subject Alternative Name extension

    public final boolean explicitCurveParams;

    /**
     * Constructs a new {@link CertificateRepresentation} with the specified details.
     *
     * @param issuerName The name of the issuer of the certificate.
     * @param subjectName The name of the subject of the certificate.
     * @param fileName The filename under which the certificate will be stored.
     * @param keyAlgorithm The algorithm of the certificate's key pair.
     * @param sizeOrCurve Either the size of the key (if using RSA or DSA) or the curve name (if using ECDSA).
     * @param certAlgorithm The algorithm used for the certificate.
     * @param directory The directory under which the certificate will be written.
     * @param asPEM Flag indicating if the certificate should be stored in PEM format; if false, it's stored in DER format.
     * @param asCRT Flag indicating if the certificate should be stored with a .crt extension (PEM format).
     * @param withKey Flag indicating if the certificate should be stored with its private key.
     * @param asKey Flag indicating if the certificate's private key should be stored with a .key extension; only relevant if withKey is true. If false, it's stored in DER format.
     * @param keyAsPem Flag indicating if the private key should be stored in PEM format; only relevant if withKey is true.
     * @param storePublicKey Flag indicating if the public key should be stored alongside the certificate.
     * @param extensionCritical Flag indicating if the extensions are critical.
     * @param keyUsageIsCritical Flag indicating if the key usage is critical.
     * @param digitalSignature Flag indicating if the key usage includes digital signatures.
     * @param keyEncipherment Flag indicating if the key usage includes key encipherment.
     * @param keyAgreement Flag indicating if the key usage includes key agreement.
     * @param certificateSigning Flag indicating if the key usage includes certificate signing.
     * @param crlSign Flag indicating if the key usage includes CRL signing.
     * @param nonRepudiation Flag indicating if the key usage includes non-repudiation.
     * @param aki Flag indicating if the Authority Key Identifier (AKI) is used.
     * @param ski Flag indicating if the Subject Key Identifier (SKI) is used.
     * @param eku Array of Extended Key Usage identifiers to be included in the certificate.
     * @param nct Flag indicating if the Netscape Certificate Type extension is used.
     * @param nc The Netscape Comment extension value.
     */
    public CertificateRepresentation(String issuerName, String subjectName, String fileName, String keyAlgorithm, Object sizeOrCurve, String certAlgorithm,
                                     String directory, boolean asPEM, boolean asCRT, boolean withKey, boolean asKey, boolean keyAsPem, boolean storePublicKey,
                                     boolean extensionCritical, boolean keyUsageIsCritical, boolean digitalSignature, boolean keyEncipherment, boolean keyAgreement, boolean certificateSigning,
                                     boolean crlSign, boolean nonRepudiation, boolean aki, boolean ski, KeyPurposeId[] eku, boolean nct, String nc, boolean includeSubjectAltName, boolean explicitCurveParams) {
        this.issuerName = issuerName;
        this.subjectName = subjectName;
        this.fileName = fileName;
        this.keyAlgorithm = keyAlgorithm;
        this.certAlgorithm = certAlgorithm;
        this.directory = directory;
        this.asPEM = asPEM;
        this.asCRT = asCRT;
        this.withKey = withKey;
        this.asKey = asKey;
        this.keyAsPem = keyAsPem;
        this.storePublicKey = storePublicKey;
        this.extensionCritical = extensionCritical;
        this.keyUsageIsCritical = keyUsageIsCritical;
        this.digitalSignature = digitalSignature;
        this.keyEncipherment = keyEncipherment;
        this.keyAgreement = keyAgreement;
        this.certificateSigning = certificateSigning;
        this.crlSign = crlSign;
        this.nonRepudiation = nonRepudiation;
        this.AKI = aki;
        this.SKI = ski;
        this.EKU = eku;
        this.NCT = nct;
        this.NC = nc;
        this.includeSubjectAltName = includeSubjectAltName;
        this.explicitCurveParams = explicitCurveParams;

        // Initialize keySize and keyCurve based on sizeOrCurve's type
        if (sizeOrCurve instanceof Integer) {
            this.keySize = (Integer) sizeOrCurve;
            this.keyCurve = null;
        } else if (sizeOrCurve instanceof String) {
            this.keyCurve = (String) sizeOrCurve;
            this.keySize = 0;
        } else {
            throw new IllegalArgumentException("sizeOrCurve must be either Integer (keySize) or String (keyCurve)");
        }
    }

    @Override
    public String toString() {
        return "CertificateRepresentation{" +
                "issuerName='" + issuerName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", directory='" + directory + '\'' +
                ", keyAlgorithm='" + keyAlgorithm + '\'' +
                ", keySize=" + keySize +
                ", keyCurve='" + keyCurve + '\'' +
                ", certAlgorithm='" + certAlgorithm + '\'' +
                ", asPEM=" + asPEM +
                ", asCRT=" + asCRT +
                ", withKey=" + withKey +
                ", asKey=" + asKey +
                ", keyAsPem=" + keyAsPem +
                ", storePublicKey=" + storePublicKey +
                ", extensionCritical=" + extensionCritical +
                ", keyUsageIsCritical=" + keyUsageIsCritical +
                ", digitalSignature=" + digitalSignature +
                ", keyEncipherment=" + keyEncipherment +
                ", keyAgreement=" + keyAgreement +
                ", certificateSigning=" + certificateSigning +
                ", crlSign=" + crlSign +
                ", nonRepudiation=" + nonRepudiation +
                ", AKI=" + AKI +
                ", SKI=" + SKI +
                ", EKU=" + Arrays.toString(EKU) +
                ", NCT=" + NCT +
                ", NC='" + NC + '\'' +
                ", includeSubjectAltName=" + includeSubjectAltName +
                ", explicitCurveParams=" + explicitCurveParams +
                '}';
    }
}
