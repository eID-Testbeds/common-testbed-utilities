package de.bsi.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if a authority or a holder reference is to long  
 * @author meier.marcus
 *
 */
public class CVAuthorityRefNotValidException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * 
	 * @brief constructor 
	 *
	 */
	public CVAuthorityRefNotValidException()
	{
		super("res:org.bsi.cvca.exception.CVAuthorityRefNotValidException");
	}
}
