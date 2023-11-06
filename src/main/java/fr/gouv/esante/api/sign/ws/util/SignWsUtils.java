/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.util;

import fr.gouv.esante.api.sign.bean.cacrl.CACRLWrapper;
import fr.gouv.esante.api.sign.bean.parameters.CertificateValidationParameters;
import fr.gouv.esante.api.sign.bean.parameters.SignatureParameters;
import fr.gouv.esante.api.sign.bean.proof.OpenIdTokenBean;
import fr.gouv.esante.api.sign.bean.rapports.RapportValidationCertificat;
import fr.gouv.esante.api.sign.service.ICertificateValidationService;
import fr.gouv.esante.api.sign.service.impl.CertificateValidationServiceImpl;
import fr.gouv.esante.api.sign.utils.EsignsanteClientException;
import fr.gouv.esante.api.sign.utils.EsignsanteException;
import fr.gouv.esante.api.sign.utils.EsignsanteServerException;
import fr.gouv.esante.api.sign.validation.certificat.rules.ICertificatVisitor;
import fr.gouv.esante.api.sign.validation.certificat.rules.impl.ExpirationCertificat;
import fr.gouv.esante.api.sign.validation.certificat.rules.impl.NonRepudiation;
import fr.gouv.esante.api.sign.validation.certificat.rules.impl.RevocationCertificat;
import fr.gouv.esante.api.sign.validation.certificat.rules.impl.TrustedCertificat;
import fr.gouv.esante.api.sign.ws.model.OpenidToken;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

/**
 * The Class SignWsUtils.
 */
public class SignWsUtils {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SignWsUtils.class);

    /**
     * Instantiates a new sign ws utils.
     */
    private SignWsUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * ANS http error.
     *
     * @param e the e
     * @return the http status
     */
    public static HttpStatus asipHttpError(final EsignsanteException e) {
        LOG.error(ExceptionUtils.getStackTrace(e));
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof EsignsanteClientException) {
            status = HttpStatus.NOT_IMPLEMENTED;
        } else if (e instanceof EsignsanteServerException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        return status;
    }

    /**
     * Retourne la liste des certificats contenus dans un KeyStore.
     *
     * @return la liste des certificats contenus dans un KeyStore
     * @throws GeneralSecurityException the general security exception
     */
    private static List<X509Certificate> getSignatureCertificates(final KeyStore pkcs12KeyStore)
            throws GeneralSecurityException {

        final List<X509Certificate> list = new ArrayList<>();

        final Enumeration<String> aliases = pkcs12KeyStore.aliases();

        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            final X509Certificate cert = (X509Certificate) pkcs12KeyStore.getCertificate(alias);
            list.add(cert);

        }
        return list;
    }

    /**
     * Contrôle de la validité des certificats de signature.
     *
     * @param signParams   the sign params
     * @param caCrlWrapper the ca crl wrapper
     * @return the http status
     * @throws AsipSignException the asip sign exception
     */
    public static HttpStatus checkCertificate(

            final SignatureParameters signParams, final CACRLWrapper caCrlWrapper) throws EsignsanteException {
        // On contrôle le certificat qui va signer
        final List<X509Certificate> certificateList;
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        try {
            if (signParams != null) {
                certificateList = getSignatureCertificates(signParams.getKeyStore());

                final ICertificateValidationService certValidationService = new CertificateValidationServiceImpl();
                final List<ICertificatVisitor> certRules = new ArrayList<>();
                certRules.add(new ExpirationCertificat());
                certRules.add(new NonRepudiation());
                certRules.add(new RevocationCertificat());
                certRules.add(new TrustedCertificat());
                final CertificateValidationParameters certParams = new CertificateValidationParameters();
                certParams.setRules(certRules);

                // On boucle sur tous les certificats du Keystore
                // et on lance les règles de validation
                boolean isValide = true;
                for (final X509Certificate cert : certificateList) {
                    final RapportValidationCertificat rapportValidationCert = certValidationService
                            .validateCertificat(cert.getEncoded(), certParams, caCrlWrapper);
                    if (!rapportValidationCert.isValide()) {
                        final String error = rapportValidationCert.getListeErreurCertificat().toString();
                        LOG.error(error);
                        isValide = false;
                    }
                }
                if (isValide) {
                    status = HttpStatus.CONTINUE;
                }
            }
        } catch (final GeneralSecurityException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }
        return status;
    }
    
    public static List<OpenIdTokenBean> convertOpenIdTokens(final List<OpenidToken> openidTokens) {
        List<OpenIdTokenBean> listTokenBeans = new ArrayList<OpenIdTokenBean>();
        if (!openidTokens.isEmpty()) {
	        for(OpenidToken token: openidTokens) {
	        	OpenIdTokenBean tokenBean = new OpenIdTokenBean();
	        	tokenBean.setAccessToken(token.getAccessToken());
	        	byte [] instrospectionresponseB64 =token.getIntrospectionResponse().getBytes();
	        	String encodedIR = Base64.getEncoder().encodeToString(instrospectionresponseB64);
	        	tokenBean.setIntrospectionResponse(encodedIR);
	        	byte [] userInfoB64 =token.getUserInfo().getBytes();
	        	String encodedUI = Base64.getEncoder().encodeToString(userInfoB64);
	        	tokenBean.setUserInfo(encodedUI);
	        	listTokenBeans.add(tokenBean);
	        }
        }
        return listTokenBeans;
    }
}
