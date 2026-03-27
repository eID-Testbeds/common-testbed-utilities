package de.bsi.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if an OID is unknown or invalid 
 * @author meier.marcus
 *
 */
public class CVInvalidOidException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVInvalidOidException()
	{
		super("res:org.bsi.cvca.exception.CVInvalidOidException");
	}
}
