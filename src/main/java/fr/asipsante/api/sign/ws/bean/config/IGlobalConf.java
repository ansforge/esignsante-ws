/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.asipsante.api.sign.ws.bean.config;

import fr.asipsante.api.sign.config.observer.CaCrlObserver;
import fr.asipsante.api.sign.ws.bean.ConfigurationMapper;
import fr.asipsante.api.sign.ws.bean.object.*;

import java.util.List;
import java.util.Optional;

/**
 * The interface Global conf.
 */
public interface IGlobalConf {

    /**
     * Add observer.
     *
     * @param caCrlService the ca crl service
     */
    void addObserver(CaCrlObserver caCrlService);

    /**
     * Remove observer.
     *
     * @param caCrlService the ca crl service
     */
    void removeObserver(CaCrlObserver caCrlService);

    /**
     * Map configs configuration mapper.
     *
     * @return the configuration mapper
     */
    ConfigurationMapper mapConfigs();

    /**
     * Gets signature by id.
     *
     * @param id the id
     * @return the signature by id
     */
    Optional<SignatureConf> getSignatureById(String id);

    /**
     * Gets signature.
     *
     * @return the signature
     */
    List<SignatureConf> getSignature();

    /**
     * Sets signature.
     *
     * @param signature the signature
     */
    void setSignature(List<SignatureConf> signature);

    /**
     * Gets proof by id.
     *
     * @param id the id
     * @return the proof by id
     */
    Optional<ProofConf> getProofById(String id);

    /**
     * Gets proof.
     *
     * @return the proof
     */
    List<ProofConf> getProof();

    /**
     * Sets proof.
     *
     * @param proof the proof
     */
    void setProof(List<ProofConf> proof);

    /**
     * Gets signature verification by id.
     *
     * @param id the id
     * @return the signature verification by id
     */
    Optional<SignVerifConf> getSignatureVerificationById(String id);

    /**
     * Gets signature verification.
     *
     * @return the signature verification
     */
    List<SignVerifConf> getSignatureVerification();

    /**
     * Sets signature verification.
     *
     * @param signatureVerification the signature verification
     */
    void setSignatureVerification(List<SignVerifConf> signatureVerification);

    /**
     * Gets certificate verification by id.
     *
     * @param id the id
     * @return the certificate verification by id
     */
    Optional<CertVerifConf> getCertificateVerificationById(String id);

    /**
     * Gets certificate verification.
     *
     * @return the certificate verification
     */
    List<CertVerifConf> getCertificateVerification();

    /**
     * Sets certificate verification.
     *
     * @param certificateVerification the certificate verification
     */
    void setCertificateVerification(List<CertVerifConf> certificateVerification);

    /**
     * Gets ca.
     *
     * @return the ca
     */
    List<CaConf> getCa();

    /**
     * Sets ca.
     *
     * @param ca the ca
     */
    void setCa(List<CaConf> ca);

}
