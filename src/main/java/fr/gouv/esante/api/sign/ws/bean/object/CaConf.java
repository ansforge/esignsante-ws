/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.gouv.esante.api.sign.ws.bean.object;

import fr.gouv.esante.api.sign.ws.bean.object.utils.CertificateFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * The type Ca conf.
 */
public class CaConf {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(CaConf.class);

    /**
     * certificate.
     */
    private String certificate;

    /**
     * crl.
     */
    private String crl;

    /**
     * Instantiates a new Ca conf.
     */
    public CaConf() {
    }

    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets certificate.
     *
     * @param certificate the certificate
     */
    public void setCertificate(final String certificate) {
        this.certificate = CertificateFormatting.formatCertificate(certificate);
    }

    /**
     * Gets crl.
     *
     * @return the crl
     */
    public String getCrl() {
        return crl;
    }

    /**
     * Sets crl.
     *
     * @param crl the crl
     */
    public void setCrl(final String crl) {
        this.crl = crl;
    }

    @Override
    public String toString() {
        return "CaConf{" +
                "certificate='" + certificate + '\'' +
                ", crl='" + crl + '\'' +
                '}';
    }

    /**
     * Check valid boolean.
     *
     * @return the boolean
     * @throws IllegalAccessException the illegal access exception
     */
    public boolean checkValid() throws IllegalAccessException {
        for (final Field f : getClass().getDeclaredFields()) {
            if (f.get(this) == null) {
                log.error("Missing field in object {}", this.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }
}
