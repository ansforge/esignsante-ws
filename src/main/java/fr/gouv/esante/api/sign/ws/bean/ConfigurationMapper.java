/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.bean;

import fr.gouv.esante.api.sign.ws.bean.object.CertVerifConf;
import fr.gouv.esante.api.sign.ws.bean.object.ProofConf;
import fr.gouv.esante.api.sign.ws.bean.object.SignVerifConf;
import fr.gouv.esante.api.sign.ws.bean.object.SignatureConf;
import fr.gouv.esante.api.sign.ws.model.*;
import fr.gouv.esante.api.sign.ws.model.ConfProof.CanonicalisationAlgorithmForProofEnum;
import fr.gouv.esante.api.sign.ws.model.ConfProof.DigestAlgorithmForProofEnum;
import fr.gouv.esante.api.sign.ws.model.ConfProof.SignaturePackagingForProofEnum;
import fr.gouv.esante.api.sign.ws.model.ConfSign.CanonicalisationAlgorithmEnum;
import fr.gouv.esante.api.sign.ws.model.ConfSign.DigestAlgorithmEnum;
import fr.gouv.esante.api.sign.ws.model.ConfSign.SignaturePackagingEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * The Class ConfigurationsMapper.
 */
public class ConfigurationMapper {

    /** The log. */
    private static Logger log = LoggerFactory.getLogger(ConfigurationMapper.class);

    /** The rules definition. */
    private static Properties rulesDefinition = new Properties();

    /** The signature list conf. */
    private List<SignatureConf> signature;

    /** The proof list conf. */
    private List<ProofConf> proof;

    /** The signature verification list conf. */
    private List<SignVerifConf> signatureVerification;

    /** The certificate verification list conf. */
    private List<CertVerifConf> certificateVerification;

    /**
     * Instantiates a new configurations mapper.
     *
     * @param signature               the signature
     * @param proof                   the proof
     * @param signatureVerification   the signature verification
     * @param certificateVerification the certificate verification
     */
    public ConfigurationMapper(final List<SignatureConf> signature, final List<ProofConf> proof,
                               final List<SignVerifConf> signatureVerification,
                               final List<CertVerifConf> certificateVerification) {
        this.signature = signature;
        this.proof = proof;
        this.signatureVerification = signatureVerification;
        this.certificateVerification = certificateVerification;
    }

    /*
      Load mapper between rule and its description.
     */
    static {
        try (final InputStream is =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("rules.properties")) {

            if (is != null) {
                rulesDefinition.load(is);
            } else {
                rulesDefinition.put("defaultKey", "defaultValue");
            }
        } catch (final IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Gets the configs.
     *
     * @return La liste des configurations
     * @throws CertificateException certificate exception
     * @throws IOException          IOException
     */
    public Conf getConfigs() throws CertificateException, IOException {
        final Conf configs = new Conf();
        configs.setSignature(mapSignConfig());
        configs.setCertificatVerification(mapVerifCertConfig());
        configs.setProof(mapProofConfig());
        configs.setSignatureVerification(mapVerifSignConfig());
        return configs;
    }

    /**
     * Map sign config.
     *
     * @return the signature conf list
     * @throws CertificateException certificate exception
     * @throws IOException input stream exception
     */
    private List<ConfSign> mapSignConfig() throws CertificateException, IOException {
        final List<ConfSign> signConfigs = new ArrayList<>();
        for (final SignatureConf signConf : signature) {
            final ConfSign config = new ConfSign();
            config.idSignConf(Long.valueOf(signConf.getIdSignConf()));
            config.associatedProofId(Long.valueOf(signConf.getIdProofConf()));
            config.description(signConf.getDescription());
            config.digestAlgorithm(DigestAlgorithmEnum.fromValue(signConf.getDigestAlgorithm()));
            config.canonicalisationAlgorithm(CanonicalisationAlgorithmEnum.fromValue(
                    signConf.getCanonicalisationAlgorithm()));
            config.signaturePackaging(SignaturePackagingEnum.fromValue(signConf.getSignaturePackaging()));
            config.dn(getDn(signConf.getCertificate()));
            signConfigs.add(config);
        }
        return signConfigs;
    }

    /**
     * Map proof config.
     *
     * @return the list
     * @throws CertificateException certificate exception
     * @throws IOException input stream exception
     */
    private List<ConfProof> mapProofConfig() throws CertificateException, IOException {
        final List<ConfProof> proofConfigs = new ArrayList<>();
        for (final ProofConf proofSignConf : proof) {
            final ConfProof config = new ConfProof();
            config.idProofConf(Long.valueOf(proofSignConf.getIdProofConf()));
            config.description(proofSignConf.getDescription());
            config.digestAlgorithmForProof(DigestAlgorithmForProofEnum.fromValue(proofSignConf.getDigestAlgorithm()));
            config.canonicalisationAlgorithmForProof(
                    CanonicalisationAlgorithmForProofEnum.fromValue(proofSignConf.getCanonicalisationAlgorithm()));
            config.signaturePackagingForProof(
                    SignaturePackagingForProofEnum.fromValue(proofSignConf.getSignaturePackaging()));
            config.dn(getDn(proofSignConf.getCertificate()));
            proofConfigs.add(config);
        }

        return proofConfigs;
    }

    /**
     * Map verif cert config.
     *
     * @return the list
     */
    public List<ConfVerifCert> mapVerifCertConfig() {
        final List<ConfVerifCert> verifCertConfigs = new ArrayList<>();
        for (final CertVerifConf certVerifConf : certificateVerification) {
            final ConfVerifCert config = new ConfVerifCert();
            config.idVerifCertConf(Long.valueOf(certVerifConf.getIdVerifCert()));
            config.description(certVerifConf.getDescription());
            final String[] metadata = certVerifConf.getMetadata().trim().split(",");
            config.metaData(Arrays.asList(metadata));

            final String[] rules = certVerifConf.getRules().trim().split(",");
            final List<Rule> rulesAsString = new ArrayList<>();
            for (final String rule : rules) {
                final Rule ruleAsString = new Rule();
                ruleAsString.id(rule);
                ruleAsString.description(
                        rulesDefinition.getProperty(rule, "Pas de description"));
                rulesAsString.add(ruleAsString);
            }
            config.rules(rulesAsString);
            verifCertConfigs.add(config);
        }

        return verifCertConfigs;
    }

    /**
     * Map verif sign config.
     *
     * @return the list
     */
    public List<ConfVerifSign> mapVerifSignConfig() {
        final List<ConfVerifSign> verifSignConfigs = new ArrayList<>();
        for (final SignVerifConf signVerifConf : signatureVerification) {
            final ConfVerifSign config = new ConfVerifSign();
            config.idVerifSignConf(Long.valueOf(signVerifConf.getIdVerifSign()));
            config.description(signVerifConf.getDescription());
            final String[] metadata = signVerifConf.getMetadata().trim().split(",");
            config.metaData(Arrays.asList(metadata));

            final String[] rules = signVerifConf.getRules().trim().split(",");
            final List<Rule> rulesAsString = new ArrayList<>();
            for (final String rule : rules) {
                final Rule ruleAsString = new Rule();
                ruleAsString.id(rule);
                ruleAsString.description(
                        rulesDefinition.getProperty(rule, "Pas de description"));
                rulesAsString.add(ruleAsString);
            }
            config.rules(rulesAsString);
            verifSignConfigs.add(config);
        }
        return verifSignConfigs;
    }

    /**
     * Gets the dn.
     *
     * @param certificate the keystore
     * @return the dn
     * @throws CertificateException certificate exception
     * @throws IOException input stream exception
     */
    private static String getDn(final String certificate) throws CertificateException, IOException {

        final CertificateFactory fac = CertificateFactory.getInstance("X509");
        final ByteArrayInputStream in = new ByteArrayInputStream(certificate.getBytes());
        final X509Certificate cert = (X509Certificate) fac.generateCertificate(in);
        in.close();

        return cert.getSubjectX500Principal().getName();
    }

}
