/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class SwaggerConfiguration.
 */
@Profile(value = {"swagger"})
@PropertySource("classpath:swagger.properties")
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

}
