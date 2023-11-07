/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.SignaturesApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.SignaturesApiDelegateImpl;

/**
 * Classe de test d'un fichier XML corrompu en signature enveloppée. Tous les
 * tests doivent retourner une erreur 501
 *
 * @author pam
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SignaturesApiController.class, SignaturesApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class})
@Import(ProjectInfoAutoConfiguration.class)
@AutoConfigureMockMvc
@WebMvcTest(SignaturesApiController.class)
@TestPropertySource(properties = { "config.secret=disable" })
public class SignatureApiIntegrationTest501 {

    /** The mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /** The doc. */
    private MockMultipartFile doc;

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
        doc = new MockMultipartFile("file", Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("FichierSigne_TOMWS2_SANS_SIGNATURE_nonConforme.xml"));
        assertNotNull(doc);
    }

    /**
     * Signature XMldsig test.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(doc).param("idSignConf", "3")
                .param("idVerifSignConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
                .param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
                .andExpect(status().isNotImplemented()).andDo(print());
    }

    /**
     * Signature XMldsig test no proof.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXMLdsigTestNoProof() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(doc).param("idSignConf", "3")
        		.with(csrf()).accept("application/json")).andExpect(status().isNotImplemented()).andDo(print());
    }

    /**
     * Signature xades test.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXadesTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/signatures/xadesbaselineb").file(doc).param("idSignConf", "3")
                        .param("idVerifSignConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
                        .param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
                .andExpect(status().isNotImplemented()).andDo(print());

    }

    /**
     * Signature xades test no proof.
     *
     * @throws Exception the exception
     */
    @Test
    public void signatureXadesTestNoProof() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselineb").file(doc)
                .param("idSignConf", "3").with(csrf()).accept("application/json")).andExpect(status().isNotImplemented())
                .andDo(print());
    }

}
