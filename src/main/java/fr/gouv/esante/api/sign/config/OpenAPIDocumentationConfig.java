/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config;

import java.text.ParseException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.gouv.esante.api.sign.service.impl.utils.Version;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * The Class OpenAPIDocumentationConfig.
 */
@Configuration
public class OpenAPIDocumentationConfig {

	/** Default ESignSante major version. */
	private static final int MAJOR = 2;

	/**
	 * The log.
	 */
	Logger log = LoggerFactory.getLogger(OpenAPIDocumentationConfig.class);

	/** ESignSante Build Properties. */
	@Autowired
	private BuildProperties buildProperties;

	/**
	 * Api info.
	 *
	 * @return the api info
	 */
	@Bean
	public OpenAPI apiInfo() {
		Info info = new Info();
		info.title("ESignSante");
		info.description("API du composant ESignSante. Ce composant dit de \"signature\" mutualise et homogénéise "
				+ "la mise en oeuvre des besoins autour de la signature électronique. Il permet aux partenaires "
				+ "de l'ANS de signer leurs documents ainsi que de vérifier la validité "
				+ "d'une signature ou d'un certificat.");

		Version wsVersion = new Version(MAJOR, 0, 0, 0);
		try {
			// assign the current version of esignsante-webservices
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		info.version(wsVersion.getVersion());

		License license = new License();
		license.setUrl("http://unlicense.org");
		info.license(license);

		Contact contact = new Contact();
		contact.setEmail("esignsante@asipsante.fr");
		info.contact(contact);

		return new OpenAPI().info(info);
	}

	@Bean
	public GroupedOpenApi customApi() {
		return GroupedOpenApi.builder().packagesToScan("fr.gouv.esante.api.sign.ws.api").pathsToExclude("/error.*")
				.build();
	}

	static {
		SpringDocUtils.getConfig().replaceWithClass(org.threeten.bp.LocalDate.class, java.sql.Date.class);
		SpringDocUtils.getConfig().replaceWithClass(org.threeten.bp.OffsetDateTime.class, java.sql.Date.class);
	}
}
