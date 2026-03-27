package de.bsi.testbedutils.bouncycertgen.x509;

import de.bsi.testbedutils.bouncycertgen.GeneratedCertificate;
import de.bsi.testbedutils.bouncycertgen.KeyGenerator;
import de.bsi.testbedutils.bouncycertgen.asn1.*;
import de.bsi.testbedutils.bouncycertgen.exceptions.ListGenerationException;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.icao.CscaMasterList;
import org.bouncycastle.asn1.icao.ICAOObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static de.bsi.testbedutils.bouncycertgen.constants.GeneralConstants.*;

public class RuntimeX509CertificateFactory {

    private static final Logger logger = Logger.getLogger(RuntimeX509CertificateFactory.class.getName());

    Map<String, byte[]> certificatesAndKeys;
    Map<String, CertificateKeyPair> caCertificates;

    /**
     * Constructs a RuntimeX509CertificateFactory with provided certificates and keys.
     * Initializes the factory with a map of certificates and keys, along with specific sector and CSCA certificates and keys.
     * CSCA certificate and key are processed to generate a public-private key pair, which is then stored along with the CSCA certificate.
     * This constructor attempts to insert the sector certificates and CSCA certificate into the provided map and processes the CSCA certificate and key.
     *
     * @param certificatesAndKeys A map where the keys are the names/identifiers for certificates and keys, and the values are the byte arrays of the certificate or key content.
     * @param termSector1 The byte array of the TERM_SECTOR1 certificate content.
     * @param termSector2 The byte array of the TERM_SECTOR2 certificate content.
     */
    public RuntimeX509CertificateFactory(Map<String, byte[]> certificatesAndKeys, byte[] termSector1, byte[] termSector2) {
        this.certificatesAndKeys = certificatesAndKeys;
        caCertificates = new HashMap<>();
        certificatesAndKeys.put(TERM_SECTOR1_PATH, termSector1);
        certificatesAndKeys.put(TERM_SECTOR2_PATH, termSector2);
    }

    /**
     * Generates certificates and keys from an XML input stream, optionally utilizing a seed for key generation.
     * This method processes an XML file containing certificate specifications, generates the specified certificates and keys,
     * and organizes them into a map. It supports generating certificates using different key algorithms (EC, RSA, DSA) and
     * allows for the generation of self-signed certificates as well as certificates signed by an issuer specified in the XML.
     * Additionally, it supports various output formats (PEM, CRT, DER) and the inclusion of private keys and public keys in the output.
     *
     * @param xmlFileInputStream The input stream of the XML file containing certificate and key specifications.
     * @param seed A byte array used as a seed for key generation. This can influence the randomness and reproducibility of the generated keys.
     * @return A map where keys are paths/names for the generated certificates and keys, and values are the byte[] representations.
     *         Returns null if the input certificates list is null.
     * @throws NoSuchAlgorithmException If a key algorithm specified in the XML does not exist.
     * @throws IOException If there is an I/O error processing the input stream or writing the output.
     * @throws OperatorCreationException If there is an error creating an operator for certificate signing.
     * @throws CertificateException If there is an error processing certificate data.
     * @throws KeyStoreException If there is an error with the Java KeyStore when storing keys.
     */
    public Set<GeneratedCertificate> generateFromXML(InputStream xmlFileInputStream, byte[] seed, String subjectAltName)
            throws NoSuchAlgorithmException, IOException, OperatorCreationException, CertificateException, KeyStoreException {
        Security.addProvider(new BouncyCastleProvider());
        Set<GeneratedCertificate> generatedCertificates = new HashSet<>();
        List<CertificateRepresentation> certificates = parseXMLtoCertificateRepresentation(xmlFileInputStream);

        if (certificates == null) return null;

        // loop over all certificates provided in data
        for(CertificateRepresentation certificateRepresentation : certificates) {
            X509Certificate certificate;
            StringWriter stringWriter;
            KeyPair keyPair = switch (certificateRepresentation.keyAlgorithm) {
                case EC ->
                        KeyGenerator.generateECPair(certificateRepresentation.keyCurve, seed, certificateRepresentation.explicitCurveParams);
                case RSA -> KeyGenerator.generateRSAPair(certificateRepresentation.keySize, seed);
                case DSA -> KeyGenerator.generateDSAPair(certificateRepresentation.keySize, seed);
                default -> null;
            };

            if(keyPair == null) continue;

            // if there is no issuer name in the provided data, Cert should be self-signed
            if (Strings.isBlank(certificateRepresentation.issuerName)) {
                certificate = generateSelfSignedCertificate(
                        keyPair,
                        certificateRepresentation.subjectName,
                        certificateRepresentation.certAlgorithm,
                        certificateRepresentation.extensionCritical,
                        certificateRepresentation.keyUsageIsCritical,
                        certificateRepresentation.digitalSignature,
                        certificateRepresentation.keyEncipherment,
                        certificateRepresentation.keyAgreement,
                        certificateRepresentation.certificateSigning,
                        certificateRepresentation.crlSign,
                        certificateRepresentation.nonRepudiation,
                        certificateRepresentation.AKI,
                        certificateRepresentation.SKI,
                        certificateRepresentation.EKU,
                        certificateRepresentation.NCT,
                        certificateRepresentation.NC,
                        certificateRepresentation.includeSubjectAltName ? subjectAltName : null
                );
                caCertificates.put(certificateRepresentation.subjectName, new CertificateKeyPair(certificate, keyPair));
            }
            // if not, create signed cert and get signing from hash-table that temporarily stores all self-signed certs
            else {
                CertificateKeyPair issuerCertificateKeyPair = caCertificates.get(certificateRepresentation.issuerName);
                if (issuerCertificateKeyPair != null) {
                    certificate = generateCertificate(
                            issuerCertificateKeyPair,
                            keyPair.getPublic(),
                            certificateRepresentation.subjectName,
                            certificateRepresentation.extensionCritical,
                            certificateRepresentation.keyUsageIsCritical,
                            certificateRepresentation.digitalSignature,
                            certificateRepresentation.keyEncipherment,
                            certificateRepresentation.keyAgreement,
                            certificateRepresentation.certificateSigning,
                            certificateRepresentation.crlSign,
                            certificateRepresentation.nonRepudiation,
                            certificateRepresentation.AKI,
                            certificateRepresentation.SKI,
                            certificateRepresentation.EKU,
                            certificateRepresentation.NCT,
                            certificateRepresentation.NC,
                            certificateRepresentation.includeSubjectAltName ? subjectAltName : null
                    );
                } else {
                    logger.warning("Could not find a fitting issuer certificate/keypair to generate certificate: " + certificateRepresentation.subjectName);
                    continue;
                }
            }
            // Write as .pem-Cert instead of DER if requested
            if (certificateRepresentation.asPEM) {
                stringWriter = new StringWriter();
                try (JcaPEMWriter writer = new JcaPEMWriter(stringWriter)) {
                    writer.writeObject(certificate);
                }
                certificatesAndKeys.put(CERTIFICATES_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + PEM_EXT,
                        stringWriter.toString().getBytes());
                stringWriter.close();
            } // else if PEM ist requested, write as PEM
            if (certificateRepresentation.asCRT) { //TODO: check else if
                stringWriter = new StringWriter();
                try (JcaPEMWriter writer = new JcaPEMWriter(stringWriter)) {
                    writer.writeObject(certificate);
                }
                certificatesAndKeys.put(CERTIFICATES_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + CRT_EXT,
                        stringWriter.toString().getBytes());
                stringWriter.close();
            }
            // if not, write as .DER
            else {
                certificatesAndKeys.put(CERTIFICATES_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + DER_EXT,
                        certificate.getEncoded());
            }

            // if the Cert should be stored with a key, write the key either as .DER or .KEY
            if (certificateRepresentation.withKey) {
                if (certificateRepresentation.asKey) {
                    stringWriter = new StringWriter();
                    try (JcaPEMWriter writer = new JcaPEMWriter(stringWriter)) {
                        writer.writeObject(keyPair.getPrivate());
                    }
                    certificatesAndKeys.put(KEYS_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + KEY_EXT,
                            stringWriter.toString().getBytes());
                    stringWriter.close();
                } else { // if its one of the CSCA-Keys, _privKey should be appended to the string
                    certificatesAndKeys.put(KEYS_DIR + certificateRepresentation.directory + certificateRepresentation.fileName +
                                    (certificateRepresentation.directory.equals(CSCA_DIR) ? PRIV_KEY_DER_SUFFIX : DER_EXT),
                            keyPair.getPrivate().getEncoded());
                }

                if (certificateRepresentation.keyAsPem) {
                    stringWriter = new StringWriter();
                    try (JcaPEMWriter writer = new JcaPEMWriter(stringWriter)) {
                        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());

                        writer.writeObject(new PemObject("PRIVATE KEY", pkcs8EncodedKeySpec.getEncoded()));
                    }
                    certificatesAndKeys.put(KEYS_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + PEM_EXT,
                            stringWriter.toString().getBytes(StandardCharsets.UTF_8));
                    stringWriter.close();
                }
                if (certificateRepresentation.storePublicKey) {
                    stringWriter = new StringWriter();
                    try (JcaPEMWriter writer = new JcaPEMWriter(stringWriter)) {
                        writer.writeObject(keyPair.getPublic());
                    }
                    certificatesAndKeys.put(KEYS_DIR + certificateRepresentation.directory + certificateRepresentation.fileName + PUB_KEY_SUFFIX,
                            stringWriter.toString().getBytes());
                    stringWriter.close();
                }

                String keyAlias = null, storePath = null;
                switch (certificateRepresentation.fileName) {
                    case "F_ENC":
                        keyAlias = "enc";
                        storePath = "keystores/RSA/F_ENC.p12";
                        break;
                    case "F":
                        keyAlias = "sig";
                        storePath = "keystores/ECDSA/F.p12";

                        // special case, as this is needed for eIDAS Middleware configuration
                        certificatesAndKeys.put("eIDAS/crypt-material/provider-signCert.pem", certificatesAndKeys.get("certificates/ECDSA/F.crt"));

                        break;
                    case "A":
                        keyAlias = "signature.eid-acme_a.com";
                        storePath = "eIDAS/crypt-material/A.p12";
                        break;
                }

                if (keyAlias != null) {
                    KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
                    pkcs12.load(null, null);
                    pkcs12.setKeyEntry(keyAlias, keyPair.getPrivate(), "123456".toCharArray(), new Certificate[]{certificate});
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    pkcs12.store(baos, "123456".toCharArray());
                    certificatesAndKeys.put(storePath, baos.toByteArray());
                }
            }
            generatedCertificates.add(new GeneratedCertificate(certificateRepresentation, certificate, keyPair));
        }

        return generatedCertificates;
    }

    public byte[] generateMasterListAndStore() {
        byte[] masterList = generateMasterList(certificatesAndKeys.get(CSCA_CERTIFICATE_PATH),
                certificatesAndKeys.get(CERTIFICATES_CSCA_DIR + "CERT_ECARD_CSCA_1_MASTERLIST_SIGNER.DER"),
                certificatesAndKeys.get(KEYS_CSCA_DIR + "CERT_ECARD_CSCA_1_MASTERLIST_SIGNER_privKey.DER"));

        certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + MASTERLIST_FILENAME, masterList);
        return masterList;
    }

    public byte[] generateDefectListAndStore() {
        byte[] defectlist = generateDefectlist(certificatesAndKeys.get(CERTIFICATES_CSCA_DIR + "CERT_ECARD_DS_1_B.DER"),
                certificatesAndKeys.get(CERTIFICATES_CSCA_DIR + "CERT_ECARD_CSCA_1_DEFECTLIST_SIGNER.DER"),
                certificatesAndKeys.get(KEYS_CSCA_DIR + "CERT_ECARD_CSCA_1_DEFECTLIST_SIGNER_privKey.DER"));

        certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + DEFECTLIST_FILENAME, defectlist);
        return defectlist;
    }

    private BlackListDetails generateBlackListDetails(String sectorCert, String sectorId) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] mdX = md.digest(certificatesAndKeys.get(CVCERTIFICATES_KEYS_DIR + sectorCert));
        byte[] sectorIdX = Hex.decode(sectorId);
        return new BlackListDetails(mdX, List.of(sectorIdX));
    }

    public byte[] generateBlackListAndStore(char EService) throws NoSuchAlgorithmException {
        List<BlackListDetails> blackListDetails = switch (EService) {
            case 'D' ->
                    List.of(generateBlackListDetails("TERM_SECTOR2.x509.cv", "4242424242424242424242424242424242424242424242424242424242424242"));
            case 'E' -> List.of();
            default ->
                    List.of(generateBlackListDetails("TERM_SECTOR1.x509.cv", "4444444444444444444444444444444444444444444444444444444444444444"));
        };

        byte[] blacklist = generateBlacklist(blackListDetails,
                certificatesAndKeys.get(CERTIFICATES_CSCA_DIR + "CERT_ECARD_CSCA_1_BLACKLIST_SIGNER.DER"),
                certificatesAndKeys.get(KEYS_CSCA_DIR + "CERT_ECARD_CSCA_1_BLACKLIST_SIGNER_privKey.DER"));

        switch (EService) {
            case 'D':
                certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + BLACKLIST_D_FILENAME, blacklist);
                break;
            case 'E':
                certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + BLACKLIST_EDSA_FILENAME, blacklist);
                certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + BLACKLIST_ERSA_FILENAME, blacklist);
                certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + BLACKLIST_EECDSA_FILENAME, blacklist);
                break;
            default:
                certificatesAndKeys.put(CERTIFICATES_CSCA_DIR + BLACKLIST_X_FILENAME, blacklist);
                break;
        }
        return blacklist;
    }

    /**
     * Parses XML into a certificate representation, from where it can generate the certificates
     *
     * @param inputStream Input stream of XML data
     * @return List of CertificateRepresentations
     */
    public List<CertificateRepresentation> parseXMLtoCertificateRepresentation(InputStream inputStream) {
        List<CertificateRepresentation> certificates = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Source xsdSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("X509CertificateGeneratorList.xsd"));
            Schema xsd = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsdSource);
            dbFactory.setSchema(xsd);
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Certificate");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Element keyElement = (Element) eElement.getElementsByTagName("Key").item(0);
                    Element storageElement = (Element) eElement.getElementsByTagName("Storage").item(0);
                    Element usageElement = (Element) eElement.getElementsByTagName("Usage").item(0);
                    Element netscapeElement = (Element) eElement.getElementsByTagName("Netscape").item(0);

                    String algorithm = getTextContent(keyElement, "Algorithm");
                    Object sizeOrCurveValue;
                    if ("EC".equals(algorithm)) {
                        sizeOrCurveValue = getTextContent(eElement, "Curve");
                    } else {
                        sizeOrCurveValue = Integer.parseInt(Objects.requireNonNull(getTextContent(keyElement, "Size")));
                    }

                    certificates.add(new CertificateRepresentation(
                            getTextContent(eElement, "IssuerName"),
                            getTextContent(eElement, "SubjectName"),
                            getTextContent(eElement, "FileName"),
                            algorithm,
                            sizeOrCurveValue,
                            getTextContent(eElement, "CertAlgorithm"),
                            getTextContent(storageElement, "Directory"),
                            parseBoolean(storageElement, "AsPEM"),
                            parseBoolean(storageElement, "AsCRT"),
                            parseBoolean(keyElement, "WithKey"),
                            parseBoolean(keyElement, "AsKey"),
                            parseBoolean(keyElement, "AsPEM"),
                            parseBoolean(storageElement, "StorePublicKey"),
                            parseBoolean(usageElement, "ExtensionCritical"),
                            parseBoolean(usageElement, "KeyUsageIsCritical"),
                            parseBoolean(usageElement, "DigitalSignature"),
                            parseBoolean(usageElement, "KeyEncipherment"),
                            parseBoolean(usageElement, "KeyAgreement"),
                            parseBoolean(usageElement, "CertificateSigning"),
                            parseBoolean(usageElement, "CrlSign"),
                            parseBoolean(usageElement, "NonRepudiation"),
                            parseBoolean(usageElement, "AuthorityKeyIdentifier"),
                            parseBoolean(usageElement, "SubjectKeyIdentifier"),
                            IntStream.range(0, eElement.getElementsByTagName("KeyPurposeId").getLength())
                                    .mapToObj(i -> eElement.getElementsByTagName("KeyPurposeId").item(i))
                                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                                    .map(node -> KeyPurposeId.getInstance(new ASN1ObjectIdentifier(node.getTextContent())))
                                    .toArray(KeyPurposeId[]::new), // EKU handling via stream api
                            parseBoolean(netscapeElement, "CertificateType"),
                            getTextContent(netscapeElement, "Comment"),
                            parseBoolean(eElement, "IncludeSubjectAltName"),
                            parseBoolean(eElement, "ExplicitCurveParams")
                    ));
                }
            }
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Parser configuration error", e);
            throw new RuntimeException("Failed to configure parser", e);
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "Parsing error", e);
            throw new RuntimeException("Failed to parse XML document", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error", e);
            throw new RuntimeException("IO error while reading XML", e);
        }

        return certificates;
    }

    private boolean parseBoolean(Element parentElement, String childElementName) {
        if (parentElement.getElementsByTagName(childElementName).getLength() == 0) return false;
        return Boolean.parseBoolean((parentElement.getElementsByTagName(childElementName).item(0)).getTextContent());
    }

    private String getTextContent(Element parentElement, String childElementName) {
        if (parentElement.getElementsByTagName(childElementName).getLength() == 0) return null;
        return parentElement.getElementsByTagName(childElementName).item(0).getTextContent();
    }

    private X509Certificate generateSelfSignedCertificate(@NotNull KeyPair keyPair, String commonName, String signatureAlgorithm, boolean extensionCritical, boolean keyUsageIsCritical,
                                                          boolean digitalSignature, boolean keyEncipherment, boolean keyAgreement, boolean certificateSigning, boolean crlSign,
                                                          boolean nonRepudiation, boolean aki, boolean ski, KeyPurposeId[] eku, boolean nct, String nc, String subjectAltName)
            throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException {
        Date now = new Date();
        Date validityEndDate = new Date(now.getTime() + (3L * 365 * 24 * 60 * 60 * 1000)); // 3 years

        X500Name issuerAndSubjectName = createX500Name(commonName);  // issuer and subject are the same for self-signed

        BigInteger serialNumber = new BigInteger(new SecureRandom().nextInt() + "");

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                issuerAndSubjectName,
                serialNumber,
                now,
                validityEndDate,
                issuerAndSubjectName,
                keyPair.getPublic());

        addExtensions(certificateBuilder, keyPair.getPublic(), keyPair.getPublic(), true, extensionCritical, keyUsageIsCritical, digitalSignature, keyEncipherment,
                keyAgreement, certificateSigning, crlSign, nonRepudiation, aki, ski, eku, nct, nc, subjectAltName);

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(new BouncyCastleProvider()).build(keyPair.getPrivate());
        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateHolder);
    }

    public X509Certificate generateCertificate(@NotNull CertificateKeyPair issuerCertificateKeyPair, @NotNull PublicKey subjectPublicKey, String subjectName,
                                               boolean extensionCritical, boolean keyUsageIsCritical, boolean digitalSignature,
                                               boolean keyEncipherment, boolean keyAgreement, boolean certificateSigning, boolean crlSign, boolean nonRepudiation, boolean aki,
                                               boolean ski, KeyPurposeId[] eku, boolean nct, String nc, String subjectAltName)
            throws NoSuchAlgorithmException, OperatorCreationException, CertificateException, CertIOException {
        Date validityBeginDate = new Date();
        Date validityEndDate = new Date(System.currentTimeMillis() + (3L * 365 * 24 * 60 * 60 * 1000)); // 3 years

        JcaX509CertificateHolder holder = new JcaX509CertificateHolder(issuerCertificateKeyPair.getCertificate());

        X500Name issuer = holder.getSubject();
        X500Name subject = createX500Name(subjectName);

        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                issuer, serialNumber, validityBeginDate, validityEndDate, subject, subjectPublicKey
        );
        addExtensions(certificateBuilder, subjectPublicKey, issuerCertificateKeyPair.getKeyPair().getPublic(), false, extensionCritical, keyUsageIsCritical, digitalSignature, keyEncipherment,
                keyAgreement, certificateSigning, crlSign, nonRepudiation, aki, ski, eku, nct, nc, subjectAltName);

        ContentSigner contentSigner = new JcaContentSignerBuilder(issuerCertificateKeyPair.getCertificate().getSigAlgName()).setProvider(new BouncyCastleProvider()).build(issuerCertificateKeyPair.getKeyPair().getPrivate());
        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateHolder);
    }

    private byte[] signList(CMSProcessableByteArray data, byte[] signerCertBytes, byte[] privateKeyBytes) {
        try {
            X509CertificateHolder signerCert = new X509CertificateHolder(signerCertBytes);
            PrivateKey privateKey = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            CMSSignedDataGenerator cmsGenerator = new CMSSignedDataGenerator();
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA224withECDSA").setProvider(new BouncyCastleProvider()).build(privateKey);
            SignerInfoGenerator sigGen = new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().build())
                    .build(contentSigner, signerCert);
            final CMSAttributeTableGenerator sAttrGen = sigGen.getSignedAttributeTableGenerator();
            cmsGenerator.addCertificates(new JcaCertStore(List.of(signerCert)));
            cmsGenerator.addSignerInfoGenerator(new SignerInfoGenerator(sigGen
                    , parameters -> sAttrGen.getAttributes(parameters).remove(CMSAttributes.cmsAlgorithmProtect).remove(CMSAttributes.signingTime), sigGen.getUnsignedAttributeTableGenerator())
            );

            CMSSignedData signedData = cmsGenerator.generate(data, true);

            return signedData.getEncoded();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | OperatorCreationException | CertificateException | CMSException e) {
            throw new ListGenerationException("Error signing the list", e);
        }
    }

    public byte[] generateMasterList(byte[] cscaCertBytes, byte[] masterlistSignerCertBytes, byte[] privateKeyBytes) {
        try {
            X509CertificateHolder cscaCert = new X509CertificateHolder(cscaCertBytes);
            org.bouncycastle.asn1.x509.Certificate[] certificates = new org.bouncycastle.asn1.x509.Certificate[] {cscaCert.toASN1Structure()};

            CscaMasterList masterList = new CscaMasterList(certificates);

            CMSProcessableByteArray processableData  = new CMSProcessableByteArray(ICAOObjectIdentifiers.id_icao_cscaMasterList, masterList.getEncoded());

            return signList(processableData, masterlistSignerCertBytes, privateKeyBytes);

        } catch (IOException e) {
            throw new ListGenerationException("Error generating the masterlist", e);
        }
    }

    public byte[] generateDefectlist(byte[] defectCertBytes, byte[] defectlistSignerCertBytes, byte[] privateKeyBytes) {
        try {
            X509CertificateHolder defectCert = new X509CertificateHolder(defectCertBytes);

            DefectList defectList = new DefectList(NISTObjectIdentifiers.id_sha256, new Defect[] {new Defect(defectCert, new KnownDefect[] {new KnownDefect(BSIObjectIdentifiers.certRevoked)})});

            CMSProcessableByteArray processableData = new CMSProcessableByteArray(BSIObjectIdentifiers.DefectList, defectList.getEncoded());

            return signList(processableData, defectlistSignerCertBytes, privateKeyBytes);

        } catch (IOException e) {
            throw new ListGenerationException("Error generating the defectlist", e);
        }
    }

    public byte[] generateBlacklist(List<BlackListDetails> entries, byte[] blacklistSignerCertBytes, byte[] privateKeyBytes) {
        try {
            BlackList blackList = new BlackList(entries.toArray(new BlackListDetails[0]));

            CMSProcessableByteArray processableData = new CMSProcessableByteArray(BSIObjectIdentifiers.BlackList, blackList.getEncoded());

            return signList(processableData, blacklistSignerCertBytes, privateKeyBytes);
        } catch (IOException e) {
            throw new ListGenerationException("Error generating the blacklist", e);
        }
    }

    private X500Name createX500Name(String commonName) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.OU, "bsi")
                .addRDN(BCStyle.O, "bund")
                .addRDN(BCStyle.C, "DE")
                .addRDN(BCStyle.CN, commonName);

        if(commonName.equals("f.testbed.de")) { // special case, because this certificate is needed as provider-signCert on eIDAS Middleware
            builder = builder
                    .addRDN(new ASN1ObjectIdentifier("2.5.29.17"), "DNS.1\\=f.testbed.de")
                    .addRDN(BCStyle.L, "Bonn")
                    .addRDN(BCStyle.ST, "NRW");
        }

        return builder.build();
    }

    private void addExtensions(X509v3CertificateBuilder certificateBuilder, PublicKey subjectPublicKey, PublicKey authorityPublicKey, boolean isCA,
                               boolean extensionCritical, boolean keyUsageIsCritical,
                               boolean digitalSignature, boolean keyEncipherment, boolean keyAgreement, boolean certificateSigning, boolean crlSign,
                               boolean nonRepudiation, boolean aki, boolean ski, KeyPurposeId[] eku, boolean nct, String nc, String subjectAltName)
            throws CertIOException, NoSuchAlgorithmException {

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        // Basic Constraints
        certificateBuilder.addExtension(Extension.basicConstraints, extensionCritical, new BasicConstraints(isCA));


        // Key Usage
        int keyUsage = (digitalSignature ? KeyUsage.digitalSignature : 0) |
                (keyEncipherment ? KeyUsage.keyEncipherment : 0) |
                (keyAgreement ? KeyUsage.keyAgreement : 0) |
                (certificateSigning ? KeyUsage.keyCertSign : 0) |
                (crlSign ? KeyUsage.cRLSign : 0) |
                (nonRepudiation ? KeyUsage.nonRepudiation : 0);
        if (keyUsage != 0) {
            certificateBuilder.addExtension(Extension.keyUsage, keyUsageIsCritical, new KeyUsage(keyUsage));
        }

        // Authority and Subject Key Identifier
        if (aki) {
            certificateBuilder.addExtension(Extension.authorityKeyIdentifier, extensionCritical,
                    extUtils.createAuthorityKeyIdentifier(isCA ? subjectPublicKey : authorityPublicKey));
        }
        if (ski) {
            certificateBuilder.addExtension(Extension.subjectKeyIdentifier, extensionCritical,
                    extUtils.createSubjectKeyIdentifier(subjectPublicKey));
        }

        // Extended Key Usage
        if (eku != null && eku.length > 0) {
            certificateBuilder.addExtension(Extension.extendedKeyUsage, extensionCritical, new ExtendedKeyUsage(eku));
        }

        // Subject Alternative Name
        if (subjectAltName != null && !subjectAltName.isBlank()) {
            certificateBuilder.addExtension(Extension.subjectAlternativeName, extensionCritical,
                    new GeneralNames(new GeneralName(GeneralName.dNSName, subjectAltName)));
        }

        // Netscape Cert Type
        if (nct) {
            int netscapeCertTypes = NetscapeCertType.sslClient | NetscapeCertType.smime;
            certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.16.840.1.113730.1.1"), extensionCritical,
                    new DERBitString(new byte[]{(byte) netscapeCertTypes}));
        }

        // Netscape Comment
        if (nc != null && !nc.isBlank()) {
            certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.16.840.1.113730.1.13"), extensionCritical,
                    new DERIA5String(nc));
        }
    }

}