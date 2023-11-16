/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.gouv.esante.api.sign.ws.api.DefaultApiDelegate;
import fr.gouv.esante.api.sign.ws.util.WsVars;

/**
 * The Class DefaultApiDelegateImpl.
 */
@Service
public class DefaultApiDelegateImpl extends ApiDelegate implements DefaultApiDelegate {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(DefaultApiDelegateImpl.class);

    /**
     * Gets the operations.
     *
     * @return the operations
     */
    @Override
    public ResponseEntity<List<String>> getOperations() {
        final Optional<String> acceptHeader = getAcceptHeader();
        ResponseEntity<List<String>> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
            final List<String> methods = new ArrayList<>();
            methods.add("/");
            methods.add("/configurations");
            methods.add("/ca");
            methods.add("/signatures/xmldsig");
            methods.add("/signatures/xmldsigwithproof");
            methods.add("/signatures/xadesbaselineb");
            methods.add("/signatures/xadesbaselinebwithproof");
            methods.add("/validation/signatures/xmldsig");
            methods.add("/validation/signatures/xmldsigwithproof");
            methods.add("/validation/signatures/xadesbaselineb");
            methods.add("/validation/signatures/xadesbaselinebwithproof");
            methods.add("/validation/certificats");
            methods.add("/validation/certificatswithproof");
            re = new ResponseEntity<>(methods, HttpStatus.OK);
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default DefaultApi interface,"
                    + " so no example is generated");
        }
        return re;
    }
}
