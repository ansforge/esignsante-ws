/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.esante.api.sign.bean.metadata.MetaDatum;
import fr.gouv.esante.api.sign.bean.parameters.SignatureParameters;
import fr.gouv.esante.api.sign.bean.parameters.SignatureValidationParameters;
import fr.gouv.esante.api.sign.enums.MetaDataType;
import fr.gouv.esante.api.sign.validation.signature.rules.IVisitor;
import fr.gouv.esante.api.sign.validation.signature.rules.impl.TrustedCertificat;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationLoader;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationMapper;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.bean.config.impl.GlobalConfJson;
import fr.gouv.esante.api.sign.ws.bean.object.SignatureConf;
import fr.gouv.esante.api.sign.ws.model.ConfVerifSign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The Class SignatureParametersLoadTest.
 */
public class SignatureParametersLoadTest {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SignatureParametersLoadTest.class);

    /** The conf file name. */
    private String confFile = "esignsante-conf.json";

    /** The conf file name. */
    private IGlobalConf conf;

    /** The params. */
    private SignatureParameters params;

    /**
     * Deserialize conf file.
     *
     * @throws IOException        the io exception
     * @throws URISyntaxException the uri syntax exception
     */
    @BeforeEach
    public void deserializeConfFile() throws IOException, URISyntaxException {
        final String jsonConf = new String(Files.readAllBytes(Paths.get(Paths.get(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource(confFile)).toURI()).toString())));
        final ObjectMapper mapper = new ObjectMapper();
        conf = mapper.readValue(jsonConf, GlobalConfJson.class);

    }

    /**
     * Gets the sign packaging test.
     */
    @Test
    public void getSignPackagingTest() {
        final Optional<SignatureConf> signConf = conf.getSignatureById("1");
        assertTrue(signConf.isPresent(), "La configuration de signature n'existe pas: idSignConf=1");
        params = ConfigurationLoader.loadSignConf(signConf.get());
        assertNotNull(params.getSignPackaging());
        assertNotNull(params.getSignId());

    }

    /**
     * Test rules definitions.
     */
    @Test
    public void testRulesDefinitions() {
        LOG.info(ConfigurationMapper.class.getSimpleName());
        final IVisitor rule = new TrustedCertificat();
        final List<MetaDatum> metadata = new ArrayList<>();
        metadata.add(new MetaDatum(MetaDataType.DATE_SIGNATURE, ""));
        final SignatureValidationParameters validationParams = new SignatureValidationParameters();
        final List<IVisitor> rules = new ArrayList<>();
        rules.add(rule);
        validationParams.setRules(rules);
        validationParams.setMetaData(metadata);

        final List<ConfVerifSign> confVerifSignList = conf.mapConfigs().mapVerifSignConfig();
        for (final ConfVerifSign confVerifSign : confVerifSignList) {
            assertEquals("L'autorité de Certification est reconnue.", confVerifSign.getRules().get(0)
                    .getDescription());
        }

    }

}
