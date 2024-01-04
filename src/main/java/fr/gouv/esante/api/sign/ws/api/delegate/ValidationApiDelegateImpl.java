/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.delegate;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import fr.gouv.esante.api.sign.bean.errors.ErreurCertificat;
import fr.gouv.esante.api.sign.bean.errors.ErreurSignature;
import fr.gouv.esante.api.sign.bean.metadata.MetaDatum;
import fr.gouv.esante.api.sign.bean.parameters.CertificateValidationParameters;
import fr.gouv.esante.api.sign.bean.parameters.ProofParameters;
import fr.gouv.esante.api.sign.bean.parameters.SignatureParameters;
import fr.gouv.esante.api.sign.bean.parameters.SignatureValidationParameters;
import fr.gouv.esante.api.sign.bean.proof.OpenIdTokenBean;
import fr.gouv.esante.api.sign.bean.rapports.RapportSignature;
import fr.gouv.esante.api.sign.bean.rapports.RapportValidationCertificat;
import fr.gouv.esante.api.sign.bean.rapports.RapportValidationSignature;
import fr.gouv.esante.api.sign.enums.MetaDataType;
import fr.gouv.esante.api.sign.service.ICACRLService;
import fr.gouv.esante.api.sign.service.ICertificateValidationService;
import fr.gouv.esante.api.sign.service.IProofGenerationService;
import fr.gouv.esante.api.sign.service.ISignatureService;
import fr.gouv.esante.api.sign.service.ISignatureValidationService;
import fr.gouv.esante.api.sign.service.impl.utils.Version;
import fr.gouv.esante.api.sign.utils.EsignsanteClientException;
import fr.gouv.esante.api.sign.utils.EsignsanteException;
import fr.gouv.esante.api.sign.utils.EsignsanteParseException;
import fr.gouv.esante.api.sign.utils.EsignsanteServerException;
import fr.gouv.esante.api.sign.ws.api.ValidationApiDelegate;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.bean.object.CertVerifConf;
import fr.gouv.esante.api.sign.ws.bean.object.ProofConf;
import fr.gouv.esante.api.sign.ws.bean.object.SignVerifConf;
import fr.gouv.esante.api.sign.ws.model.ESignSanteValidationReport;
import fr.gouv.esante.api.sign.ws.model.ESignSanteValidationReportWithProof;
import fr.gouv.esante.api.sign.ws.model.Erreur;
import fr.gouv.esante.api.sign.ws.model.Metadata;
import fr.gouv.esante.api.sign.ws.util.ESignatureType;
import fr.gouv.esante.api.sign.ws.util.SignWsUtils;
import fr.gouv.esante.api.sign.ws.util.WsVars;

/**
 * The Class ValidationApiDelegateImpl.
 */
@Service
public class ValidationApiDelegateImpl extends ApiDelegate implements ValidationApiDelegate {

	/** Default ESignSante major version. */
	private static final int MAJOR = 2;

	/** Default ESignSante version. */
	private static final Version DEFAULT_VERSION = new Version(MAJOR, 7, 9);

	/**
	 * The log.
	 */
	Logger log = LoggerFactory.getLogger(ValidationApiDelegateImpl.class);

	/** The signature validation service. */
	@Autowired
	private ISignatureValidationService signatureValidationService;

	/** The signature service. */
	@Autowired
	private ISignatureService signatureService;

	/** The certificate validation service. */
	@Autowired
	private ICertificateValidationService certificateValidationService;

	/** The proof generation service. */
	@Autowired
	private IProofGenerationService proofGenerationService;

	/** The service ca crl. */
	@Autowired
	private ICACRLService serviceCaCrl;

	/** The global configurations. */
	@Autowired
	private IGlobalConf globalConf;

	/** ESignSante Build Properties. */
	@Autowired
	private BuildProperties buildProperties;
	
	// Static vars
	private final String verifSignRequestType = "VerifSign";
	private final String verifCertRequestType = "VerifCert";

	/**
	 * Validate digital signature with proof.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @param proofParameters the proof parameters
	 * @param idProofConf     the id proof conf
	 * @param isXades         the is xades
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReportWithProof> validateDigitalSignatureWithProof(
			final Long idVerifSignConf, final MultipartFile doc, final ProofParameters proofParameters,
			final Long idProofConf, ESignatureType type) {
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteValidationReportWithProof> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

		final Optional<SignVerifConf> verifConf = globalConf.getSignatureVerificationById(idVerifSignConf.toString());
		final Optional<ProofConf> signProofConf = globalConf.getProofById(idProofConf.toString());
		if (!verifConf.isPresent() || !signProofConf.isPresent()) {
			re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			log.error("Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
		} else {
			if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
				if (doc == null || proofParamsMissing(proofParameters)) {
					re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				} else {
					final SignatureValidationParameters signVerifParams = verifConf.get().getSignVerifParams();
					final SignatureParameters signProofParams = signProofConf.get().getSignProofParams();
					re = validateWithProof(doc, proofParameters, type, signVerifParams, signProofParams);
					log.info("Validate Digital Signature With Proof Generated : {}", HttpStatus.OK.getReasonPhrase());
				}
			}
		}
		return re;
	}

	/**
	 * Validate with Proof.
	 *
	 * @param doc                      the doc
	 * @param proofParameters          the proof parameters
	 * @param type                     the signature type
	 * @param signValidationParameters the sign validation parameters
	 * @param signProofParams          the sign proof params
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReportWithProof> validateWithProof(final MultipartFile doc,
			final ProofParameters proofParameters, ESignatureType type,
			final SignatureValidationParameters signValidationParameters, final SignatureParameters signProofParams) {
		ResponseEntity<ESignSanteValidationReportWithProof> re;
		try {
			// Validation de la signature du document
			final RapportValidationSignature rapportVerifSignANS = genSignVerifReport(doc, type,
					signValidationParameters);

			// Génération de la preuve
			final String proof = proofGenerationService.generateSignVerifProof(rapportVerifSignANS, proofParameters,
					serviceCaCrl.getCacrlWrapper());

			// Contrôle du certificat de signature de la preuve
			final HttpStatus status = SignWsUtils.checkCertificate(signProofParams, serviceCaCrl.getCacrlWrapper());
			if (status != HttpStatus.CONTINUE) {
				re = new ResponseEntity<>(status);
			} else {
				// Signature de la preuve
				final RapportSignature rapportSignProofANS = signatureService.signXADESBaselineB(proof, signProofParams);
				final ESignSanteValidationReportWithProof rapport = populateResultSignWithProof(
						rapportVerifSignANS.getListeErreurSignature(), rapportVerifSignANS.getMetaData(),
						rapportVerifSignANS.isValide(), rapportSignProofANS.getDocSigne());
				re = new ResponseEntity<>(rapport, HttpStatus.OK);
			}
		} catch (final EsignsanteClientException | EsignsanteParseException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final EsignsanteServerException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | EsignsanteException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * Called operation.
	 *
	 * @param opName the op name
	 * @return the string
	 */
	private String calledOperation(final String opName) {
		final Optional<NativeWebRequest> request = getRequest();
		String operation = opName;
		if (request.isPresent()) {
			operation = request.get().getContextPath() + opName;
		}
		return operation;
	}

	/**
	 * Verif signature XMldsig with proof.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param idProofConf     the id proof conf
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReportWithProof> verifSignatureXMLdsigWithProof(
			final Long idVerifSignConf, final MultipartFile doc, final String requestId, final String proofTag,
			final String applicantId, final Long idProofConf) {
		log.info("Validation de signature XMLDsig avec preuve.");
		Version wsVersion = DEFAULT_VERSION;
		try {
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters(this.verifSignRequestType, requestId, proofTag, applicantId,
				calledOperation("/validation/signatures/xmldsigwithproof"), wsVersion);
		return validateDigitalSignatureWithProof(idVerifSignConf, doc, proofParameters, idProofConf,
				ESignatureType.XMLDSIG);
	}

	/**
	 * Verif signature xades with proof.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param idProofConf     the id proof conf * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReportWithProof> verifSignatureXadesWithProof(final Long idVerifSignConf,
			final MultipartFile doc, final String requestId, final String proofTag, final String applicantId,
			final Long idProofConf) {
		log.info("Validation de signature XADES-Baseline-B avec preuve.");
		Version wsVersion = DEFAULT_VERSION;
		try {
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters(this.verifSignRequestType, requestId, proofTag, applicantId,
				calledOperation("/validation/signatures/xadesbaselinebwithproof"), wsVersion);
		return validateDigitalSignatureWithProof(idVerifSignConf, doc, proofParameters, idProofConf,
				ESignatureType.XADES);
	}

	/**
	 * Verif signature pades with proof.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param idProofConf     the id proof conf
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReportWithProof> verifSignaturePadesWithProof(final Long idVerifSignConf,
			final MultipartFile doc, final String requestId, final String proofTag, final String applicantId,
			final Long idProofConf) {
		log.info("Validation de signature PADES-Baseline-B avec preuve.");
		Version wsVersion = DEFAULT_VERSION;
		try {
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters(this.verifSignRequestType, requestId, proofTag, applicantId,
				calledOperation("/validation/signatures/padesbaselinebwithproof"), wsVersion);

		try {
			// Remplissage de la liste des beans OpenId
			List<OpenIdTokenBean> tokens;
			tokens = SignWsUtils.convertOpenIdTokens(parseOpenIdTokenHeader());
			if (!tokens.isEmpty()) {
				proofParameters.setOpenidTokens(tokens);
			}
			return validateDigitalSignatureWithProof(idVerifSignConf, doc, proofParameters, idProofConf,
					ESignatureType.PADES);
		} catch (EsignsanteClientException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Validate digital signature.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @param type            the signature type
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReport> validateDigitalSignature(final Long idVerifSignConf,
			final MultipartFile doc, ESignatureType type) {
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteValidationReport> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

		final Optional<SignVerifConf> verifConf = globalConf.getSignatureVerificationById(idVerifSignConf.toString());

		if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
			if (doc == null) {
				re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (!verifConf.isPresent()) {
				re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
				log.error("Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
			} else {
				final SignatureValidationParameters signVerifParams = verifConf.get().getSignVerifParams();
				re = validate(doc, type, signVerifParams);
				log.info("Validate Digital Signature : {}", HttpStatus.OK.getReasonPhrase());
			}
		}
		return re;
	}

	/**
	 * Validate.
	 *
	 * @param doc                      the doc
	 * @param type                     the signature type
	 * @param signValidationParameters the sign validation parameters
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReport> validate(final MultipartFile doc, ESignatureType type,
			final SignatureValidationParameters signValidationParameters) {
		ResponseEntity<ESignSanteValidationReport> re;
		try {
			// Validation de la signature du document
			final RapportValidationSignature rapportVerifSignANS = genSignVerifReport(doc, type,
					signValidationParameters);
			final ESignSanteValidationReport rapport = populateResultSign(rapportVerifSignANS.getListeErreurSignature(),
					rapportVerifSignANS.getMetaData(), rapportVerifSignANS.isValide());

			re = new ResponseEntity<>(rapport, HttpStatus.OK);
		} catch (final EsignsanteClientException | EsignsanteParseException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final EsignsanteServerException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | EsignsanteException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * Verif signature XM ldsig.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReport> verifSignatureXMLdsig(final Long idVerifSignConf,
			final MultipartFile doc) {
		log.info("Validation de signature XMLDsig.");
		return validateDigitalSignature(idVerifSignConf, doc, ESignatureType.XMLDSIG);
	}

	/**
	 * Verif signature xades.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReport> verifSignatureXades(final Long idVerifSignConf,
			final MultipartFile doc) {
		log.info("Validation de signature XADES-Baseline-B.");
		return validateDigitalSignature(idVerifSignConf, doc, ESignatureType.XADES);
	}

	/**
	 * Verif signature pades.
	 *
	 * @param idVerifSignConf the id verif sign conf
	 * @param doc             the doc
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReport> verifSignaturePades(final Long idVerifSignConf,
			final MultipartFile doc) {
		log.info("Validation de signature PADES-Baseline-B.");
		return validateDigitalSignature(idVerifSignConf, doc, ESignatureType.PADES);
	}

	/**
	 * Generate rapport validation signature.
	 *
	 * @param doc                      original document
	 * @param type                     Xades, Pades or D-sig
	 * @param signValidationParameters signature validation parameters
	 * @return RapportValidationSignature
	 * @throws IOException         stream file exception
	 * @throws EsignsanteException asipsign exception
	 */
	private RapportValidationSignature genSignVerifReport(final MultipartFile doc, ESignatureType type,
			final SignatureValidationParameters signValidationParameters) throws IOException, EsignsanteException {

		// Validation de la signature du document
		final RapportValidationSignature rapportVerifSignANS;
		if (ESignatureType.XADES.equals(type)) {
			rapportVerifSignANS = signatureValidationService.validateXADESBaseLineBSignature(doc.getBytes(),
					signValidationParameters, serviceCaCrl.getCacrlWrapper());
		} else if (ESignatureType.PADES.equals(type)) {
			rapportVerifSignANS = signatureValidationService.validatePADESBaseLineBSignature(doc.getBytes(),
					signValidationParameters, serviceCaCrl.getCacrlWrapper());
		} else {
			rapportVerifSignANS = signatureValidationService.validateXMLDsigSignature(doc.getBytes(),
					signValidationParameters, serviceCaCrl.getCacrlWrapper());
		}

		return rapportVerifSignANS;
	}

	/**
	 * Verif certificat with proof.
	 *
	 * @param idVerifCertConf the id verif cert conf
	 * @param doc             the doc
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param idProofConf     the id proof conf
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReportWithProof> verifCertificatWithProof(final Long idVerifCertConf,
			final MultipartFile doc, final String requestId, final String proofTag, final String applicantId,
			final Long idProofConf) {
		log.info("Validation de certificat avec preuve.");
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteValidationReportWithProof> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

		final Optional<CertVerifConf> verifConf = globalConf.getCertificateVerificationById(idVerifCertConf.toString());
		final Optional<ProofConf> signProofConf = globalConf.getProofById(idProofConf.toString());

		Version wsVersion = DEFAULT_VERSION;
		try {
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters(this.verifCertRequestType, requestId, proofTag, applicantId,
				wsVersion);

		if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
			if (doc == null || proofParamsMissing(proofParameters)) {
				re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (!verifConf.isPresent() || !signProofConf.isPresent()) {
				re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
				log.error("Certificate Validation Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
			} else {
				final CertificateValidationParameters certVerifParams = verifConf.get().getCertVerifParams();
				final SignatureParameters signProofParams = signProofConf.get().getSignProofParams();
				re = validateCertWithProof(doc, certVerifParams, signProofParams, proofParameters);
				log.info("Certificate Validation Done, Proof Generated : {}", HttpStatus.OK.getReasonPhrase());
			}
		}
		return re;
	}

	/**
	 * Checks if proof params are present.
	 *
	 * @param proofParameters the proof parameters
	 * @return boolean
	 */
	private boolean proofParamsMissing(final ProofParameters proofParameters) {
		return proofParameters.getRequestid() == null || proofParameters.getProofTag() == null
				|| proofParameters.getApplicantId() == null;
	}

	/**
	 * Validate cert with proof.
	 *
	 * @param doc                      the doc
	 * @param certValidationParameters the cert validation parameters
	 * @param signProofParams          the sign proof params
	 * @param proofParameters          the proof parameters
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReportWithProof> validateCertWithProof(final MultipartFile doc,
			final CertificateValidationParameters certValidationParameters, final SignatureParameters signProofParams,
			final ProofParameters proofParameters) {

		ResponseEntity<ESignSanteValidationReportWithProof> re;

		try {
			final RapportValidationCertificat rapportVerifCertANS = createRapportValidationCertificat(doc,
					certValidationParameters);

			// Génération de la preuve
			final String proof = proofGenerationService.generateCertVerifProof(rapportVerifCertANS, proofParameters,
					serviceCaCrl.getCacrlWrapper());

			// Contrôle du certificat de signature de la preuve
			final HttpStatus status = SignWsUtils.checkCertificate(signProofParams, serviceCaCrl.getCacrlWrapper());
			if (status != HttpStatus.CONTINUE) {
				re = new ResponseEntity<>(status);
			} else {
				// Signature de la preuve
				final RapportSignature rapportSignProofANS = signatureService.signXADESBaselineB(proof,
						signProofParams);

				final ESignSanteValidationReportWithProof rapport = populateResultVerifCertWithProof(
						rapportVerifCertANS.getListeErreurCertificat(), rapportVerifCertANS.getMetaData(),
						rapportVerifCertANS.isValide(), rapportSignProofANS.getDocSigne());

				re = new ResponseEntity<>(rapport, HttpStatus.OK);
			}
		} catch (final EsignsanteClientException e3) {
			log.error(ExceptionUtils.getStackTrace(e3));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final EsignsanteServerException e3) {
			log.error(ExceptionUtils.getStackTrace(e3));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | EsignsanteException e3) {
			log.error(ExceptionUtils.getStackTrace(e3));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * Verif certificat.
	 *
	 * @param idVerifCertConf the id verif cert conf
	 * @param doc             the doc
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteValidationReport> verifCertificat(final Long idVerifCertConf,
			final MultipartFile doc) {
		log.info("Validation de certificat.");
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteValidationReport> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

		final Optional<CertVerifConf> verifConf = globalConf.getCertificateVerificationById(idVerifCertConf.toString());

		if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
			if (doc == null) {
				re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (!verifConf.isPresent()) {
				re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
				log.error("Certificate Validation Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
			} else {
				final CertificateValidationParameters certVerifParams = verifConf.get().getCertVerifParams();
				re = validateCert(doc, certVerifParams);
				log.info("Certificate Validation Done : {}", HttpStatus.OK.getReasonPhrase());
			}
		}
		return re;
	}

	/**
	 * Validate cert.
	 *
	 * @param doc                      the doc
	 * @param certValidationParameters the cert validation parameters
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteValidationReport> validateCert(final MultipartFile doc,
			final CertificateValidationParameters certValidationParameters) {

		ResponseEntity<ESignSanteValidationReport> re;

		try {
			final RapportValidationCertificat rapportVerifCertANS = createRapportValidationCertificat(doc,
					certValidationParameters);

			final ESignSanteValidationReport rapport = populateResultVerifCert(
					rapportVerifCertANS.getListeErreurCertificat(), rapportVerifCertANS.getMetaData(),
					rapportVerifCertANS.isValide());

			re = new ResponseEntity<>(rapport, HttpStatus.OK);

		} catch (final EsignsanteClientException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final EsignsanteServerException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | EsignsanteException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * Generate rapport validation certificat.
	 * 
	 * @param doc                      original document
	 * @param certValidationParameters certificate validation paramters
	 * @return RapportValidationCertificat
	 * @throws EsignsanteException asipsign exception
	 * @throws IOException         stream file exception
	 */
	private RapportValidationCertificat createRapportValidationCertificat(final MultipartFile doc,
			final CertificateValidationParameters certValidationParameters) throws EsignsanteException, IOException {

		final RapportValidationCertificat rapportVerifCertANS;

		if (isBinaryFile(doc)) {
			rapportVerifCertANS = certificateValidationService.validateCertificat(doc.getBytes(),
					certValidationParameters, serviceCaCrl.getCacrlWrapper());
		} else {
			final String docString = new String(doc.getBytes(), UniversalDetector.detectCharset(doc.getInputStream()));
			rapportVerifCertANS = certificateValidationService.validateCertificat(docString, certValidationParameters,
					serviceCaCrl.getCacrlWrapper());
		}

		return rapportVerifCertANS;
	}

	/**
	 * Checks if is binary file. Guess whether given file is binary. Just checks for
	 * anything under 0x09.
	 * 
	 * @param doc the doc
	 * @return true si binaire / false si texte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean isBinaryFile(final MultipartFile doc) throws IOException {

		boolean isBinary = false;
		final int maxSize = 1024;
		final int textThreshold = 95;
		final int base = 100;
		final InputStream in = doc.getInputStream();
		int size = in.available();
		if (size > maxSize) {
			size = maxSize;
		}
		final byte[] data = new byte[size];
		if (in.read(data) > 0) {
			log.debug("binary stream data not empty, ok to proceed");
		}
		in.close();

		int ascii = 0;
		int other = 0;

		for (final byte b : data) {
			if (b < 0x09) {
				isBinary = true;
				break;
			} else if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) {
				ascii++;
			} else if (b >= 0x20 && b <= 0x7E) {
				ascii++;
			} else {
				other++;
			}
		}

		if (!isBinary) {
			if (!(other == 0)) {
				isBinary = base * other / (ascii + other) > textThreshold;
			}
		}

		return isBinary;
	}

	/**
	 * Populate result sign.
	 *
	 * @param erreursSignature the erreurs signature
	 * @param metadata         the metadata
	 * @param isValide         the is valide
	 * @return the rapport verif
	 */
	private ESignSanteValidationReport populateResultSign(final List<ErreurSignature> erreursSignature,
			final List<MetaDatum> metadata, final boolean isValide) {

		final ESignSanteValidationReport rapport = new ESignSanteValidationReport();

		rapport.setValide(isValide);

		final List<Erreur> erreurs = new ArrayList<>();

		for (final ErreurSignature erreurANS : erreursSignature) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}

		rapport.setErreurs(erreurs);

		final List<Metadata> metas = new ArrayList<>();
		for (final MetaDatum metadatum : metadata) {
			final Metadata meta = new Metadata();
			meta.setTypeMetadata(metadatum.getType().getName());
			if (metadatum.getType().equals(MetaDataType.RAPPORT_DIAGNOSTIQUE)
					|| metadatum.getType().equals(MetaDataType.RAPPORT_DSS)) {
				meta.setMessage(Base64.getEncoder().encodeToString(metadatum.getValue().getBytes()));
			} else {
				meta.setMessage(metadatum.getValue());
			}
			metas.add(meta);
		}
		rapport.setMetaData(metas);
		return rapport;
	}

	/**
	 * Populate result sign with proof.
	 *
	 * @param erreursSignature the erreurs signature
	 * @param metadata         the metadata
	 * @param isValide         the is valide
	 * @param preuve           the preuve
	 * @return the rapport verif with proof
	 */
	private ESignSanteValidationReportWithProof populateResultSignWithProof(
			final List<ErreurSignature> erreursSignature, final List<MetaDatum> metadata, final boolean isValide,
			final String preuve) {

		final ESignSanteValidationReportWithProof rapport = new ESignSanteValidationReportWithProof();

		rapport.setValide(isValide);
		rapport.setPreuve(Base64.getEncoder().encodeToString(preuve.getBytes()));
		final List<Erreur> erreurs = new ArrayList<>();

		for (final ErreurSignature erreurANS : erreursSignature) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}

		rapport.setErreurs(erreurs);

		final List<Metadata> metas = new ArrayList<>();
		for (final MetaDatum metadatum : metadata) {
			final Metadata meta = new Metadata();
			meta.setTypeMetadata(metadatum.getType().getName());
			if (metadatum.getType().equals(MetaDataType.RAPPORT_DIAGNOSTIQUE)
					|| metadatum.getType().equals(MetaDataType.RAPPORT_DSS)) {
				meta.setMessage(Base64.getEncoder().encodeToString(metadatum.getValue().getBytes()));
			} else {
				meta.setMessage(metadatum.getValue());
			}
			metas.add(meta);
		}
		rapport.setMetaData(metas);

		return rapport;
	}

	/**
	 * Populate result verif cert.
	 *
	 * @param erreursVerifCert the erreurs verif cert
	 * @param metadata         the metadata
	 * @param isValide         the is valide
	 * @return the rapport verif
	 */
	private ESignSanteValidationReport populateResultVerifCert(final List<ErreurCertificat> erreursVerifCert,
			final List<MetaDatum> metadata, final boolean isValide) {
		final ESignSanteValidationReport rapport = new ESignSanteValidationReport();

		rapport.setValide(isValide);

		final List<Erreur> erreurs = new ArrayList<>();

		for (final ErreurCertificat erreurANS : erreursVerifCert) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getType().getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}
		rapport.setErreurs(erreurs);
		final List<Metadata> metas = new ArrayList<>();

		for (final MetaDatum metadatum : metadata) {
			final Metadata meta = new Metadata();
			meta.setTypeMetadata(metadatum.getType().getName());
			if (metadatum.getType().equals(MetaDataType.RAPPORT_DIAGNOSTIQUE)
					|| metadatum.getType().equals(MetaDataType.RAPPORT_DSS)) {
				meta.setMessage(Base64.getEncoder().encodeToString(metadatum.getValue().getBytes()));
			} else {
				meta.setMessage(metadatum.getValue());
			}
			metas.add(meta);
		}
		rapport.setMetaData(metas);

		return rapport;

	}

	/**
	 * Populate result verif cert with proof.
	 *
	 * @param erreursVerifCert the erreurs verif cert
	 * @param metadata         the metadata
	 * @param isValide         the is valide
	 * @param proof            the proof
	 * @return the rapport verif with proof
	 */
	private ESignSanteValidationReportWithProof populateResultVerifCertWithProof(
			final List<ErreurCertificat> erreursVerifCert, final List<MetaDatum> metadata, final boolean isValide,
			final String proof) {
		final ESignSanteValidationReportWithProof rapport = new ESignSanteValidationReportWithProof();

		rapport.setValide(isValide);
		rapport.setPreuve(Base64.getEncoder().encodeToString(proof.getBytes()));
		final List<Erreur> erreurs = new ArrayList<>();

		for (final ErreurCertificat erreurANS : erreursVerifCert) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getType().getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}
		rapport.setErreurs(erreurs);
		final List<Metadata> metas = new ArrayList<>();

		for (final MetaDatum metadatum : metadata) {
			final Metadata meta = new Metadata();
			meta.setTypeMetadata(metadatum.getType().getName());
			if (metadatum.getType().equals(MetaDataType.RAPPORT_DIAGNOSTIQUE)
					|| metadatum.getType().equals(MetaDataType.RAPPORT_DSS)) {
				meta.setMessage(Base64.getEncoder().encodeToString(metadatum.getValue().getBytes()));
			} else {
				meta.setMessage(metadatum.getValue());
			}
			metas.add(meta);
		}
		rapport.setMetaData(metas);

		return rapport;
	}

}
