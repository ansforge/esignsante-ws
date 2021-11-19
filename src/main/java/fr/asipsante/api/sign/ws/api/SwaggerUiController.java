/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * The Class SwaggerUiController.
 */
@Profile("!swagger")
@RestController
public class SwaggerUiController {

    /**
     * Gets the swagger UI.
     *
     * @param httpResponse the http response
     */
    @RequestMapping(value = "swagger-ui.html", method = RequestMethod.GET)
    public void getSwagger(final HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    }
}
