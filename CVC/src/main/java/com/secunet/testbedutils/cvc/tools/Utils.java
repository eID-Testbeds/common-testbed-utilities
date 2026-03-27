package com.secunet.testbedutils.cvc.tools;

import java.io.File;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import java.io.IOException;

/**
 * Some CVCertificate utils.
 *
 */
public class Utils {

    /**
     * Loads CV certificate from file.
     *
     * @param certificateFile Certificate file.
     * @return CV certificate.
     */
    public static CVCertificate loadCVCertificate(File certificateFile) {
        CVCertificate result = null;

        if (certificateFile == null) {
            return null;
        }

        if (!certificateFile.exists()) {
            System.out.println("File does not exist: " + certificateFile.getAbsolutePath());
            return null;
        }

        DataBuffer rawCert = null;
        try {
            rawCert = DataBuffer.readFromFile(certificateFile.getAbsolutePath());
            result = new CVCertificate(rawCert);
            System.out.println("Loaded " + result.getCertHolderRef() + " from " + certificateFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Unable to read CV certificate from file:" + e.getMessage());
            return null;
        } catch (Exception e) {
            rawCert = DataBuffer.decodeB64(new String(rawCert.toByteArray()));
            try {
                result = new CVCertificate(rawCert);
            } catch (Exception ex) {
                System.out.println("Unable to read CV certificate from file:" + e.getMessage());
                result = null;

            }
        }

        return result;
    }
}
