package de.bsi.testbedutils.cvc.cvcertificate.exception;


/**
 * Something is wrong with certificate date
 *
 */
public class CVUnknownAlgorithmException extends CVBaseException
{
	static final long serialVersionUID = 1;

	/**
	 * @brief constructor
	 *
	 */
	public CVUnknownAlgorithmException()
	{
		super("res:org.bsi.cvca.exception.CVUnknownAlgorithmException");
	}
}
