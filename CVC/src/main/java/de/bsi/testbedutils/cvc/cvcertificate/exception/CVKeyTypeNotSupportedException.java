package de.bsi.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if a keytype isn't supported
 * @author meier.marcus
 *
 */
public class CVKeyTypeNotSupportedException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVKeyTypeNotSupportedException()
	{
		super("res:org.bsi.cvca.exception.CVKeyTypeNotSupportedException");
	}
	/**
	 * @brief 
	 *
	 * @param cause 
	 */
	public CVKeyTypeNotSupportedException(Throwable cause)
	{
		super("res:org.bsi.cvca.exception.CVKeyTypeNotSupportedException", cause);
	}
}
