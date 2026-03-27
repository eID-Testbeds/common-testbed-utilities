package de.bsi.testbedutils.cvc.cvcertificate.exception;


/**
 * Something is wrong with certificate date
 *
 */
public class CVUnknownCryptoProviderException extends CVBaseException
{
	static final long serialVersionUID = 1;

	/**
	 * @brief constructor
	 *
	 */
	public CVUnknownCryptoProviderException()
	{
		super("res:org.bsi.cvca.exception.CVUnknownCryptoProviderException");
	}
}
