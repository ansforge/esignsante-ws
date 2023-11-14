/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.gouv.esante.api.sign.ws.bean.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.gouv.esante.api.sign.bean.parameters.SignatureParameters;
import fr.gouv.esante.api.sign.ws.bean.ConfigurationLoader;
import fr.gouv.esante.api.sign.ws.bean.object.utils.CertificateFormatting;
import fr.gouv.esante.api.sign.ws.util.Secrets;
import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * The type Signature conf.
 */
public class SignatureConf {

	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(SignatureConf.class);

	/**
	 * signParams.
	 */
	@JsonIgnore
	private SignatureParameters signParams;

	/**
	 * idSignConf.
	 */
	private String idSignConf;

	/**
	 * secret.
	 */
	private String secret;

	/**
	 * idProofConf.
	 */
	private String idProofConf;

	/**
	 * description.
	 */
	private String description;

	/**
	 * certificate.
	 */
	private String certificate;

	/**
	 * privateKey.
	 */
	private String privateKey;

	/**
	 * canonicalisationAlgorithm.
	 */
	private String canonicalisationAlgorithm;

	/**
	 * digestAlgorithm.
	 */
	private String digestAlgorithm;

	/**
	 * signaturePackaging.
	 */
	private String signaturePackaging;

	/**
	 * signId.
	 */
	private String signId;

	/**
	 * signValueId.
	 */
	private String signValueId;

	/**
	 * objectId.
	 */
	private String objectId;

	/**
	 * element to sign (optional)
	 */
	@Nullable
	private String elementToSign;

	/**
	 * Instantiates a new Signature conf.
	 */
	public SignatureConf() {
	}

	/**
	 * No secret match boolean.
	 *
	 * @param secret the secret
	 * @return the boolean
	 */
	public boolean noSecretMatch(final String secret) {
		return Arrays.stream(getSecret().trim().split(" ")).noneMatch(hash -> Secrets.match(secret, hash));
	}

	/**
	 * Gets sign params.
	 *
	 * @return the sign params
	 */
	public SignatureParameters getSignParams() {
		return signParams;
	}

	/**
	 * Gets id sign conf.
	 *
	 * @return the id sign conf
	 */
	public String getIdSignConf() {
		return idSignConf;
	}

	/**
	 * Sets id sign conf.
	 *
	 * @param idSignConf the id sign conf
	 */
	public void setIdSignConf(final String idSignConf) {
		this.idSignConf = idSignConf;
	}

	/**
	 * Gets secret.
	 *
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Sets secret.
	 *
	 * @param secret the secret
	 */
	public void setSecret(final String secret) {
		this.secret = secret;
	}

	/**
	 * Gets id proof conf.
	 *
	 * @return the id proof conf
	 */
	public String getIdProofConf() {
		return idProofConf;
	}

	/**
	 * Sets id proof conf.
	 *
	 * @param idProofConf the id proof conf
	 */
	public void setIdProofConf(final String idProofConf) {
		this.idProofConf = idProofConf;
	}

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets certificate.
	 *
	 * @return the certificate
	 */
	public String getCertificate() {
		return certificate;
	}

	/**
	 * Sets certificate.
	 *
	 * @param certificate the certificate
	 */
	public void setCertificate(final String certificate) {
		this.certificate = CertificateFormatting.formatCertificate(certificate);
	}

	/**
	 * Gets private key.
	 *
	 * @return the private key
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * Sets private key.
	 *
	 * @param privateKey the private key
	 */
	public void setPrivateKey(final String privateKey) {
		this.privateKey = CertificateFormatting.formatPrivateKey(privateKey);
	}

	/**
	 * Gets canonicalisation algorithm.
	 *
	 * @return the canonicalisation algorithm
	 */
	public String getCanonicalisationAlgorithm() {
		return canonicalisationAlgorithm;
	}

	/**
	 * Sets canonicalisation algorithm.
	 *
	 * @param canonicalisationAlgorithm the canonicalisation algorithm
	 */
	public void setCanonicalisationAlgorithm(final String canonicalisationAlgorithm) {
		this.canonicalisationAlgorithm = canonicalisationAlgorithm;
	}

	/**
	 * Gets digest algorithm.
	 *
	 * @return the digest algorithm
	 */
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * Sets digest algorithm.
	 *
	 * @param digestAlgorithm the digest algorithm
	 */
	public void setDigestAlgorithm(final String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	/**
	 * Gets signature packaging.
	 *
	 * @return the signature packaging
	 */
	public String getSignaturePackaging() {
		return signaturePackaging;
	}

	/**
	 * Sets signature packaging.
	 *
	 * @param signaturePackaging the signature packaging
	 */
	public void setSignaturePackaging(final String signaturePackaging) {
		this.signaturePackaging = signaturePackaging;
	}

	/**
	 * Gets sign id.
	 *
	 * @return the sign id
	 */
	public String getSignId() {
		return signId;
	}

	/**
	 * Sets sign id.
	 *
	 * @param signId the sign id
	 */
	public void setSignId(final String signId) {
		this.signId = signId;
	}

	/**
	 * Gets sign value id.
	 *
	 * @return the sign value id
	 */
	public String getSignValueId() {
		return signValueId;
	}

	/**
	 * Sets sign value id.
	 *
	 * @param signValueId the sign value id
	 */
	public void setSignValueId(final String signValueId) {
		this.signValueId = signValueId;
	}

	/**
	 * Gets object id.
	 *
	 * @return the object id
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * Sets object id.
	 *
	 * @param objectId the object id
	 */
	public void setObjectId(final String objectId) {
		this.objectId = objectId;
	}
	
	public String getElementToSign() {
		return elementToSign;
	}

	public void setElementToSign(String elementToSign) {
		this.elementToSign = elementToSign;
	}

	@Override
	public String toString() {
		return "SignatureConf{" + "idSignConf='" + idSignConf + '\'' + ", secret='" + secret + '\'' + ", idProofConf='"
				+ idProofConf + '\'' + ", description='" + description + '\'' + ", certificate='" + certificate + '\''
				+ ", privateKey='" + privateKey + '\'' + ", canonicalisationAlgorithm='" + canonicalisationAlgorithm
				+ '\'' + ", digestAlgorithm='" + digestAlgorithm + '\'' + ", signaturePackaging='" + signaturePackaging
				+ '\'' + ", signId='" + signId + '\'' + ", signValueId='" + signValueId + '\'' + ", objectId='"
				+ objectId + '\'' + ", elementToSign='" + elementToSign + '\'' + '}';
	}

	/**
	 * Check valid boolean.
	 *
	 * @return the boolean
	 * @throws IllegalAccessException the illegal access exception
	 */
	public boolean checkValid() throws IllegalAccessException {
		for (final Field f : getClass().getDeclaredFields()) {
			if (f.getDeclaredAnnotation(Nullable.class) == null) {
				if (!"signParams".equals(f.getName()) && f.get(this) == null) {
					log.error("Missing field {} in object {}", f.getName(), this.getClass().getSimpleName());
					return false;
				}
			}
		}
		signParams = ConfigurationLoader.loadSignConf(this);
		return signParams.getKeyStore() != null;
	}
}
