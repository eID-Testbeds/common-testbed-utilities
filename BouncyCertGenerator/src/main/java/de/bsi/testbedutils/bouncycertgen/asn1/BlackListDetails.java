package de.bsi.testbedutils.bouncycertgen.asn1;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.*;


/**
 * BlackListDetails as specified in BSI TR-03129
 * <p/>
 *
 * <pre>
 *  BlackListDetails ::= SEQUENCE{
 *    sectorID OCTET STRING,
 *    sectorSpecificIDs SEQUENCE OF OCTET STRING
 *  }
 * </pre>
 */

public class BlackListDetails extends ASN1Object {

    private ASN1OctetString sectorID;
    private ASN1Sequence sectorSpecificIDs;

    /**
     * Private constructor to be used by getInstance
     *
     * @param seq ASN1Sequence
     */
    private BlackListDetails(ASN1Sequence seq) {
        if (seq.size() != 2)
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        int i = 0;
        sectorID = ASN1OctetString.getInstance(seq.getObjectAt(i++));
        sectorSpecificIDs = ASN1Sequence.getInstance(seq.getObjectAt(i++));
    }

    public BlackListDetails(byte[] sectorID, List<byte[]> sectorSpecificIDs) {
        this.sectorID = new DEROctetString(sectorID);
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (byte[] id : sectorSpecificIDs) {
            v.add(new DEROctetString(id));
        }
        this.sectorSpecificIDs = new DERSequence(v);
    }

    /**
     * Create a new BlackListDetails from an object
     *
     * @param obj the object we want to convert
     * @return a BlackListDetails or null if obj was null
     */
    public static BlackListDetails getInstance(Object obj) {
        if (obj instanceof BlackListDetails) {
            return (BlackListDetails) obj;
        } else if (obj != null) {
            return new BlackListDetails(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(sectorID);
        v.add(sectorSpecificIDs);
        return new BERSequence(v);
    }

    /**
     * Get the sectorID. Still not sure, how it is usable.
     *
     * @return the sectorID
     */
    public byte[] getSectorID() {
        return sectorID.getOctets();
    }

    /**
     * Get the sectorSpecificIDs.
     *
     * @return the sectorSpecificIDs
     */
    public List<byte[]> getSectorSpecificIDs() {
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < sectorSpecificIDs.size(); i++) {
            list.add(ASN1OctetString.getInstance(sectorSpecificIDs.getObjectAt(i)).getOctets());
        }
        return list;
    }
}
