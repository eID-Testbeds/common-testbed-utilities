package de.bsi.testbedutils.cvc.cvcertificate.exception;



/**
 * 
 * @author meier.marcus
 *
 */
public class CVInvalidKeySourceException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVInvalidKeySourceException()
	{
		super("res:org.bsi.cvca.exception.CVInvalidKeySourceException");
	}
}
