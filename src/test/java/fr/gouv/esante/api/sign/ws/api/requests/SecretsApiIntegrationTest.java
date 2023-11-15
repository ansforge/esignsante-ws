/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.SecretsApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.SecretsApiDelegateImpl;
import fr.gouv.esante.api.sign.ws.model.Secret;


/**
 * The Class SignatureApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SecretsApiController.class, SecretsApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class })
@AutoConfigureMockMvc
@WebMvcTest(SecretsApiController.class)
public class SecretsApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

    static {
        final String confPath;
        try {
            confPath = String.valueOf(Paths.get(Paths.get(Objects.requireNonNull(Thread.currentThread().
                    getContextClassLoader().getResource("esignsante-conf.json")).toURI()).toString()));
            System.setProperty("ws.conf", confPath);
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    /**
     * Secret generation test.
     *
     * @throws Exception the exception
     */
    @Test
    public void secretGenTest() throws Exception {
        final Secret secret = new Secret();
        secret.setPlainSecret("password");

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        final ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        final String requestJson = ow.writeValueAsString(secret);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/secrets").with(csrf()).accept("application/json").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)).andExpect(status().isOk()).andDo(print()).andReturn();

        assertTrue(result.getResponse().getContentAsString().startsWith("{\"secureSecretHash\":"), "Should have 'secureSecretHash' as the first json object");
    }
}
