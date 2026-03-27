package de.bsi.testbedutils.bouncycertgen.asn1;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Defect as specified in BSI TR-03129
 * <p/>
 *
 * <pre>
 *  Defect ::= SEQUENCE {
 *  	signerIdentifier	SignerIdentifier,
 *  	certificateHash		OCTET STRING OPTIONAL,
 *  	knowDefects			SET OF KnownDefect
 *  }
 * </pre>
 */

public class Defect extends ASN1Object {

    private SignerIdentifier signerId;
    private ASN1OctetString certificateHash;
    private ASN1Set knownDefects;

    /**
     * Private constructor to be used by getInstance
     *
     * @param seq ASN1Sequence
     */
    private Defect(ASN1Sequence seq) {

        int index = 0;

        if (seq.size() < 1 || seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }

        signerId = SignerIdentifier.getInstance(seq.getObjectAt(index++));

        if (seq.getObjectAt(index) instanceof ASN1OctetString) {
            certificateHash = ASN1OctetString.getInstance(seq.getObjectAt(index++));
        }

        if (seq.getObjectAt(index) instanceof ASN1Set) {
            knownDefects = ASN1Set.getInstance(seq.getObjectAt(index++));
        }
    }

    public Defect(X509CertificateHolder defectCertificate, KnownDefect[] knownDefects) {
        this.signerId = new SignerIdentifier(new IssuerAndSerialNumber(defectCertificate.getIssuer(), defectCertificate.getSerialNumber()));
        this.knownDefects = new DERSet(knownDefects);
    }

    /**
     * Create a new Defect from an object
     *
     * @param obj the object we want to convert
     * @return a Defect or null if obj was null
     */
    public static Defect getInstance(Object obj) {
        if (obj instanceof Defect) {
            return (Defect) obj;
        } else if (obj != null) {
            return new Defect(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {

        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(signerId);
        if (certificateHash != null)
            v.add(certificateHash);
        v.add(knownDefects);
        return new BERSequence(v);
    }

    /**
     * Get the signer identifier
     *
     * @return the signer identifier
     */
    public SignerIdentifier getSignerId() {
        return signerId;
    }

    /**
     * Get the certificate hash
     *
     * @return the certificate hash
     */
    public ASN1OctetString getCertificateHash() {
        return certificateHash;
    }

    /**
     * Get the known defects
     *
     * @return the known defects
     */
    public ASN1Set getKnownDefects() {
        return knownDefects;
    }

    public List<KnownDefect> getKnownDefectList() {
        List<KnownDefect> list = new ArrayList<>();

        for (ASN1Encodable asn : knownDefects) {
            KnownDefect knownDefect = KnownDefect.getInstance(asn);
            list.add(knownDefect);
        }

        return list;
    }

    public String getIssuer() {
        IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(signerId);
        return issuerAndSerialNumber.getName().toString();
    }

    public BigInteger getSerialNumber() {
        IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(signerId);
        return issuerAndSerialNumber.getSerialNumber().getValue();
    }

}
