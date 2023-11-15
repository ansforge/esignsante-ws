/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.SignaturesApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.SignaturesApiDelegateImpl;

/**
 * The Class SignatureApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SignaturesApiController.class, SignaturesApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class})
@Import(ProjectInfoAutoConfiguration.class)
@AutoConfigureMockMvc
@WebMvcTest(SignaturesApiController.class)
@TestPropertySource(properties = "config.secret=disable")
public class SignatureApiIntegrationTestNoSecret {

    /** The mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /** The xml. */
    private MockMultipartFile xml;

    /** The texte. */
    private MockMultipartFile texte;

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

    /**
     * Inits the.
     *
     * @throws Exception the exception
     */
    @BeforeEach
    public void init() throws Exception {
        xml = new MockMultipartFile("file", Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("Fichier_TOMWS2_SANS_SIGNATURE.xml"));

        texte = new MockMultipartFile("file",
                Thread.currentThread().getContextClassLoader().getResourceAsStream("toBeSigned.txt"));
        assertNotNull(xml);
        assertNotNull(texte);
    }

    /**
     * Cas non passant de signature XMLDSIG avec un certificat expiré.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigCertExpire() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(xml)
                .param("secret", "lalalalalala").param("idSignConf", "5").with(csrf()).accept("application/json"))
                .andExpect(status().isServiceUnavailable()).andDo(print());
    }

    /**
     * Cas non passant de signature XMLDSIG avec preuve avec un certificat expiré.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigWithProofCertExpire() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml).param("secret", "password")
                        .param("idSignConf", "3").param("idVerifSignConf", "1").param("requestId", "Request-1")
                        .param("proofTag", "MonTAG").param("applicantId", "RPPS").with(csrf()).accept("application/json"))
                .andExpect(status().isServiceUnavailable()).andDo(print());
    }

    /**
     * Cas non passant de signature XMLDSIG avec un certificat révoqué.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigCertRevoque() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(xml)
                .param("secret", "poopopioppopop").param("idSignConf", "6").with(csrf()).accept("application/json"))
                .andExpect(status().isServiceUnavailable()).andDo(print());
    }

    /**
     * Cas non passant de signature XMLDSIG avec preuve avec un certificat révoqué.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigWithProofCertRevoque() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml)
                .param("secret", "lalalalalala").param("idSignConf", "5").param("idVerifSignConf", "1")
                .param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
                .with(csrf()).accept("application/json")).andExpect(status().isServiceUnavailable()).andDo(print());
    }

    /**
     * Cas non passant de signature XMLDSIG avec preuve avec un certificat avec
     * mauvais usage.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigWithProofCertBadUsage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml)
                .param("secret", "poopopioppopop").param("idSignConf", "6").param("idVerifSignConf", "1")
                .param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
                .with(csrf()).accept("application/json")).andExpect(status().isServiceUnavailable()).andDo(print());
    }

    /**
     * Cas passant signature XMLDSIG sans preuve.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestNoProof() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(xml).param("secret", "123456")
                .param("idSignConf", "1").with(csrf()).accept("application/json")).andExpect(status().is2xxSuccessful())
                .andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(2, body.names().length());
    }

    /**
     * Cas non passant signature XMLDSIG sans preuve, secret incorrect.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestNoProofWrongSecret() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(xml)
                .param("secret", "wrongSecret").param("idSignConf", "1").with(csrf()).accept("application/json"))
                .andExpect(status().is2xxSuccessful()).andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(2, body.names().length());
    }

    /**
     * Cas passant signature XMLDSIG avec preuve.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestWithProof() throws Exception {
        final MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml).param("secret", "password")
                        .param("idSignConf", "1").param("idVerifSignConf", "1").param("requestId", "Request-1")
                        .param("proofTag", "MonTAG").param("applicantId", "RPPS").with(csrf()).accept("application/json"))
                .andExpect(status().is2xxSuccessful()).andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(5, body.names().length());
        assertTrue((Boolean) body.get("valide"));
    }

    /**
     * Cas non passant signature XMLDSIG avec preuve, secret incorrect.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestWithProofWrongSecret() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml)
                .param("secret", "wrongSecret").param("idSignConf", "1").param("idVerifSignConf", "1")
                .param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
                .with(csrf()).accept("application/json")).andExpect(status().is2xxSuccessful()).andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(5, body.names().length());
        assertTrue((Boolean) body.get("valide"));
    }

    /**
     * Erreur 404.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(xml).param("secret", "123456")
                .param("idSignConf", "100").with(csrf()).accept("application/json")).andExpect(status().isNotFound()).andDo(print());
    }

    /**
     * Erreur 404.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestWrongAllIds() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(xml).param("secret", "123456")
                        .param("idSignConf", "100").param("idVerifSignConf", "100").param("requestId", "Request-1")
                        .param("proofTag", "MonTAG").param("applicantId", "RPPS").with(csrf()).accept("application/json"))
                .andExpect(status().isNotFound()).andDo(print());
    }

    /**
     * Erreur 501. Cas d'une demande de signature d'un fichier non XML enveloppée
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestWrongFileFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(texte)
                .param("secret", "password").param("idSignConf", "3").param("idVerifSignConf", "1")
                .param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
                .with(csrf()).accept("application/json")).andExpect(status().isNotImplemented()).andDo(print());
    }

    /**
     * Cas passant signature XADES sans preuve.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXadesTest() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselineb").file(xml)
                .param("secret", "123456").param("idSignConf", "1").with(csrf()).accept("application/json"))
                .andExpect(status().is2xxSuccessful()).andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(2, body.names().length());
    }

    /**
     * Cas passant signature XADES avec preuve.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXadesTestWithProof() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselinebwithproof").file(xml)
                .param("secret", "123456").param("idSignConf", "1").param("idVerifSignConf", "1")
                .param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
                .with(csrf()).accept("application/json")).andExpect(status().is2xxSuccessful()).andDo(print()).andReturn();

        final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(0, body.getJSONArray("erreurs").length());
        assertEquals(5, body.names().length());
        assertTrue((Boolean) body.get("valide"));
    }

    /**
     * Erreur 404.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXadesTestWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselineb").file(xml)
                .param("secret", "disabled").param("idSignConf", "100").with(csrf()).accept("application/json"))
                .andExpect(status().isNotFound()).andDo(print());
    }

}
