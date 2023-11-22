/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
package fr.gouv.esante.api.sign.ws.bean.config.impl;

import fr.gouv.esante.api.sign.config.observer.CaCrlObserver;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationMapper;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.bean.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * The type Global conf json.
 */
public class GlobalConfJson implements IGlobalConf {

    /**
     * signature.
     */
    private List<SignatureConf> signature;

    /**
     * proof.
     */
    private List<ProofConf> proof;

    /**
     * signatureVerification.
     */
    private List<SignVerifConf> signatureVerification;

    /**
     * certificateVerification.
     */
    private List<CertVerifConf> certificateVerification;

    /**
     * ca.
     */
    private List<CaConf> ca;

    /**
     * caCrlObservers.
     */
    private List<CaCrlObserver> caCrlObservers = new ArrayList<>();

    /**
     * Instantiates a new Global conf json.
     */
    public GlobalConfJson() {
    }

    /**
     * Instantiates a new Global conf json.
     *
     * @param signature               the signature
     * @param proof                   the proof
     * @param signatureVerification   the signature verification
     * @param certificateVerification the certificate verification
     * @param ca                      the ca
     */
    public GlobalConfJson(final List<SignatureConf> signature, final List<ProofConf> proof,
                          final List<SignVerifConf> signatureVerification,
                          final List<CertVerifConf> certificateVerification, final List<CaConf> ca) {
        this.signature = signature;
        this.proof = proof;
        this.signatureVerification = signatureVerification;
        this.certificateVerification = certificateVerification;
        this.ca = ca;
    }

    /**
     * Add observer.
     *
     * @param caCrlObserver the ca crl observer
     */
    @Override
    public void addObserver(final CaCrlObserver caCrlObserver) {
        this.caCrlObservers.add(caCrlObserver);
    }

    /**
     * Remove observer.
     *
     * @param caCrlObserver the ca crl observer
     */
    @Override
    public void removeObserver(final CaCrlObserver caCrlObserver) {
        this.caCrlObservers.remove(caCrlObserver);
    }

    /**
     * Map configs configuration mapper.
     *
     * @return the configuration mapper
     */
    public ConfigurationMapper mapConfigs() {
        return new ConfigurationMapper(signature, proof, signatureVerification, certificateVerification);
    }

    /**
     * Gets signature by id.
     *
     * @param id the id
     * @return the signature by id
     */
    public Optional<SignatureConf> getSignatureById(final String id) {
        return getSignature().stream().filter(signConf -> id.equals(signConf.getIdSignConf())).findAny();
    }

    /**
     * Gets signature.
     *
     * @return the signature
     */
    public List<SignatureConf> getSignature() {
        return signature;
    }

    /**
     * Sets signature.
     *
     * @param signature the signature
     */
    public void setSignature(final List<SignatureConf> signature) {
        this.signature = signature;
    }

    /**
     * Gets proof by id.
     *
     * @param id the id
     * @return the proof by id
     */
    public Optional<ProofConf> getProofById(final String id) {
        return getProof().stream().filter(proofConf -> id.equals(proofConf.getIdProofConf())).findAny();
    }

    /**
     * Gets proof.
     *
     * @return the proof
     */
    public List<ProofConf> getProof() {
        return proof;
    }

    /**
     * Sets proof.
     *
     * @param proof the proof
     */
    public void setProof(final List<ProofConf> proof) {
        this.proof = proof;
    }

    /**
     * Gets signature verification by id.
     *
     * @param id the id
     * @return the signature verification by id
     */
    public Optional<SignVerifConf> getSignatureVerificationById(final String id) {
        return getSignatureVerification().stream().filter(signVerif -> id.equals(signVerif.getIdVerifSign())).findAny();
    }

    /**
     * Gets signature verification.
     *
     * @return the signature verification
     */
    public List<SignVerifConf> getSignatureVerification() {
        return signatureVerification;
    }

    /**
     * Sets signature verification.
     *
     * @param signatureVerification the signature verification
     */
    public void setSignatureVerification(final List<SignVerifConf> signatureVerification) {
        this.signatureVerification = signatureVerification;
    }

    /**
     * Gets certificate verification by id.
     *
     * @param id the id
     * @return the certificate verification by id
     */
    public Optional<CertVerifConf> getCertificateVerificationById(final String id) {
        return getCertificateVerification().stream()
                .filter(confVerif -> id.equals(confVerif.getIdVerifCert())).findAny();
    }

    /**
     * Gets certificate verification.
     *
     * @return the certificate verification
     */
    public List<CertVerifConf> getCertificateVerification() {
        return certificateVerification;
    }

    /**
     * Sets certificate verification.
     *
     * @param certificateVerification the certificate verification
     */
    public void setCertificateVerification(final List<CertVerifConf> certificateVerification) {
        this.certificateVerification = certificateVerification;
    }

    /**
     * Gets ca.
     *
     * @return the ca
     */
    public List<CaConf> getCa() {
        return ca;
    }

    /**
     * Sets ca.
     *
     * @param ca the ca
     */
    public void setCa(final List<CaConf> ca) {
        this.ca = ca;
        for (final CaCrlObserver caCrlObserver : caCrlObservers) {
            caCrlObserver.update();
        }
    }

    /**
     * Override toString method.
     *
     * @return the string value of globalConf
     */
    @Override
    public String toString() {
        return "GlobalConfJson{" +
                "signature=" + signature.toString() +
                ", proof=" + proof.toString() +
                ", signatureVerification=" + signatureVerification.toString() +
                ", certificateVerification=" + certificateVerification.toString() +
                ", ca=" + ca.toString() +
                '}';
    }

}
