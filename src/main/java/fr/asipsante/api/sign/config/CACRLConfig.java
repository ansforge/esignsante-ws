/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.config;

import fr.asipsante.api.sign.config.utils.CaCrlServiceLoader;
import fr.asipsante.api.sign.service.*;
import fr.asipsante.api.sign.service.impl.*;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

/**
 * The Class CACRLConfig.
 */
@Configuration
public class CACRLConfig {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(CACRLConfig.class);

    /**
     * globalConf.
     */
    @Autowired
    private IGlobalConf globalConf;

    /**
     * Signature service.
     *
     * @return the i signature service
     */
    @Bean
    @Lazy
    public ISignatureService signatureService() {
        return new SignatureServiceImpl();
    }

    /**
     * Signature validation service.
     *
     * @return the i signature validation service
     */
    @Bean
    @Lazy
    public ISignatureValidationService signatureValidationService() {
        return new SignatureValidationServiceImpl();
    }

    /**
     * Certificate validation service.
     *
     * @return the i certificate validation service
     */
    @Bean
    @Lazy
    public ICertificateValidationService certificateValidationService() {
        return new CertificateValidationServiceImpl();
    }

    /**
     * Proof generation service.
     *
     * @return the i proof generation service
     */
    @Bean
    @Lazy
    public IProofGenerationService proofGenerationService() {
        return new ProofGenerationServiceImpl();
    }

    /**
     * Service ca crl.
     *
     * @return the ICACRL service
     * @throws IOException IOException
     */
    @Bean
    @Lazy
    public ICACRLService serviceCaCrl() throws IOException {
        final ICACRLService serviceCaCrl = new CACRLServiceImpl();
        return CaCrlServiceLoader.loadCaCrl(serviceCaCrl, globalConf.getCa());
    }
}
