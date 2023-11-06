/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.gouv.esante.api.sign.ws.bean.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.gouv.esante.api.sign.bean.parameters.SignatureValidationParameters;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationLoader;
import fr.gouv.esante.api.sign.ws.bean.object.utils.RulesFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * The type Sign verif conf.
 */
public class SignVerifConf {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(SignVerifConf.class);

    /**
     * signVerifParams.
     */
    @JsonIgnore
    private SignatureValidationParameters signVerifParams;

    /**
     * idVerifSign.
     */
    private String idVerifSign;

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
     * Gets sign verif params.
     *
     * @return the sign verif params
     */
    public SignatureValidationParameters getSignVerifParams() {
        return signVerifParams;
    }

    /**
     * Sets sign verif params.
     *
     * @param signVerifParams the sign verif params
     */
    public void setSignVerifParams(final SignatureValidationParameters signVerifParams) {
        this.signVerifParams = signVerifParams;
    }

    /**
     * Instantiates a new Sign verif conf.
     */
    public SignVerifConf() {
    }

    /**
     * Gets id verif sign.
     *
     * @return the id verif sign
     */
    public String getIdVerifSign() {
        return idVerifSign;
    }

    /**
     * Sets id verif sign.
     *
     * @param idVerifSign the id verif sign
     */
    public void setIdVerifSign(final String idVerifSign) {
        this.idVerifSign = idVerifSign;
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
        this.rules = RulesFormatting.formatSignRules(rules);
    }

    @Override
    public String toString() {
        return "SignVerifConf{" +
                "idVerifSign='" + idVerifSign + '\'' +
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
            if (!"signVerifParams".equals(f.getName()) && f.get(this) == null) {
                log.error("Missing field {} in object {}", f.getName() ,this.getClass().getSimpleName());
                return false;
            }
        }
        signVerifParams = ConfigurationLoader.loadSignVerifConf(this);
        return true;
    }

}
