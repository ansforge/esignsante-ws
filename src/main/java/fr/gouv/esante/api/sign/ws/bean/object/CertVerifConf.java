/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.gouv.esante.api.sign.ws.bean.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.gouv.esante.api.sign.bean.parameters.CertificateValidationParameters;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationLoader;
import fr.gouv.esante.api.sign.ws.bean.object.utils.RulesFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * The type Cert verif conf.
 */
public class CertVerifConf {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(CertVerifConf.class);

    /**
     * idVerifCert.
     */
    @JsonIgnore
    private CertificateValidationParameters certVerifParams;

    /**
     * idVerifCert.
     */
    private String idVerifCert;

    /**
     * description.
     */
    private String description;

    /**
     * metadata.
     */
    private String metadata;

    /**
     * rules.
     */
    private String rules;

    /**
     * Instantiates a new Cert verif conf.
     */
    public CertVerifConf() {
    }

    /**
     * Gets cert verif params.
     *
     * @return the cert verif params
     */
    public CertificateValidationParameters getCertVerifParams() {
        return certVerifParams;
    }

    /**
     * Sets cert verif params.
     *
     * @param certVerifParams the cert verif params
     */
    public void setCertVerifParams(final CertificateValidationParameters certVerifParams) {
        this.certVerifParams = certVerifParams;
    }

    /**
     * Gets id verif cert.
     *
     * @return the id verif cert
     */
    public String getIdVerifCert() {
        return idVerifCert;
    }

    /**
     * Sets id verif cert.
     *
     * @param idVerifCert the id verif cert
     */
    public void setIdVerifCert(final String idVerifCert) {
        this.idVerifCert = idVerifCert;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(final String metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets rules.
     *
     * @return the rules
     */
    public String getRules() {
        return rules;
    }

    /**
     * Sets rules.
     *
     * @param rules the rules
     */
    public void setRules(final String rules) {
        this.rules = RulesFormatting.formatCertRules(rules);
    }

    @Override
    public String toString() {
        return "CertVerifConf{" +
                "idVerifCert='" + idVerifCert + '\'' +
                ", description='" + description + '\'' +
                ", metadata='" + metadata + '\'' +
                ", rules='" + rules + '\'' +
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
            if (!"certVerifParams".equals(f.getName()) && f.get(this) == null) {
                log.error("Missing field {} in object {}", f.getName() ,this.getClass().getSimpleName());
                return false;
            }
        }
        certVerifParams = ConfigurationLoader.loadCertVerifConf(this);
        return true;
    }

}
