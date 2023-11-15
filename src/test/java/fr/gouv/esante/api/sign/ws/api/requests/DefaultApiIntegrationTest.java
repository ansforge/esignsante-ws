/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.DefaultApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.DefaultApiDelegateImpl;

/**
 * The Class DefaultApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { DefaultApiController.class, DefaultApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class  })
@AutoConfigureMockMvc
@WebMvcTest(DefaultApiController.class)
public class DefaultApiIntegrationTest {

    /** The mock mvc. */
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

    /**
     * Test passant qui permet de récuperer la liste des opérations disponibles.
     *
     * @throws Exception the exception
     */
    @Test
    public void rootGetTest() throws Exception {
        final String body = "[\"/\",\"/configurations\",\"/ca\",\"/signatures/xmldsig\",\"/signatures/xmldsigwithproof\",\"/signatures/xadesbaselineb\",\"/signatures/xadesbaselinebwithproof\",\"/validation/signatures/xmldsig\",\"/validation/signatures/xmldsigwithproof\",\"/validation/signatures/xadesbaselineb\",\"/validation/signatures/xadesbaselinebwithproof\",\"/validation/certificats\",\"/validation/certificatswithproof\"]";
        mockMvc.perform(get("/").accept("application/json"))
                .andExpect(status().isOk()).andExpect(content().json(body)).andDo(print());
    }
}
