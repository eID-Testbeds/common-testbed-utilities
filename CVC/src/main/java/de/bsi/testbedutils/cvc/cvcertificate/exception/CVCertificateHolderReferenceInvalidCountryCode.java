package de.bsi.testbedutils.cvc.cvcertificate.exception;



/**
 * This exception is thrown if the county code of the certificate holder is no two-characters String
 * @author hellrung.andreas
 *
 */
public class CVCertificateHolderReferenceInvalidCountryCode extends CVBaseException
{
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVCertificateHolderReferenceInvalidCountryCode()
	{
		super("res:org.bsi.cvca.exception.CVCertificateHolderReferenceInvalidCountryCode");
	}
		
}
