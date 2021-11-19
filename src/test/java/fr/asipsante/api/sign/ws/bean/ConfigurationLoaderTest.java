/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.asipsante.api.sign.bean.parameters.SignatureParameters;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
import fr.asipsante.api.sign.ws.bean.config.impl.GlobalConfJson;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.*;

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
    @BeforeClass
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
        assertFalse("La liste des configs de signature est vide", conf.getSignature().isEmpty());
        assertEquals("La liste ne contient pas le bon nombre de configuration de signature", 5,
                conf.getSignature().size());
    }

    /**
     * configLoadSignConfTest
     */
    @Test
    public void configLoadSignConfTest() {
        assertTrue(conf.getSignatureById("1").isPresent());
        final SignatureParameters signParams = ConfigurationLoader.loadSignConf(conf.getSignatureById("1").get());
        assertEquals("La description de la première conf de signature n'est pas la bonne", "Scheduling Test.", signParams.getDescription());
    }

    /**
     * Config load proof sign conf test.
     */
    @Test
    public void configLoadProofSignConfTest() {
        assertTrue(conf.getProofById("1").isPresent());
        final SignatureParameters signProofParams = ConfigurationLoader.loadProofSignConf(conf.getProofById("1").get());
        assertEquals("La description de la première conf de signature de preuve n'est pas la bonne",
                "Fichier par defaut pour la preuve", signProofParams.getDescription());
    }

}
