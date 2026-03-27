package de.bsi.testbedutils.bouncycertgen.cv;

import de.bsi.testbedutils.cvc.cvcertificate.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.*;

/**
 * This class is an offspring from CVCertGen.java, manipulated to return generated files instead of writing them directly to disk,
 * so they can be generated at runtime.
 */
public class RuntimeCVCertGen {

    private static final String CVCONFIG_SCHEMA_FILE_NAME = "cv_schema.xsd";

    private static Logger logger = LogManager.getRootLogger();
    private Date m_relDate;

    final Map<String, byte[]> certificatesAndKeys;

    /**
     * Initializes CV certificate generator.
     *
     * @param certificatesAndKeys Map that all files will be stored in as byte arrays
     */
    public RuntimeCVCertGen(Map<String, byte[]> certificatesAndKeys) {
        this.certificatesAndKeys = certificatesAndKeys;

        // set now as relative date
        m_relDate = new Date();

        // add provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Sets logger.
     *
     * @param logger The logger.
     */
    public static void setLogger(Logger logger) {
        RuntimeCVCertGen.logger = logger;
    }

    /**
     * Generates CV certificates from XML structure.
     *
     * @param xmlFileStream XML file InputStream.
     * @param subjectUrl    URL of the subject.
     * @return CV certificate list.
     */
    public Set<CVCertificate> generateFromXML(InputStream xmlFileStream, String subjectUrl) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Source xsdSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(CVCONFIG_SCHEMA_FILE_NAME));
        Schema xsd = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsdSource);
        dbFactory.setSchema(xsd);
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document xml = dBuilder.parse(xmlFileStream);
        return generateFromXML(xml, subjectUrl);
    }

    /**
     * Generates CV certificates from XML structure.
     *
     * @param xml        XML document.
     * @param subjectUrl URL of the subject.
     * @return CV certificate list.
     */
    public Set<CVCertificate> generateFromXML(Document xml, String subjectUrl) {
        Set<CVCertificate> certs = null;

        Hashtable<String, KeyInfo> keys = null;

        if (xml != null) {
            certs = new HashSet<>();

            NodeList rootChilds = xml.getFirstChild().getChildNodes();
            for (int i = 0; i < rootChilds.getLength(); i++) {
                switch (rootChilds.item(i).getNodeName()) {
                    case "keys": // checking/generating keys
                        keys = new Hashtable<>();

                        NodeList keysChilds = rootChilds.item(i).getChildNodes();
                        for (int j = 0; j < keysChilds.getLength(); j++) {
                            if (keysChilds.item(j).getNodeName().equals("key")) {
                                KeyInfo key = null;
                                try {
                                    key = generateKeyInfoFromNode(keysChilds.item(j));
                                } catch (Exception e) {
                                    logger.warn(e.getMessage());
                                }

                                if (key != null) {
                                    keys.put(key.getName(), key);
                                    logger.debug("added keys " + key.getName());
                                } else {
                                    logger.warn("unable to generated key ...");
                                }
                            }
                        }
                        break;
                    case "cert": // generating certificates
                        CVCertificate cert = null;
                        try {
                            cert = generateCertFromNode(rootChilds.item(i), keys, subjectUrl);
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                        }

                        if (cert != null) {
                            certs.add(cert);
                            logger.debug("added certificate (Holder Reference) " + cert.getCertHolderRef());
                        } else {
                            logger.warn("unable to generated certificate ...");
                        }
                        break;
                }
            }
        }

        return certs;
    }

    /**
     * Generates (or loads) key from XML node.
     *
     * @param node XML node.
     * @return KeyInfo.
     * @throws Exception
     */
    protected KeyInfo generateKeyInfoFromNode(Node node) throws Exception {
        KeyInfo keyInfo = null;

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("name")) {
                keyInfo = new KeyInfo(attributes.item(i).getNodeValue());
            }
        }

        // getting parameter
        if (keyInfo != null) {
            NodeList keyChilds = node.getChildNodes();
            for (int i = 0; i < keyChilds.getLength(); i++) {
                switch (keyChilds.item(i).getNodeName()) {
                    case "filePrivateKey":
                        keyInfo.setFilePrivateKey(keyChilds.item(i).getTextContent());
                        break;
                    case "filePublicKey":
                        keyInfo.setFilePublicKey(keyChilds.item(i).getTextContent());
                        break;
                    case "algorithm":
                        keyInfo.setAlgorithm(keyChilds.item(i).getTextContent());
                        break;
                    case "ecdsa": // ECDSA parameter
                        keyInfo.setType(keyChilds.item(i).getNodeName().toUpperCase());
                        keyInfo.setKeyParamSpec(parseECAlgorithm(keyChilds.item(i).getTextContent()));
                        break;
                    case "rsa": // RSA parameter
                        keyInfo.setType(keyChilds.item(i).getNodeName().toUpperCase());
                        BigInteger rsaPublicExpo = RSAKeyGenParameterSpec.F4; // default
                        int rsaKeyLength = 0;

                        NodeList rsaChilds = keyChilds.item(i).getChildNodes();
                        for (int j = 0; j < rsaChilds.getLength(); j++) {
                            switch (rsaChilds.item(j).getNodeName()) {
                                case "publicExpo":
                                    rsaPublicExpo = new BigInteger(rsaChilds.item(j).getTextContent());
                                    break;
                                case "length":
                                    String str = rsaChilds.item(j).getTextContent();
                                    rsaKeyLength = Integer.parseInt(str);
                            }
                        }

                        keyInfo.setKeyParamSpec(new RSAKeyGenParameterSpec(rsaKeyLength, rsaPublicExpo));
                        break;
                }
            }
                // generating key pair
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyInfo.getType(), new BouncyCastleProvider());

                SecureRandom random = new SecureRandom();
                keyGen.initialize(keyInfo.getKeyParamSpec(), random);
                keyInfo.setKeyPair(keyGen.generateKeyPair());

                // saving keys

                DataBuffer privKey = new DataBuffer(keyInfo.getKeyPair().getPrivate().getEncoded());

                privKey.writeToByteMap("CVCertificates/" + keyInfo.getFilePrivateKey(), certificatesAndKeys);

                if (keyInfo.getFilePublicKey() != null) {

                    DataBuffer pubKey = new DataBuffer(keyInfo.getKeyPair().getPublic().getEncoded());
                    pubKey.writeToByteMap("CVCertificates/" + keyInfo.getFilePublicKey(), certificatesAndKeys);

                    CVPubKeyHolder holder = new CVPubKeyHolder();
                    holder.setIncludeDomainParam(true);
                    holder.setAlgorithm(parseTAAlgorithm(keyInfo.getAlgorithm()));
                    holder.setKeySource(new PublicKeySource(keyInfo.getKeyPair().getPublic()));
                    DataBuffer pubKeyCV = new DataBuffer();
                    TLV.append(pubKeyCV, CVCertificate.s_CvPublicKeyTag, holder.generateCertPubKey(Oids.OID_RI_BASE));
                    pubKeyCV.writeToByteMap("CVCertificates/" + keyInfo.getFilePublicKey() + ".cv", certificatesAndKeys);
                }

                logger.debug("key " + keyInfo.getName() + " saved.");
        }

        return keyInfo;
    }

    /**
     * Generates CV certificate from XML node.
     *
     * @param node XML node.
     * @param keys Table of keys used for certificate generation.
     * @return CV certificate.
     * @throws Exception
     */
    protected CVCertificate generateCertFromNode(Node node, Hashtable<String, KeyInfo> keys, String subjectUrl) throws Exception {
        CVCertificate cert = new CVCertificate();
        String outputFile = null;
        KeyInfo signKey = null;
        KeyInfo publicKey;
        Node certDescNode = null;
        ArrayList<DataBuffer> sectorKeys = new ArrayList<>();

        boolean createAdditionalHexFile = false;

        NodeList certChilds = node.getChildNodes();
        for (int i = 0; i < certChilds.getLength(); i++) {
            switch (certChilds.item(i).getNodeName()) {
                case "profileId":
                    cert.setProfileId(Integer.parseInt(certChilds.item(i).getTextContent()));
                    break;
                case "certAuthRef":
                    cert.setCertAuthRef(certChilds.item(i).getTextContent());
                    break;
                case "publicKey": {
                    boolean domainParam = false;
                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("domainParam")) {
                            domainParam = parseXSBoolean(attributes.item(j).getNodeValue());
                        }
                    }

                    String str = certChilds.item(i).getTextContent();
                    publicKey = keys.get(str);
                    if (publicKey != null) {
                        cert.getPublicKey().setAlgorithm(parseTAAlgorithm(publicKey.getAlgorithm()));
                        cert.getPublicKey().setIncludeDomainParam(domainParam);

                        PublicKey key = publicKey.getKeyPair().getPublic();
                        cert.getPublicKey().setKeySource(new PublicKeySource(key));
                    } else {
                        logger.warn("Public key not found in key list!");
                        return null;
                    }
                }
                break;
                case "certHolderRef":
                    cert.setCertHolderRef(certChilds.item(i).getTextContent());
                    break;
                case "certHolderAuth": {
                    CVAuthorization usedAuth = null;
                    DataBuffer oid = null;

                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("type")) {
                            switch (attributes.item(j).getNodeValue()) {
                                case "AT":
                                    usedAuth = new CVAuthorizationAT();
                                    break;
                                case "IS":
                                    usedAuth = new CVAuthorizationIS();
                                    break;
                                case "ST":
                                    usedAuth = new CVAuthorizationST();
                                    break;
                            }
                        } else if (attributes.item(j).getNodeName().equals("forceOID")) {
                            oid = new DataBuffer();
                            oid.fromHexBinary(attributes.item(j).getNodeValue());
                        }
                    }

                    cert.getCertHolderAuth().setAuth(usedAuth);

                    if (oid != null) {
                        cert.getCertHolderAuth().getAuth().setInstanceOid(oid);
                    }

                    NodeList authChilds = certChilds.item(i).getChildNodes();
                    for (int j = 0; j < authChilds.getLength(); j++) {
                        switch (authChilds.item(j).getNodeName()) {
                            case "role":
                                switch (authChilds.item(j).getTextContent()) {
                                    case "CVCA":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.CVCA);
                                        break;
                                    case "DV_DOMESTIC":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.DVdomestic);
                                        break;
                                    case "DV_FOREIGN":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.DVforeign);
                                        break;
                                    case "TERMINAL":
                                        cert.getCertHolderAuth().getAuth().setRole(CertHolderRole.Terminal);
                                        break;
                                }
                                break;
                            case "writeDG17":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG17, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG18":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG18, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG19":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG19, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG20":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG20, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "writeDG21":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Write_DG21, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG1":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG1, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG2":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG2, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG3":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG3, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG4":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG4, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG5":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG5, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG6":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG6, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG7":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG7, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG8":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG8, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG9":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG9, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG10":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG10, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG11":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG11, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG12":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG12, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG13":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG13, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG14":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG14, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG15":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG15, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG16":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG16, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG17":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG17, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG18":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG18, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG19":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG19, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG20":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG20, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readDG21":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_Read_DG21, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "installQualifiedCertificate":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "installCertificate":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_InstallCertificate, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "pinManagement":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_PINManagement, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "canAllowed":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_CANAllowed, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "privilegedTerminal":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "restrictedIdentification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_RestrictedIdentification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "communityIDVerification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_CommunityIDVerification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "ageVerification":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationAT.auth_AgeVerification, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readEPassDG3":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationIS.auth_Read_DG3, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "readEPassDG4":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationIS.auth_Read_DG4, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "generateQualifiedElectronicSignature":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationST.auth_GenerateQualifiedSignature, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                            case "generateElectronicSignature":
                                cert.getCertHolderAuth().getAuth().setAuth(CVAuthorizationST.auth_GenerateSignature, parseXSBoolean(authChilds.item(j).getTextContent()));
                                break;
                        }
                    }
                }
                break;
                case "effDate": {
                    Date date = DatatypeConverter.parseDate(certChilds.item(i).getTextContent()).getTime();
                    cert.getEffDate().setDate(date);
                }
                break;
                case "effDateOffset": {
                    int days = Integer.parseInt(certChilds.item(i).getTextContent());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m_relDate); // Use given date as relative date
                    cal.add(Calendar.DATE, days);
                    cert.getEffDate().setDate(cal.getTime());
                }
                break;
                case "expDate": {
                    Date date = DatatypeConverter.parseDate(certChilds.item(i).getTextContent()).getTime();
                    cert.getExpDate().setDate(date);
                }
                break;
                case "expDateOffset": {
                    int days = Integer.parseInt(certChilds.item(i).getTextContent());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cert.getEffDate().getDate()); // Use effDate as relative date
                    cal.add(Calendar.DATE, days);
                    cert.getExpDate().setDate(cal.getTime());
                }
                break;
                case "extensions": {
                    NodeList extensionsChilds = certChilds.item(i).getChildNodes();
                    for (int j = 0; j < extensionsChilds.getLength(); j++) {
                        switch (extensionsChilds.item(j).getNodeName()) {
                            case "description":
                                certDescNode = extensionsChilds.item(j); // If commCerts shall be integrated in certificate description, not possible until signKey fetched. So save for later processing ...
                                break;
                            case "terminalSector":
                                sectorKeys.addAll(getSectorKeysFromNode(extensionsChilds.item(j)));
                                break;
                        }
                    }
                }
                break;
                case "signKey": {
                    signKey = keys.get(certChilds.item(i).getTextContent());
                    if (signKey != null) {
                        cert.getSignKey().setAlgorithm(parseTAAlgorithm(signKey.getAlgorithm()));

                        PrivateKey key = signKey.getKeyPair().getPrivate();
                        cert.getSignKey().setKeySource(new PrivateKeySource(key));
                    } else {
                        logger.warn("Private key not found in key list!");
                        return null;
                    }
                }
                break;
                case "outputFile": {
                    outputFile = certChilds.item(i).getTextContent();

                    NamedNodeMap attributes = certChilds.item(i).getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        if (attributes.item(j).getNodeName().equals("createAdditionalHexFile")) {
                            createAdditionalHexFile = parseXSBoolean(attributes.item(j).getNodeValue());
                        }
                    }
                }
                break;
            }
        }

        // create extensions
        CVExtensionDataList extDataList = new CVExtensionDataList();

        if (certDescNode != null) {
            CertificateDescription desc = generateDescFromNode(certDescNode, parseTAAlgorithm(signKey.getAlgorithm()), subjectUrl); // use signkey information for hash algorithm
            if (desc != null) {
                CVExtensionData descExt = new CVExtensionData();
                descExt.setType(CVExtensionType.extDescription);
                // TODO: CHECK!
                // if this is being hashed, certificate output will be the hash and not the correct ASN1 certificate output, using desc.generates hex output directly
                DataBuffer hash = DataBuffer.generateHash(desc.generate(), parseTAAlgorithm(signKey.getAlgorithm())); // use signkey information for hash algorithm

                descExt.setHash1(hash);
                extDataList.add(descExt);
            } else {
                logger.warn("unable to generated/get certificate description ...");
            }
        }

        if (!sectorKeys.isEmpty()) {
            CVExtensionData descExt = new CVExtensionData();
            descExt.setType(CVExtensionType.extSector);
            for (int i = 0; i < sectorKeys.size(); i++) {
                if (sectorKeys.get(i) != null) {
                    DataBuffer hash = DataBuffer.generateHash(sectorKeys.get(i), parseTAAlgorithm(signKey.getAlgorithm())); // use signkey information for hash algorithm
                    switch (i) {
                        case 0:
                            descExt.setHash1(hash);
                            break;
                        case 1:
                            descExt.setHash2(hash);
                            break;
                        default:
                            logger.warn("unable to add sector key #" + (i + 1));
                    }
                }
            }
            extDataList.add(descExt);
        }

        if (!extDataList.isEmpty()) {
            CVExtension ext = new CVExtension();
            ext.setExtensions(extDataList);

            cert.setExtension(ext);
        }

        // save certificate
        DataBuffer rawCert = cert.generateCert();

        rawCert.writeToByteMap("CVCertificates/" + outputFile, certificatesAndKeys);


        if (createAdditionalHexFile) {
            certificatesAndKeys.put("CVCertificates/" + outputFile + ".hex", rawCert.asHexBinary().getBytes(StandardCharsets.UTF_8));
        }

        return cert;
    }

    /**
     * Generates certificate description from XML node.
     *
     * @param node      XML node
     * @param algorithm Algorithm information for hashing commCertificates.
     * @return Certificate description.
     * @throws Exception
     */
    protected CertificateDescription generateDescFromNode(Node node, TAAlgorithm algorithm, String subjectUrl) throws Exception {
        CertificateDescription desc = new CertificateDescription();
        desc.setSubjectURL(subjectUrl);
        String descFile = null;

        NodeList descChilds = node.getChildNodes();
        for (int i = 0; i < descChilds.getLength(); i++) {
            switch (descChilds.item(i).getNodeName()) {
                case "issuerName":
                    desc.setIssuerName(descChilds.item(i).getTextContent());
                    break;
                case "issuerURL":
                    desc.setIssuerURL(descChilds.item(i).getTextContent());
                    break;
                case "subjectName":
                    desc.setSubjectName(descChilds.item(i).getTextContent());
                    break;
//                case "subjectURL":
//                    desc.setSubjectURL(descChilds.item(i).getTextContent());
//                    break;
                case "termsOfUsage": {
                    String terms = descChilds.item(i).getTextContent();
                    desc.setPlainText(terms);
                }
                break;
                case "redirectURL":
                    desc.setRedirectURL(descChilds.item(i).getTextContent());
                    break;
                case "commCerts": {
                    NodeList commCertsChilds = descChilds.item(i).getChildNodes();
                    for (int j = 0; j < commCertsChilds.getLength(); j++) {
                        switch (commCertsChilds.item(j).getNodeName()) {
                            case "hashCommCert": {
                                String hashHexString = commCertsChilds.item(j).getTextContent();
                                if (hashHexString != null && !hashHexString.isEmpty()) {
                                    DataBuffer commHash = new DataBuffer();
                                    commHash.fromHexBinary(hashHexString);
                                    desc.addCommCertificates(commHash);
                                } else {
                                    logger.warn("Unable to read certificate hash from null/empty string");
                                }
                            }
                            break;
                        }
                    }
                }
                break;
                case "fileDescription":
                    descFile = descChilds.item(i).getTextContent();
                    break;
            }
        }
            if (descFile != null) {
                DataBuffer rawDesc = desc.generate();

                rawDesc.writeToByteMap("CVCertificates/" + descFile, certificatesAndKeys);

                logger.debug("description " + descFile + " saved.");
            }


        return desc;
    }

    /**
     * Gets sector keys from XML node.
     *
     * @param node XML node.
     * @return Sector key.
     * @throws Exception
     */
    protected LinkedList<DataBuffer> getSectorKeysFromNode(Node node) throws Exception {
        LinkedList<DataBuffer> out = new LinkedList<>();

        NodeList terminalSectorChilds = node.getChildNodes();
        for (int i = 0; i < terminalSectorChilds.getLength(); i++) {
            switch (terminalSectorChilds.item(i).getNodeName()) {
                case "fileSectorPublicKey":
                    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(terminalSectorChilds.item(i).getTextContent())) {
                        if (is != null) {
                            DataBuffer sectorKey = DataBuffer.fromInputStream(is);
                            out.add(sectorKey);
                        } else {
                            logger.warn("Unable to read terminal sector key from file " + terminalSectorChilds.item(i).getTextContent());
                        }
                    }
                    break;
                case "hexSectorPublicKey": {
                    String hashHexString = terminalSectorChilds.item(i).getTextContent();
                    if (hashHexString != null && !hashHexString.isEmpty()) {
                        DataBuffer sectorHash = new DataBuffer();
                        sectorHash.fromHexBinary(hashHexString);
                        out.add(sectorHash);
                    } else {
                        logger.warn("Unable to read sector key hash from null/empty string");
                    }
                }
            }
        }

        return out;
    }

    /**
     * Parses a xs:boolean value.
     *
     * @param str String to parse.
     * @return True if string is "true" or "1"
     */
    private boolean parseXSBoolean(String str) {
        return (str.equals("true") || str.equals("1"));
    }

    /**
     * Parses EC algorithm parameter from string.
     *
     * @param algorithm Algorithm name.
     * @return EC algorithm parameter.
     */
    protected AlgorithmParameterSpec parseECAlgorithm(String algorithm) {
        switch (algorithm) {
            case "ASN1::secp112r1":
                return ECCCurves.ASN1SECP112R1.getECParameter();
            case "ASN1::secp128r1":
                return ECCCurves.ASN1SECP128R1.getECParameter();
            case "ASN1::secp160r1":
                return ECCCurves.ASN1SECP160R1.getECParameter();
            case "ASN1::secp160k1":
                return ECCCurves.ASN1SECP160K1.getECParameter();
            case "ASN1::secp160r2":
                return ECCCurves.ASN1SECP160R2.getECParameter();
            case "ASN1::secp192k1":
                return ECCCurves.ASN1SECP192K1.getECParameter();
            case "ASN1::secp192r1":
                return ECCCurves.ASN1SECP192R1.getECParameter();
            case "ASN1::secp224k1":
                return ECCCurves.ASN1SECP224K1.getECParameter();
            case "ASN1::secp224r1":
                return ECCCurves.ASN1SECP224R1.getECParameter();
            case "ASN1::secp256k1":
                return ECCCurves.ASN1SECP256K1.getECParameter();
            case "ASN1::secp256r1":
                return ECCCurves.ASN1SECP256R1.getECParameter();
            case "ASN1::secp384r1":
                return ECCCurves.ASN1SECP384R1.getECParameter();
            case "ASN1::secp521r1":
                return ECCCurves.ASN1SECP521R1.getECParameter();
            case "BRAINPOOL::p160r1":
                return ECCCurves.BRAINPOOLP160R1.getECParameter();
            case "BRAINPOOL::p160t1":
                return ECCCurves.BRAINPOOLP160T1.getECParameter();
            case "BRAINPOOL::p192r1":
                return ECCCurves.BRAINPOOLP192R1.getECParameter();
            case "BRAINPOOL::p192t1":
                return ECCCurves.BRAINPOOLP192T1.getECParameter();
            case "BRAINPOOL::p224r1":
                return ECCCurves.BRAINPOOLP224R1.getECParameter();
            case "BRAINPOOL::p224t1":
                return ECCCurves.BRAINPOOLP224T1.getECParameter();
            case "BRAINPOOL::p256r1":
                return ECCCurves.BRAINPOOLP256R1.getECParameter();
            case "BRAINPOOL::p256t1":
                return ECCCurves.BRAINPOOLP256T1.getECParameter();
            case "BRAINPOOL::p320r1":
                return ECCCurves.BRAINPOOLP320R1.getECParameter();
            case "BRAINPOOL::p320t1":
                return ECCCurves.BRAINPOOLP320T1.getECParameter();
            case "BRAINPOOL::p384r1":
                return ECCCurves.BRAINPOOLP384R1.getECParameter();
            case "BRAINPOOL::p384t1":
                return ECCCurves.BRAINPOOLP384T1.getECParameter();
            case "BRAINPOOL::p512r1":
                return ECCCurves.BRAINPOOLP512R1.getECParameter();
            case "BRAINPOOL::p512t1":
                return ECCCurves.BRAINPOOLP512T1.getECParameter();
        }

        return null;
    }

    /**
     * Parses TA algorithm from string.
     *
     * @param algorithm Algorithm name.
     * @return TA algorithm.
     */
    protected TAAlgorithm parseTAAlgorithm(String algorithm) {
        switch (algorithm) {
            case "TA_RSA_v1_5_SHA_256":
                return TAAlgorithm.RSA_v1_5_SHA_256;
            case "TA_RSA_v1_5_SHA_512":
                return TAAlgorithm.RSA_v1_5_SHA_512;
            case "TA_RSA_PSS_SHA_256":
                return TAAlgorithm.RSA_PSS_SHA_256;
            case "TA_RSA_PSS_SHA_512":
                return TAAlgorithm.RSA_PSS_SHA_512;
            case "TA_ECDSA_SHA_256":
                return TAAlgorithm.ECDSA_SHA_256;
            case "TA_ECDSA_SHA_512":
                return TAAlgorithm.ECDSA_SHA_512;
        }

        return null;
    }

    /**
     * Sets relative date.
     *
     * @param date The date to set.
     */
    public void setDate(Date date) {
        if (date != null) {
            this.m_relDate = date;
        }
    }
}
