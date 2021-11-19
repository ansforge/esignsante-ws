/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api.requests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.asipsante.api.sign.config.CACRLConfig;
import fr.asipsante.api.sign.config.ScheduledConfig;
import fr.asipsante.api.sign.config.WebConfig;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The Class DefaultApiIntegrationTest.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CACRLConfig.class, ScheduledConfig.class, WebConfig.class })
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("fr.asipsante.api.sign.ws.api")
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
