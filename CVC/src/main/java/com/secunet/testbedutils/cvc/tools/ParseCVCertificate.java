/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.testbedutils.cvc.tools;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import java.io.File;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * CLI to parse and display the CVC as String.
 *
 * @author Moieen Abbas
 */
public class ParseCVCertificate {

    /**
     * Main function.
     *
     * @param args
     */
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        if (args.length < 1) {
            System.out.println("No arguments ...");
            showHelp();
        } else {
            if (args[0].equals("-?")) {
                showHelp();
            } else {
                CVCertificate obj_CVCertificate = Utils.loadCVCertificate(new File(args[0]));
                System.out.println(obj_CVCertificate.toString());
            }
        }

    }

    /**
     * Shows help text.
     */
    private static void showHelp() {
        String runCmd = "java -jar " + ParseCVCertificate.class.getSimpleName();

        System.out.println();
        System.out.println("CommandLine Interface for viewing CV certificate.");

        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " <certificate file>");

        // Parameter
        System.out.println();
        System.out.println("-?\tShows this help.");
        System.out.println("<file>\tPath to CV Certificate file.");

        System.out.println();
    }
}
