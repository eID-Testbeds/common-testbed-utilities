package de.bsi.testbedutils.bouncycertgen.asn1;

import org.bouncycastle.asn1.*;

/**
 * KnowDefect as specified in BSI TR-03129
 * <p/>
 *
 * <pre>
 *  KnowDefect ::= SEQUENCE {
 *  	defectType	OBJECT IDENTIFIER,
 *  	parameters	ANY defined by defectType OPTIONAL
 *  }
 * </pre>
 */

public class KnownDefect extends ASN1Object {

    private ASN1ObjectIdentifier defectType;
    private ASN1Encodable parameters;

    /**
     * Private constructor to be used by getInstance
     *
     * @param seq ASN1Sequence
     */
    private KnownDefect(ASN1Sequence seq) {

        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }

        defectType = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));

        if (seq.size() > 1) {
            parameters = seq.getObjectAt(1);
        }
    }

    public KnownDefect(ASN1ObjectIdentifier defectType) {
        this.defectType = defectType;
        this.parameters = new ASN1Enumerated(0);
    }

    /**
     * Create a new KnownDefect from an object
     *
     * @param obj the object we want to convert
     * @return a KnownDefect or null if obj was null
     */
    public static KnownDefect getInstance(Object obj) {
        if (obj instanceof KnownDefect) {
            return (KnownDefect) obj;
        } else if (obj != null) {
            return new KnownDefect(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(defectType);

        if (parameters != null) {
            v.add(parameters);
        }

        return new BERSequence(v);
    }

    /**
     * Get an ASN1ObjectIdentifier representing the defect type
     *
     * @return the defect type
     */
    public ASN1ObjectIdentifier getDefectType() {
        return defectType;
    }

    /**
     * Get the parameters of the defect
     *
     * @return the parameters
     */
    public ASN1Encodable getParameters() {
        return parameters;
    }
}
