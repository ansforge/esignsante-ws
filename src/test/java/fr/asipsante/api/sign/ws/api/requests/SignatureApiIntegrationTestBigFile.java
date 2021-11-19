/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api.requests;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.asipsante.api.sign.config.provider.impl.ESignSanteSanteConfigurationsJson;
import fr.asipsante.api.sign.config.CACRLConfig;
import fr.asipsante.api.sign.config.ScheduledConfig;
import fr.asipsante.api.sign.config.WebConfig;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The Class SignatureApiIntegrationTestBigFile.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ESignSanteSanteConfigurationsJson.class, CACRLConfig.class, ScheduledConfig.class,
		WebConfig.class })
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("fr.asipsante.api.sign.ws.api")
@TestPropertySource(properties = { "config.secret=disable" })
public class SignatureApiIntegrationTestBigFile {

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The doc. */
	private MockMultipartFile doc;

	static {
		final String confPath;
		try {
			confPath = String.valueOf(Paths.get(Paths.get(Objects
					.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("esignsante-conf.json"))
					.toURI()).toString()));
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
	@Before
	public void init() throws Exception {
		doc = new MockMultipartFile("file",
				Thread.currentThread().getContextClassLoader().getResourceAsStream("ANS_TXT_H0440001_Enteteflux.txt"));
		assertNotNull("Le fichier n'a pas été lu.", doc);
	}

	/**
	 * Signature XM ldsig test with proof.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void signatureXMLdsigTestWithProof() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.multipart("/signatures/xmldsigwithproof").file(doc).param("idSignConf", "1")
						.param("idVerifSignConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
						.param("applicantId", "RPPS").param("idProofConf", "1").accept("application/json"))
				.andExpect(status().isOk()).andDo(print());
	}

	/**
	 * Signature XM ldsig test no proof.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void signatureXMLdsigTestNoProof() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xmldsig").file(doc).param("idSignConf", "1")
				.accept("application/json")).andExpect(status().isOk()).andDo(print());
	}

	/**
	 * Signature xades test with proof.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void signatureXadesTestWithProof() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselinebwithproof").file(doc)
				.param("idSignConf", "1").param("idVerifSignConf", "1").param("requestId", "Request-1")
				.param("proofTag", "MonTAG").param("applicantId", "RPPS").param("idProofConf", "1")
				.accept("application/json")).andExpect(status().isOk()).andDo(print());
	}

	/**
	 * Signature xades test with proof.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void signatureXadesTestWithProofBigToken() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselinebwithproof").file(doc)
				.param("idSignConf", "1").param("idVerifSignConf", "1").param("requestId", "Request-1")
				.param("proofTag", "MonTAG").param("applicantId", "RPPS").param("idProofConf", "1")
				.header("OpenidToken",
						"[{\"accessToken\":\"ceci_est_un_accessToken\",\"introspectionResponse\":\"{\\\"exp\\\":1628250495,\\\"iat\\\":1628250435,\\\"auth_time\\\":1628250434,\\\"jti\\\":\\\"0cc00f99-3f1b-4799-9222-a944ca82c310\\\",\\\"iss\\\":\\\"https://auth.bas.esw.esante.gouv.fr/auth/realms/esante-wallet\\\",\\\"sub\\\":\\\"f:550dc1c8-d97b-4b1e-ac8c-8eb4471cf9dd:899700218896\\\",\\\"typ\\\":\\\"Bearer\\\",\\\"azp\\\":\\\"ans-poc-bas-psc\\\",\\\"nonce\\\":\\\"\\\",\\\"session_state\\\":\\\"e0e82435-fef5-430b-9b31-187fe3b0ffe6\\\",\\\"preferred_username\\\":\\\"899700218896\\\",\\\"email_verified\\\":false,\\\"acr\\\":\\\"eidas3\\\",\\\"scope\\\":\\\"openid profile email scope_all\\\",\\\"client_id\\\":\\\"ans-poc-bas-psc\\\",\\\"username\\\":\\\"899700218896\\\",\\\"active\\\":true}\",\"userInfo\":\"{\\\"Secteur_Activite\\\":\\\"SA07^1.2.250.1.71.4.2.4\\\",\\\"sub\\\":\\\"f:550dc1c8-d97b-4b1e-ac8c-8eb4471cf9dd:899700218896\\\",\\\"email_verified\\\":false,\\\"SubjectOrganization\\\":\\\"CABINET M SPECIALISTE0021889\\\",\\\"Mode_Acces_Raison\\\":\\\"\\\",\\\"preferred_username\\\":\\\"899700218896\\\",\\\"given_name\\\":\\\"ROBERT\\\",\\\"Acces_Regulation_Medicale\\\":\\\"FAUX\\\",\\\"UITVersion\\\":\\\"1.0\\\",\\\"Palier_Authentification\\\":\\\"APPPRIP3^1.2.250.1.213.1.5.1.1.1\\\",\\\"SubjectRefPro\\\":{\\\"codeCivilite\\\":\\\"M\\\",\\\"exercices\\\":[{\\\"codeProfession\\\":\\\"10\\\",\\\"codeCategorieProfessionnelle\\\":\\\"C\\\",\\\"codeCiviliteDexercice\\\":\\\"M\\\",\\\"nomDexercice\\\":\\\"SPECIALISTE0021889\\\",\\\"prenomDexercice\\\":\\\"ROBERT\\\",\\\"codeTypeSavoirFaire\\\":\\\"\\\",\\\"codeSavoirFaire\\\":\\\"\\\",\\\"activities\\\":[{\\\"codeModeExercice\\\":\\\"L\\\",\\\"codeSecteurDactivite\\\":\\\"SA07\\\",\\\"codeSectionPharmacien\\\":\\\"\\\",\\\"codeRole\\\":\\\"\\\",\\\"numeroSiretSite\\\":\\\"\\\",\\\"numeroSirenSite\\\":\\\"\\\",\\\"numeroFinessSite\\\":\\\"\\\",\\\"numeroFinessetablissementJuridique\\\":\\\"\\\",\\\"identifiantTechniqueDeLaStructure\\\":\\\"\\\",\\\"raisonSocialeSite\\\":\\\"CABINET M SPECIALISTE0021889\\\",\\\"enseigneCommercialeSite\\\":\\\"\\\",\\\"complementDestinataire\\\":\\\"\\\",\\\"complementPointGeographique\\\":\\\"\\\",\\\"numeroVoie\\\":\\\"1\\\",\\\"indiceRepetitionVoie\\\":\\\"\\\",\\\"codeTypeDeVoie\\\":\\\"R\\\",\\\"libelleVoie\\\":\\\"PASTEUR\\\",\\\"mentionDistribution\\\":\\\"\\\",\\\"bureauCedex\\\":\\\"\\\",\\\"codePostal\\\":\\\"75009\\\",\\\"codeCommune\\\":\\\"75109\\\",\\\"codePays\\\":\\\"\\\",\\\"telephone\\\":\\\"\\\",\\\"telephone2\\\":\\\"\\\",\\\"telecopie\\\":\\\"\\\",\\\"adresseEMail\\\":\\\"\\\",\\\"codeDepartement\\\":\\\"75\\\",\\\"ancienIdentifiantDeLaStructure\\\":\\\"\\\",\\\"autoriteDenregistrement\\\":\\\"\\\"}]}]},\\\"SubjectOrganizationID\\\":\\\"\\\",\\\"SubjectRole\\\":[\\\"10^1.2.250.1.213.1.1.5.5\\\"],\\\"PSI_Locale\\\":\\\"1.2.250.1.213.1.3.1.1\\\",\\\"SubjectNameID\\\":\\\"899700218896\\\",\\\"family_name\\\":\\\"SPECIALISTE0021889\\\"}\"}]")
				.accept("application/json")).andExpect(status().isNotImplemented()).andDo(print());
	}

	/**
	 * Signature xades test no proof.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void signatureXadesTestNoProof() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/signatures/xadesbaselineb").file(doc)
				.param("idSignConf", "1").accept("application/json")).andExpect(status().isOk()).andDo(print());
	}
}
