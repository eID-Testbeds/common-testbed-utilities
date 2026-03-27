package de.bsi.testbedutils.bouncycertgen;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import de.bsi.testbedutils.bouncycertgen.x509.CertificateRepresentation;
import de.bsi.testbedutils.bouncycertgen.xml.x509.CertificateDefinition;

public class GeneratedCertificate
{
	private final X509Certificate certificate;
	private final KeyPair keyPair;
	private final CertificateRepresentation definition;

	public GeneratedCertificate(CertificateRepresentation definition, X509Certificate certificate, KeyPair keyPair)
	{
		this.certificate = certificate;
		this.keyPair = keyPair;
		this.definition = definition;
	}

	public X509Certificate getCertificate()
	{
		return certificate;
	}

	public KeyPair getKeyPair()
	{
		return keyPair;
	}

	public CertificateRepresentation getDefinition()
	{
		return definition;
	}
}
