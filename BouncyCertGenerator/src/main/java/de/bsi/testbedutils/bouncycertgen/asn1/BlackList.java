package de.bsi.testbedutils.bouncycertgen.asn1;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.*;


/**
 * BlackList as specified in BSI TR-03129
 * <p/>
 *
 * <pre>
 *  BlackList ::= SEQUENCE{
 *    version INTEGER{v1(0)},
 *    type INTEGER{complete(0),added(1),removed(2)},
 *    listID ListID,
 *    deltaBase ListID OPTIONAL, -- required for delta lists
 *    content SEQUENCE OF BlackListDetails,
 *  }
 * </pre>
 */

public class BlackList extends ASN1Object {
    private ASN1Integer version;
    private ASN1Integer type;
    private ASN1OctetString listID;
    private ASN1OctetString deltaBase;
    private ASN1Sequence content;

    /**
     * Private constructor to be used by getInstance
     *
     * @param seq ASN1Sequence
     */
    private BlackList(ASN1Sequence seq) {

        if (seq.size() < 1 || seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int i = 0;
        version = ASN1Integer.getInstance(seq.getObjectAt(i++));
        type = ASN1Integer.getInstance(seq.getObjectAt(i++));
        listID = ASN1OctetString.getInstance(seq.getObjectAt(i++));
        if (seq.getObjectAt(i) instanceof ASN1OctetString)
            deltaBase = ASN1OctetString.getInstance(seq.getObjectAt(i++));
        content = ASN1Sequence.getInstance(seq.getObjectAt(i++));
    }

    public BlackList(BlackListDetails[] content) {
        this.version = new ASN1Integer(0);
        this.type = new ASN1Integer(0);
        this.listID = new DEROctetString(new byte[] {0x22});
        this.content = new DERSequence(content);
    }

    /**
     * Create a new BlackList from an object
     *
     * @param obj the object we want to convert
     * @return a BlackList or null if obj was null
     */
    public static BlackList getInstance(Object obj) {
        if (obj instanceof BlackList) {
            return (BlackList) obj;
        } else if (obj != null) {
            return new BlackList(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(version);
        v.add(type);
        v.add(listID);
        if (deltaBase != null)
            v.add(deltaBase);
        v.add(content);
        return new BERSequence(v);
    }

    /**
     * Get the version of the BlackList
     *
     * @return the version
     */
    public int getVersion() {
        return version.getValue().intValue();
    }

    /**
     * Get the type of the BlackList. 0 means complete list, 1 means added list, 2 means removed list
     *
     * @return the type
     */
    public int getListType() {
        return type.getValue().intValue();
    }

    /**
     * Get the listID of the BlackList
     *
     * @return the listID
     */
    public byte[] getListID() {
        return listID.getOctets();
    }

    /**
     * Get the base listId of a delta BlackList (if available)
     *
     * @return the base listId or null
     */
    public byte[] getDeltaBase() {
        if (deltaBase != null)
            return deltaBase.getOctets();
        else
            return null;
    }

    /**
     * Get the content of the BlackList
     *
     * @return the content
     */
    public ASN1Sequence getContent() {
        return content;
    }

    public List<BlackListDetails> getBlackListDetailsList() {
        List<BlackListDetails> result = new ArrayList<>();

        for (ASN1Encodable asn : content) {
            BlackListDetails bld = BlackListDetails.getInstance(asn);
            result.add(bld);
        }

        return result;
    }
}
