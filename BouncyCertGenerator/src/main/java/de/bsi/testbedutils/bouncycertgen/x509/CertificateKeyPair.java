package de.bsi.testbedutils.bouncycertgen.x509;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class CertificateKeyPair {
    private X509Certificate certificate;
    private KeyPair keyPair;

    public CertificateKeyPair(X509Certificate certificate, KeyPair keyPair) {
        this.certificate = certificate;
        this.keyPair = keyPair;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
