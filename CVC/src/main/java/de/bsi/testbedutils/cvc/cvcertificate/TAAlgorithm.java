package de.bsi.testbedutils.cvc.cvcertificate;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CVCA signature algorithm
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:55
 */
public enum TAAlgorithm {
    /**
     * Undefined algorithm
     */
    UNDEFINED(KeyType.KEY_UNDEFINED),
    /**
     * RSA v1.5 with SHA 1
     */
    RSA_v1_5_SHA_1(KeyType.KEY_RSA, Oids.OID_RSA_v1_5_SHA_1, "SHA1WithRSA"),
    /**
     * RSA v1.5 with SHA 256
     */
    RSA_v1_5_SHA_256(KeyType.KEY_RSA, Oids.OID_RSA_v1_5_SHA_256, "SHA256WithRSA"),
    /**
     * RSA v1.5 with SHA 512
     */
    RSA_v1_5_SHA_512(KeyType.KEY_RSA, Oids.OID_RSA_v1_5_SHA_512, "SHA512WithRSA"),
    /**
     * RSA v1.5 without hashing
     */
    RSA_v1_5_NONE(KeyType.KEY_RSA, "NONEWITHRSA"),
    /**
     * RSA PSS with SHA 1
     */
    RSA_PSS_SHA_1(KeyType.KEY_RSA, Oids.OID_RSA_PSS_SHA_1, "SHA1withRSA/PSS"),
    /**
     * RSA PSS with SHA 256
     */
    RSA_PSS_SHA_256(KeyType.KEY_RSA, Oids.OID_RSA_PSS_SHA_256, "SHA256withRSA/PSS"),
    /**
     * RSA PSS with SHA 512
     */
    RSA_PSS_SHA_512(KeyType.KEY_RSA, Oids.OID_RSA_PSS_SHA_512, "SHA512withRSA/PSS"),
    /**
     * RSA PSS without hashing
     */
    RSA_PSS_NONE(KeyType.KEY_RSA, "NONEWITHRSAPSS"),
    /**
     * ECC with SHA1
     */
    ECDSA_SHA_1(KeyType.KEY_ECDSA, Oids.OID_ECDSA_SHA_1, "SHA1withCVC-ECDSA"),
    /**
     * ECC with SHA224
     */
    ECDSA_SHA_224(KeyType.KEY_ECDSA, Oids.OID_ECDSA_SHA_224, "SHA224withCVC-ECDSA"),
    /**
     * ECC with SHA256
     */
    ECDSA_SHA_256(KeyType.KEY_ECDSA, Oids.OID_ECDSA_SHA_256, "SHA256withCVC-ECDSA"),
    /**
     * ECC with SHA384
     */
    ECDSA_SHA_384(KeyType.KEY_ECDSA, Oids.OID_ECDSA_SHA_384, "SHA384withCVC-ECDSA"),
    /**
     * ECC with SHA512
     */
    ECDSA_SHA_512(KeyType.KEY_ECDSA, Oids.OID_ECDSA_SHA_512, "SHA512withCVC-ECDSA"),
    /**
     * ECDSA without hashing
     */
    ECDSA_NONE(KeyType.KEY_ECDSA, "NONEwithECDSA");

    private final KeyType keyType;
    private final byte[] oid;
    private final String signAlgo;
    private String strOid;

    TAAlgorithm(KeyType keyType) {
        this.keyType = keyType;
        this.oid = new byte[0];
        signAlgo = "UNDEFIED";
    }

    TAAlgorithm(KeyType keyType, byte[] oid) {
        this.keyType = keyType;
        this.oid = oid;
        signAlgo = "UNDEFINED";
    }

    TAAlgorithm(KeyType keyType, byte[] oid, String signAlgo) {
        this.keyType = keyType;
        this.oid = oid;
        this.signAlgo = signAlgo;
    }

    TAAlgorithm(KeyType keyType, String signAlgo) {
        this.keyType = keyType;
        this.oid = new byte[0];
        this.signAlgo = signAlgo;
    }

    public static TAAlgorithm findFromOid(DataBuffer dataBuffer) {
        for (TAAlgorithm taAlgorithm : TAAlgorithm.values()) {
            if (Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, taAlgorithm.getOid()).equals(dataBuffer)) {
                return taAlgorithm;
            }
        }
        return UNDEFINED;
    }

    /**
     * Returns true if an ECDSA Signature Algorithm
     *
     * @return boolean
     */
    boolean isECDSA() {
        return keyType == KeyType.KEY_ECDSA;
    }

    /**
     * Returns true if an ECDSA Signature Algorithm with any Hash
     *
     * @return boolean
     */
    boolean isECDSAWithSHA() {
        return keyType == KeyType.KEY_ECDSA && this != ECDSA_NONE;
    }

    /**
     * Returns true if an RSA Signature Algorithm
     *
     * @return boolean
     */
    boolean isRSA() {
        return keyType == KeyType.KEY_RSA;
    }

    /**
     * Returns true if an RSA Signature Algorithm with any Hash
     *
     * @return boolean
     */
    boolean isRSAWithSHA() {
        return keyType == KeyType.KEY_RSA && this != RSA_PSS_NONE;
    }

    /**
     * Returns the key type of this algo
     *
     * @return keyType {@link KeyType}
     */
    public KeyType getKeyType() {
        return keyType;
    }


    public byte[] getOid() {
        return oid;
    }

    public DataBuffer getOidDataBuffer() {
        return Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, getOid());
    }

    /**
     * Returns the Sign Algo as String
     *
     * @return
     */
    public String getSignAlgo() {
        return signAlgo;
    }

    /**
     * Returns the OID String as OID representation e.g 0.4.0.127.0.7.2.2.2.2.3
     * @return
     */
    public String getOID() {
        if (strOid == null) {
            // Use a StringBuilder to efficiently build the final string
            List<String> integers = new ArrayList<>();
            integers.add("0");//add prefix 0
            for (byte b : getOidDataBuffer().toByteArray()) {
                // Convert the decimal integer value to its string representation
                integers.add(String.valueOf(b));
            }

            strOid = integers.stream().map(String::valueOf).collect(Collectors.joining("."));
        }
        return strOid;
    }
}