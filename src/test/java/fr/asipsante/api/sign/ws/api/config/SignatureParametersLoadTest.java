/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.asipsante.api.sign.bean.metadata.MetaDatum;
import fr.asipsante.api.sign.bean.parameters.SignatureParameters;
import fr.asipsante.api.sign.bean.parameters.SignatureValidationParameters;
import fr.asipsante.api.sign.enums.MetaDataType;
import fr.asipsante.api.sign.validation.signature.rules.IVisitor;
import fr.asipsante.api.sign.validation.signature.rules.impl.TrustedCertificat;
import fr.asipsante.api.sign.ws.bean.ConfigurationLoader;
import fr.asipsante.api.sign.ws.bean.ConfigurationMapper;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
import fr.asipsante.api.sign.ws.bean.config.impl.GlobalConfJson;
import fr.asipsante.api.sign.ws.bean.object.SignatureConf;
import fr.asipsante.api.sign.ws.model.ConfVerifSign;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;

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
    @Before
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
        assertTrue("La configuration de signature n'existe pas: idSignConf=1", signConf.isPresent());
        params = ConfigurationLoader.loadSignConf(signConf.get());
        assertNotNull("Le packaging n'est pas configuré.", params.getSignPackaging());
        assertNotNull("Le paramètre signId n'est pas renseigné.", params.getSignId());

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
            assertEquals("L'autorité de Certification n'est pas reconnue.",
                    "L'autorité de Certification est reconnue.", confVerifSign.getRules().get(0)
                    .getDescription());
        }

    }

}
