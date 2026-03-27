package de.bsi.testbedutils.bouncycertgen.asn1;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.*;


/**
 * DefectList as specified in BSI TR-03129
 * <p/>
 *
 * <pre>
 *  DefectList ::= SEQUENCE {
 *  	version		INTEGER {v1 (0) },
 *  	hashAlg		OBJECT IDENTIFIER,
 *  	defects		SET OF Defect
 *  }
 * </pre>
 */

public class DefectList extends ASN1Object {
    private ASN1Integer version;
    private ASN1ObjectIdentifier hashAlg;
    private ASN1Set defects;

    /**
     * Private constructor to be used by getInstance
     *
     * @param seq ASN1Sequence
     */
    private DefectList(ASN1Sequence seq) {

        if (seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int i = 0;
        version = ASN1Integer.getInstance(seq.getObjectAt(i++));
        hashAlg = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(i++));
        defects = ASN1Set.getInstance(seq.getObjectAt(i++));

    }

    public DefectList(ASN1ObjectIdentifier hashAlg, Defect[] defects) {
        this.version = new ASN1Integer(0);
        this.hashAlg = hashAlg;
        this.defects = new DERSet(defects);
    }

    /**
     * Create a new DefectList from an object
     *
     * @param obj the object we want to convert
     * @return a DefectList or null if obj was null
     */
    public static DefectList getInstance(Object obj) {
        if (obj instanceof DefectList) {
            return (DefectList) obj;
        } else if (obj != null) {
            return new DefectList(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(version);
        v.add(hashAlg);
        v.add(defects);
        return new BERSequence(v);
    }

    /**
     * Returns the version of the defect list
     *
     * @return the version of the defect list
     */
    public int getVersion() {
        return version.getValue().intValue();
    }

    /**
     * Returns the hash algorithm object identifier used to calculate the defect list
     *
     * @return the hash algorithm object identifier used to calculate the defect list
     */
    public ASN1ObjectIdentifier getHashAlg() {
        return hashAlg;
    }

    /**
     * Returns the defects
     *
     * @return the defects
     */
    public ASN1Set getDefects() {
        return defects;
    }

    public List<Defect> getDefectList() {
        List<Defect> list = new ArrayList<>();

        for (ASN1Encodable asn : defects) {
            Defect defect = Defect.getInstance(asn);
            list.add(defect);
        }

        return list;
    }
}
