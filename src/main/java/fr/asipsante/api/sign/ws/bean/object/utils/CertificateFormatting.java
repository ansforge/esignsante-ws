/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.bean.object.utils;

/**
 * The type Certificate formatting.
 */
public class CertificateFormatting {

    /**
     * Format certificate string.
     *
     * @param certificate the certificate
     * @return the string
     */
    public static String formatCertificate(final String certificate) {
        final String encodedText = certificate.replaceAll("\\s+","")
                .replaceAll("(-----BEGINCERTIFICATE-----)", "")
                .replaceAll("(-----ENDCERTIFICATE-----)", "");
        final String formattedEncodedText = insertPeriodically(encodedText, "\n", 64);
        return "-----BEGIN CERTIFICATE-----\n" + formattedEncodedText + "\n-----END CERTIFICATE-----";
    }

    /**
     * Format private key string.
     *
     * @param privateKey the private key
     * @return the string
     */
    public static String formatPrivateKey(final String privateKey) {
        if (privateKey.trim().startsWith("-----BEGIN RSA PRIVATE KEY-----")) {
            return formatPKCS1PrivateKey(privateKey);
        } else {
            return formatPKCS8PrivateKey(privateKey);
        }
    }

    private static String formatPKCS8PrivateKey(final String privateKey) {
        final String encodedText = privateKey.replaceAll("\\s+","")
                .replaceAll("(-----BEGINPRIVATEKEY-----)", "")
                .replaceAll("(-----ENDPRIVATEKEY-----)", "");
        final String formattedEncodedText = insertPeriodically(encodedText, "\n", 64);
        return "-----BEGIN PRIVATE KEY-----\n" + formattedEncodedText + "\n-----END PRIVATE KEY-----";
    }

    private static String formatPKCS1PrivateKey(final String privateKey) {
        final String encodedText = privateKey.replaceAll("\\s+","")
                .replaceAll("(-----BEGINRSAPRIVATEKEY-----)", "")
                .replaceAll("(-----ENDRSAPRIVATEKEY-----)", "");
        final String formattedEncodedText = insertPeriodically(encodedText, "\n", 64);
        return "-----BEGIN RSA PRIVATE KEY-----\n" + formattedEncodedText + "\n-----END RSA PRIVATE KEY-----";
    }

    /**
     * Insert periodically string.
     *
     * @param text   the text
     * @param insert the insert
     * @param period the period
     * @return the string
     */
    public static String insertPeriodically(final String text, final String insert, final int period) {
        final StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length()/period)+1);

        int index = 0;
        String prefix = "";
        while (index < text.length())
        {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

}
