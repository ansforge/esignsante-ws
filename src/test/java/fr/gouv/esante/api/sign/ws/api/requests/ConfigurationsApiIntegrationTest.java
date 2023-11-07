/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;
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

import fr.gouv.esante.api.sign.config.AppGlobalConfig;
import fr.gouv.esante.api.sign.config.CACRLConfig;
import fr.gouv.esante.api.sign.config.SecurityConfig;
import fr.gouv.esante.api.sign.ws.api.ConfigurationsApiController;
import fr.gouv.esante.api.sign.ws.api.delegate.ConfigurationsApiDelegateImpl;

/**
 * The Class ConfigurationsApiIntegrationTest.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ConfigurationsApiController.class, ConfigurationsApiDelegateImpl.class, AppGlobalConfig.class, SecurityConfig.class, CACRLConfig.class  })
@AutoConfigureMockMvc
@WebMvcTest(ConfigurationsApiController.class)
public class ConfigurationsApiIntegrationTest {

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
     * Configurations get test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("deprecation")
	@Test
    public void configurationsGetTest() throws Exception {

        final String expectedBody = "{\"signature\":[{\"idSignConf\":1,\"associatedProofId\":1,\"description\":\"Scheduling Test.\",\"signaturePackaging\":\"ENVELOPING\",\"digestAlgorithm\":\"SHA512\",\"canonicalisationAlgorithm\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=testsign.test.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idSignConf\":2,\"associatedProofId\":2,\"description\":\"Fichier de configuration de Ioanna pour les signatures de documents.\",\"signaturePackaging\":\"ENVELOPING\",\"digestAlgorithm\":\"SHA512\",\"canonicalisationAlgorithm\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=testsign.test.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idSignConf\":3,\"associatedProofId\":5,\"description\":\"Fichier de configuration de Ioanna pour les signatures de documents.\",\"signaturePackaging\":\"ENVELOPED\",\"digestAlgorithm\":\"SHA512\",\"canonicalisationAlgorithm\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=testsign.test.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idSignConf\":5,\"associatedProofId\":6,\"description\":\"Scheduling Test.\",\"signaturePackaging\":\"ENVELOPING\",\"digestAlgorithm\":\"SHA512\",\"canonicalisationAlgorithm\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=rpps.tra.henix\\\\;asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idSignConf\":6,\"associatedProofId\":7,\"description\":\"Scheduling Test.\",\"signaturePackaging\":\"ENVELOPING\",\"digestAlgorithm\":\"SHA512\",\"canonicalisationAlgorithm\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=tomwsrevoque.henix.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"}],\"signatureVerification\":[{\"idVerifSignConf\":1,\"description\":\"\",\"rules\":[{\"id\":\"TrustedCertificat\",\"description\":\"L'autorité de Certification est reconnue.\"},{\"id\":\"FormatSignature\",\"description\":\"Le format de signature est correct (Xades Baseline B, XMlDsig-core-1).\"},{\"id\":\"SignatureCertificatValide\",\"description\":\"La signature du certificat est valide.\"},{\"id\":\"ExistenceBaliseSigningTime\",\"description\":\"La balise 'SigningTime' existe dans la signature.\"},{\"id\":\"ExistenceDuCertificatDeSignature\",\"description\":\"Le certificat utilisé pour la signature existe dans la signature.\"},{\"id\":\"ExpirationCertificat\",\"description\":\"Le certificat n'est pas expiré.\"},{\"id\":\"NonRepudiation\",\"description\":\"L'usage de la clé du certificat correspond à un usage de signature électronique et de non répudiation.\"},{\"id\":\"RevocationCertificat\",\"description\":\"Le certificat n'est pas révoqué.\"},{\"id\":\"SignatureNonVide\",\"description\":\"La signature existe et n'est pas vide.\"},{\"id\":\"SignatureIntacte\",\"description\":\"La signature est intacte.\"},{\"id\":\"DocumentIntact\",\"description\":\"Le document est intact.\"}],\"metaData\":[\"DATE_SIGNATURE\",\"DN_CERTIFICAT\",\"RAPPORT_DIAGNOSTIQUE\",\"DOCUMENT_ORIGINAL_NON_SIGNE\",\"RAPPORT_DSS\"]},{\"idVerifSignConf\":2,\"description\":\"\",\"rules\":[{\"id\":\"TrustedCertificat\",\"description\":\"L'autorité de Certification est reconnue.\"},{\"id\":\"ExistenceBaliseSigningTime\",\"description\":\"La balise 'SigningTime' existe dans la signature.\"},{\"id\":\"ExistenceDuCertificatDeSignature\",\"description\":\"Le certificat utilisé pour la signature existe dans la signature.\"},{\"id\":\"ExpirationCertificat\",\"description\":\"Le certificat n'est pas expiré.\"},{\"id\":\"NonRepudiation\",\"description\":\"L'usage de la clé du certificat correspond à un usage de signature électronique et de non répudiation.\"},{\"id\":\"RevocationCertificat\",\"description\":\"Le certificat n'est pas révoqué.\"},{\"id\":\"SignatureNonVide\",\"description\":\"La signature existe et n'est pas vide.\"},{\"id\":\"FormatSignature\",\"description\":\"Le format de signature est correct (Xades Baseline B, XMlDsig-core-1).\"},{\"id\":\"DocumentIntact\",\"description\":\"Le document est intact.\"},{\"id\":\"SignatureIntacte\",\"description\":\"La signature est intacte.\"},{\"id\":\"SignatureCertificatValide\",\"description\":\"La signature du certificat est valide.\"}],\"metaData\":[\"\"]}],\"certificatVerification\":[{\"idVerifCertConf\":1,\"description\":\"\",\"rules\":[{\"id\":\"ExpirationCertificat\",\"description\":\"Le certificat n'est pas expiré.\"},{\"id\":\"RevocationCertificat\",\"description\":\"Le certificat n'est pas révoqué.\"},{\"id\":\"SignatureCertificatValide\",\"description\":\"La signature du certificat est valide.\"},{\"id\":\"TrustedCertificat\",\"description\":\"L'autorité de Certification est reconnue.\"},{\"id\":\"NonRepudiation\",\"description\":\"L'usage de la clé du certificat correspond à un usage de signature électronique et de non répudiation.\"}],\"metaData\":[\"DN_CERTIFICAT\",\"RAPPORT_DIAGNOSTIQUE\",\"RAPPORT_DSS\"]},{\"idVerifCertConf\":2,\"description\":\"\",\"rules\":[{\"id\":\"ExpirationCertificat\",\"description\":\"Le certificat n'est pas expiré.\"},{\"id\":\"RevocationCertificat\",\"description\":\"Le certificat n'est pas révoqué.\"},{\"id\":\"SignatureCertificatValide\",\"description\":\"La signature du certificat est valide.\"},{\"id\":\"TrustedCertificat\",\"description\":\"L'autorité de Certification est reconnue.\"},{\"id\":\"NonRepudiation\",\"description\":\"L'usage de la clé du certificat correspond à un usage de signature électronique et de non répudiation.\"}],\"metaData\":[\"\"]}],\"proof\":[{\"idProofConf\":1,\"description\":\"Fichier par defaut pour la preuve\",\"signaturePackagingForProof\":\"ENVELOPED\",\"digestAlgorithmForProof\":\"SHA256\",\"canonicalisationAlgorithmForProof\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=testsign.test.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idProofConf\":2,\"description\":\"Configuration de Ioanna pour la preuve\",\"signaturePackagingForProof\":\"ENVELOPED\",\"digestAlgorithmForProof\":\"SHA256\",\"canonicalisationAlgorithmForProof\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=asipsign.preuve.henix.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idProofConf\":5,\"description\":\"Fichier par defaut pour la preuve\",\"signaturePackagingForProof\":\"ENVELOPED\",\"digestAlgorithmForProof\":\"SHA256\",\"canonicalisationAlgorithmForProof\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=rpps.tra.henix\\\\;asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idProofConf\":6,\"description\":\"Fichier par defaut pour la preuve\",\"signaturePackagingForProof\":\"ENVELOPED\",\"digestAlgorithmForProof\":\"SHA256\",\"canonicalisationAlgorithmForProof\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=tomwsrevoque.henix.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"},{\"idProofConf\":7,\"description\":\"Fichier par defaut pour la preuve\",\"signaturePackagingForProof\":\"ENVELOPED\",\"digestAlgorithmForProof\":\"SHA256\",\"canonicalisationAlgorithmForProof\":\"http://www.w3.org/2001/10/xml-exc-c14n#\",\"dn\":\"CN=mock.platines.henix.asipsante.fr,OU=318751275100020,O=ASIP-SANTE,ST=Paris (75),C=FR\"}]}";

        final MvcResult result = mockMvc.perform(get("/configurations").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn();

        assertEquals(expectedBody, result.getResponse().getContentAsString());
    }
}
