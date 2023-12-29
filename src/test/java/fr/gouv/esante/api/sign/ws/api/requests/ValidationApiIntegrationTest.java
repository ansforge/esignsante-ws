/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.ValidationApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.ValidationApiDelegateImpl;

/**
 * The Class ValidationApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ValidationApiController.class, ValidationApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class})
@Import(ProjectInfoAutoConfiguration.class)
@AutoConfigureMockMvc
@WebMvcTest(ValidationApiController.class)
public class ValidationApiIntegrationTest {

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The doc. */
	private MockMultipartFile doc, docFragment, docXadesSigned;

	/** The pdf. */
	private MockMultipartFile pdf, pdf2;

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
	@BeforeEach
	public void init() throws Exception {
		doc = new MockMultipartFile("file", "XMLDsig_enveloping_ok.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("XMLDsig_enveloping_ok.xml"));
		
		docXadesSigned = new MockMultipartFile("file", "XADES_enveloped_ok.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("XADES_enveloped_ok.xml"));
		
		docFragment = new MockMultipartFile("file", "signed_fragment_inside_security.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("signed_fragment_inside_security.xml"));
		
		pdf = new MockMultipartFile("file", "doc_signe_pades.pdf", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("doc_signe_pades.pdf"));

		pdf2 = new MockMultipartFile("file", "ANS organigramme-signed-pades-baseline-b.pdf", null,
				Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("ANS organigramme-signed-pades-baseline-b.pdf"));
	}

	/**
	 * Cas passant validation XMLDsig.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXMLdsigTest() throws Exception {

		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xmldsigwithproof").file(doc)
						.param("idVerifSignConf", "2").param("requestId", "Request-1").param("proofTag", "MonTAG")
						.param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(4, body.names().length());
		assertEquals(0, body.getJSONArray("erreurs").length());
	}
	
	/**
	 * Cas passant validation XMLDsig par fragment.
	 * Test en échec lors du build par maven, mais en succès en test JUnit 5 unitairement, à vérifier.
	 *
	 * @throws Exception the exception
	 */
	
	@Test
	public void verifSignXMLdsigFragmentTest() throws Exception {

		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xmldsigwithproof").file(docFragment)
						.param("idVerifSignConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
						.param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(4, body.names().length());
	}

	/**
	 * Cas passant validation XADES.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXadesBaselineBTest() throws Exception {

		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xadesbaselinebwithproof").file(docXadesSigned)
						.param("idVerifSignConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
						.param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(4, body.names().length());
		assertEquals(0, body.getJSONArray("erreurs").length());
	}

	/**
	 * Cas passant validation PADES avec preuve.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignPadesBaselineBTestWithProof() throws Exception {
		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.multipart("/validation/signatures/padesbaselinebwithproof").file(pdf).param("idVerifSignConf", "1")
				.param("requestId", "Request-1").param("proofTag", "MonTAG").param("applicantId", "RPPS")
				.param("idProofConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(4, body.names().length());
	}

	/**
	 * Cas passant validation PADES sans preuve.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignPadesBaselineBTest() throws Exception {
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/padesbaselineb").file(pdf)
						.param("idVerifSignConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
	}

	/**
	 * Cas non passant validation XADES certificat expiré.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXadesExpireTest() throws Exception {
		final MockMultipartFile document = new MockMultipartFile("file", "XADESCertExpire.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("XADESCertExpire.xml"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xadesbaselineb").file(document)
						.param("idVerifSignConf", "2").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(1, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERSIGN05\"}"), "Le code erreur attendu n'est pas le bon");
	}

	/**
	 * Cas non passant - validation certificat expiré & révoqué.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatExpireTest() throws Exception {
		String docName = "rpps.tra.henix.asipsante.fr-sign-expire.pem";
		final MockMultipartFile cert = new MockMultipartFile("file", docName,
				null, Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(docName));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/certificats").file(cert)
						.param("idVerifCertConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(1, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERCERT01\"}"), "Le code erreur attendu n'est pas le bon");
	}

	/**
	 * Cas non passant de validation de signature XMLDSIG avec preuve avec un
	 * document non conforme (XML corrompu / cassé).
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXMLdsigTestBadFile() throws Exception {
		doc = new MockMultipartFile("file", "TOM_FICHIER_bad.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("TOM_FICHIER_bad.xml"));

		mockMvc.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xmldsigwithproof").file(doc)
				.param("idSignConf", "1").param("idVerifSignConf", "1").param("requestId", "Request-1")
				.param("proofTag", "MonTAG").param("applicantId", "RPPS").param("idProofConf", "1")
				.with(csrf()).accept("application/json")).andExpect(status().isNotImplemented()).andDo(print());
	}

	/**
	 * Cas non passant validation XMLDSIG document altéré.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXmldsigEnveloppeeDocAltereTest() throws Exception {
		final MockMultipartFile document = new MockMultipartFile("file",
				"Signature_Dsig_enveloppee_document_modifie.xml", null, Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("Signature_Dsig_enveloppee_document_modifie.xml"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xmldsig").file(document)
						.param("idVerifSignConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(2, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERSIGN01\"}"), "Le code erreur attendu n'est pas le bon");
		assertTrue(body.getJSONArray("erreurs").get(1).toString().endsWith("\"codeErreur\":\"ERDOCN01\"}"), "Le code erreur attendu n'est pas le bon");
	}

	/**
	 * Cas non passant validation xades enveloppante document altéré.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXadesEnveloppanteDocAltereTest() throws Exception {
		final MockMultipartFile document = new MockMultipartFile("file", "sign_xades_enveloping_modifiedDoc.xml", null,
				Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("sign_xades_enveloping_modifiedDoc.xml"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xadesbaselineb").file(document)
						.param("idVerifSignConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		// expected 2 errors: an expired certificate & altered document
		assertEquals(2, body.getJSONArray("erreurs").length());
	}

	/**
	 * Cas non passant validation XADES mauvaise autorité.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifSignXadesBadACTest() throws Exception {
		final MockMultipartFile document = new MockMultipartFile("file", "SignatureCA-PERSONNE.xml", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("SignatureCA-PERSONNE.xml"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/signatures/xadesbaselineb").file(document)
						.param("idVerifSignConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(1, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERDOCN01\"}"), "Le code erreur attendu n'est pas le bon");
	}

	/**
	 * Cas passant validation certificat PEM.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatTest() throws Exception {
		final MockMultipartFile cert = new MockMultipartFile("file", "asip-p12-EL-TEST-ORG-SIGN-20181116-141712.pem",
				null, Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("asip-p12-EL-TEST-ORG-SIGN-20181116-141712.pem"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/certificats").file(cert)
						.param("idVerifCertConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(0, body.getJSONArray("erreurs").length());
	}

	/**
	 * Cas passant validation certificat PEM avec preuve.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatWithProofTest() throws Exception {
		final MockMultipartFile cert = new MockMultipartFile("file", "asip-p12-EL-TEST-ORG-SIGN-20181116-141712.pem",
				null, Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("asip-p12-EL-TEST-ORG-SIGN-20181116-141712.pem"));
		final MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.multipart("/validation/certificatswithproof").file(cert).param("idSignConf", "1")
						.param("idVerifCertConf", "1").param("requestId", "Request-1").param("proofTag", "MonTAG")
						.param("applicantId", "RPPS").param("idProofConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(4, body.names().length());
		assertEquals(0, body.getJSONArray("erreurs").length());
	}

	/**
	 * Cas passant validation certificat PEM avec mauvaise autorité.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatBadACTest() throws Exception {
		final MockMultipartFile cert = new MockMultipartFile("file", "classe4.pem", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("classe4.pem"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/certificats").file(cert)
						.param("idVerifCertConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(1, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERCERT04\"}"), "Le code erreur attendu n'est pas le bon");
	}

	/**
	 * Cas passant validation certificat DER.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatDERTest() throws Exception {
		final MockMultipartFile cert = new MockMultipartFile("file", "asip-p12-EL-TEST-ORG-SIGN-20181116-141712.der",
				null, Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("asip-p12-EL-TEST-ORG-SIGN-20181116-141712.der"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/certificats").file(cert)
						.param("idVerifCertConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(0, body.getJSONArray("erreurs").length());
	}

	/**
	 * Cas non passant validation certificat DER (mauvaise autorité).
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifCertificatBadCADERTest() throws Exception {
		final MockMultipartFile cert = new MockMultipartFile("file", "2600301752-Auth.crt", null,
				Thread.currentThread().getContextClassLoader().getResourceAsStream("2600301752-Auth.crt"));
		final MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/validation/certificats").file(cert)
						.param("idVerifCertConf", "1").with(csrf()).accept("application/json"))
				.andExpect(status().isOk()).andDo(print()).andReturn();

		final JSONObject body = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(3, body.names().length());
		assertEquals(1, body.getJSONArray("erreurs").length());
		assertTrue(body.getJSONArray("erreurs").get(0).toString().endsWith("\"codeErreur\":\"ERCERT04\"}"), "Le code erreur attendu n'est pas le bon");
	}
}
