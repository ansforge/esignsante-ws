/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The type Security config.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Http CSRF security configuration.
     *
     * @param http HttpSecurity
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	//http.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    	http.csrf((csrf) -> csrf.disable());    	
    	http.authorizeHttpRequests((web) -> web.requestMatchers("/**").permitAll());
    	
        return http.build();
    }

}
