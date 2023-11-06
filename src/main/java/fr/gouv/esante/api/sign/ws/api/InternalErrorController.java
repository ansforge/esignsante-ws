/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Cette classe permet de masquer les erreurs non prévues et de retourner
 * uniquement un code 500.
 */
@Component
public class InternalErrorController extends BasicErrorController {

    /**
     * Constructeur par défaut.
     *
     * @param errorAttributes : Les attributs de la réponse en cas d'erreur.
     */
    public InternalErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }

    /**
     * Traitement des erreurs inatendues en masquant la cause.
     *
     * @param request : la requête à traiter.
     * @return une erreur 500 sans message.
     */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> errorJson(final HttpServletRequest request) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
