/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.asipsante.api.sign.ws.bean.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.asipsante.api.sign.bean.parameters.SignatureParameters;
import fr.asipsante.api.sign.ws.bean.ConfigurationLoader;
import fr.asipsante.api.sign.ws.bean.object.utils.CertificateFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * The type Proof conf.
 */
public class ProofConf {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(ProofConf.class);

    /**
     * signProofParams.
     */
    @JsonIgnore
    private SignatureParameters signProofParams;

    /**
     * idProofConf.
     */
    private String idProofConf;

    /**
     * description.
     */
    private String description;

    /**
     * certificate.
     */
    private String certificate;

    /**
     * privateKey.
     */
    private String privateKey;

    /**
     * canonicalisationAlgorithm.
     */
    private String canonicalisationAlgorithm;

    /**
     * digestAlgorithm.
     */
    private String digestAlgorithm;

    /**
     * signaturePackaging.
     */
    private String signaturePackaging;

    /**
     * Instantiates a new Proof conf.
     */
    public ProofConf() {
    }

    /**
     * Gets sign proof params.
     *
     * @return the sign proof params
     */
    public SignatureParameters getSignProofParams() {
        return signProofParams;
    }

    /**
     * Sets sign proof params.
     *
     * @param signProofParams the sign proof params
     */
    public void setSignProofParams(final SignatureParameters signProofParams) {
        this.signProofParams = signProofParams;
    }

    /**
     * Gets id proof conf.
     *
     * @return the id proof conf
     */
    public String getIdProofConf() {
        return idProofConf;
    }

    /**
     * Sets id proof conf.
     *
     * @param idProofConf the id proof conf
     */
    public void setIdProofConf(final String idProofConf) {
        this.idProofConf = idProofConf;
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
     * Gets private key.
     *
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets private key.
     *
     * @param privateKey the private key
     */
    public void setPrivateKey(final String privateKey) {
        this.privateKey = CertificateFormatting.formatPrivateKey(privateKey);
    }

    /**
     * Gets canonicalisation algorithm.
     *
     * @return the canonicalisation algorithm
     */
    public String getCanonicalisationAlgorithm() {
        return canonicalisationAlgorithm;
    }

    /**
     * Sets canonicalisation algorithm.
     *
     * @param canonicalisationAlgorithm the canonicalisation algorithm
     */
    public void setCanonicalisationAlgorithm(final String canonicalisationAlgorithm) {
        this.canonicalisationAlgorithm = canonicalisationAlgorithm;
    }

    /**
     * Gets digest algorithm.
     *
     * @return the digest algorithm
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * Sets digest algorithm.
     *
     * @param digestAlgorithm the digest algorithm
     */
    public void setDigestAlgorithm(final String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * Gets signature packaging.
     *
     * @return the signature packaging
     */
    public String getSignaturePackaging() {
        return signaturePackaging;
    }

    /**
     * Sets signature packaging.
     *
     * @param signaturePackaging the signature packaging
     */
    public void setSignaturePackaging(final String signaturePackaging) {
        this.signaturePackaging = signaturePackaging;
    }

    @Override
    public String toString() {
        return "ProofConf{" +
                "idProofConf='" + idProofConf + '\'' +
                ", description='" + description + '\'' +
                ", certificate='" + certificate + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", canonicalisationAlgorithm='" + canonicalisationAlgorithm + '\'' +
                ", digestAlgorithm='" + digestAlgorithm + '\'' +
                ", signaturePackaging='" + signaturePackaging + '\'' +
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
            if (!"signProofParams".equals(f.getName()) && f.get(this) == null) {
                log.error("Missing field {} in object {}", f.getName() ,this.getClass().getSimpleName());
                return false;
            }
        }
        signProofParams = ConfigurationLoader.loadProofSignConf(this);
        return signProofParams.getKeyStore() != null;
    }
}
