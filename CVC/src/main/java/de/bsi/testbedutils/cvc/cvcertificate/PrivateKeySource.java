package de.bsi.testbedutils.cvc.cvcertificate;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Signature;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import de.bsi.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import de.bsi.testbedutils.cvc.cvcertificate.exception.CVUnknownAlgorithmException;

/**
 * 
 * 
 * @author meier.marcus
 */
public class PrivateKeySource extends IPrivateKeySource {
    
    protected PrivateKey m_key;
    
    /**
     * 
     * @param key
     *            consigns the private key which will used to sign the data
     */
    public PrivateKeySource(PrivateKey key) {
        
        m_key = key;
        
    }
    
    @Override
    public KeyType getKeyType() throws CVKeyTypeNotSupportedException {
        String strAlgo = m_key.getAlgorithm();
        
        if ("RSA".equals(strAlgo)) {
            return KeyType.KEY_RSA;
        } else if ("EC".equals(strAlgo) || "ECC".equals(strAlgo) || "ECDSA".equals(strAlgo)) {
            return KeyType.KEY_ECDSA;
        }
        
        throw new CVKeyTypeNotSupportedException();
    }
    
    @Override
    public void signContent(DataBuffer rContent, TAAlgorithm signGenOp, DataBuffer rSignature)
            throws CVKeyTypeNotSupportedException {
        Signature signature;
        try {
            if(signGenOp == TAAlgorithm.UNDEFINED){
                // m_rLog << "unknown algorithm type" << std::endl;
                throw new CVUnknownAlgorithmException();
            }
            signature = Signature.getInstance(signGenOp.getSignAlgo(), new BouncyCastleProvider());
            signature.initSign(m_key);
            signature.update(rContent.toByteArray());
            byte[] sign = signature.sign();
            
            if (signGenOp.isECDSA()) {
                    DataBuffer plainSign = convertToPlainBC(sign);
                    rSignature.assign(plainSign);
            } else {
                    rSignature.assign(sign);
            }
        } catch (Exception e) {
            throw new CVKeyTypeNotSupportedException(e);
        }
    }
    
    protected static DataBuffer convertToPlainBC(byte[] sign) {

        try (ASN1InputStream dIn = new ASN1InputStream(sign)) {
            ASN1Primitive obj;

            obj = dIn.readObject();

            if (obj instanceof ASN1Sequence) {
                ASN1Sequence seq = (ASN1Sequence) obj;
                if (seq.size() == 2) {
                    ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
                    ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
                    byte[] res;
                    byte[] byteR = makeUnsigned(r.getValue());
                    byte[] byteS = makeUnsigned(s.getValue());

                    if (byteR.length > byteS.length) {
                        res = new byte[byteR.length * 2];
                    } else {
                        res = new byte[byteS.length * 2];
                    }

                    System.arraycopy(byteR, 0, res, res.length / 2 - byteR.length, byteR.length);
                    System.arraycopy(byteS, 0, res, res.length - byteS.length, byteS.length);

                    return new DataBuffer(res);
                }
            }
        } catch (Exception ignored) {
        }
        // ???
        return new DataBuffer(sign);
    }
    
    private static byte[] makeUnsigned(BigInteger val) {
        byte[] res = val.toByteArray();
        
        if (res[0] == 0) {
            byte[] tmp = new byte[res.length - 1];
            
            System.arraycopy(res, 1, tmp, 0, tmp.length);
            
            return tmp;
        }
        
        return res;
    }
}
