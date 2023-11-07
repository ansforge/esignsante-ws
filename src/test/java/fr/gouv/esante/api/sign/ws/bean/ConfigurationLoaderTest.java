/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gouv.esante.api.sign.bean.parameters.SignatureParameters;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.bean.config.impl.GlobalConfJson;

/**
 * The Class ConfigurationLoaderTest.
 */
public class ConfigurationLoaderTest {

    /** The ws conf path good. */
    private static IGlobalConf  conf;

    /**
     * Init.
     *
     * @throws Exception the exception
     */
    @BeforeAll
    public static void init() throws Exception {
        final String wsConfPathGood = String.valueOf(Paths.get(Paths.get(Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("esignsante-conf.json")).toURI()).toString()));
        final String jsonConf = new String(Files.readAllBytes(Paths.get(wsConfPathGood)));
        final ObjectMapper mapper = new ObjectMapper();
        conf = mapper.readValue(jsonConf, GlobalConfJson.class);
    }

    /**
     * Configs ok test.
     */
    @Test
    public void configsOkTest() {
        assertFalse(conf.getSignature().isEmpty());
        assertEquals(5, conf.getSignature().size());
    }

    /**
     * configLoadSignConfTest
     */
    @Test
    public void configLoadSignConfTest() {
        assertTrue(conf.getSignatureById("1").isPresent());
        final SignatureParameters signParams = ConfigurationLoader.loadSignConf(conf.getSignatureById("1").get());
        assertEquals("Scheduling Test.", signParams.getDescription());
    }

    /**
     * Config load proof sign conf test.
     */
    @Test
    public void configLoadProofSignConfTest() {
        assertTrue(conf.getProofById("1").isPresent());
        final SignatureParameters signProofParams = ConfigurationLoader.loadProofSignConf(conf.getProofById("1").get());
        assertEquals("Fichier par defaut pour la preuve", signProofParams.getDescription());
    }

}
