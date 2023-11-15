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
import fr.gouv.esante.api.sign.ws.api.CaApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.CaApiDelegateImpl;

/**
 * The Class CaApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { CaApiDelegateImpl.class, CaApiController.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class })
@AutoConfigureMockMvc
@WebMvcTest(CaApiController.class)
public class CaApiIntegrationTest {

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
     * Ca get test.
     *
     * @throws Exception the exception
     */
    @Test
    public void caGetTest() throws Exception {
        final String body = "[\"CN=TEST AC IGC-SANTE ELEMENTAIRE ORGANISATIONS,OU=IGC-SANTE TEST,OU=0002 187512751,O=ASIP-SANTE,C=FR\",\"CN=TEST AC RACINE IGC-SANTE ELEMENTAIRE,OU=IGC-SANTE TEST,OU=0002 187512751,O=ASIP-SANTE,C=FR\"]";
        mockMvc.perform(get("/ca").accept("application/json")).andExpect(status().isOk())
                .andExpect(content().json(body)).andDo(print());
    }
}
