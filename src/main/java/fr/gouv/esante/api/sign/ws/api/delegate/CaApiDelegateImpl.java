/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.delegate;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.gouv.esante.api.sign.service.ICACRLService;
import fr.gouv.esante.api.sign.ws.api.CaApiDelegate;
import fr.gouv.esante.api.sign.ws.util.WsVars;

/**
 * The Class CaApiDelegateImpl.
 */
@Service
@Primary
public class CaApiDelegateImpl extends ApiDelegate implements CaApiDelegate {

    /** The cacrl service. */
    @Autowired
    private ICACRLService cacrlService;

    /**
     * Gets the ca.
     *
     * @return the ca
     */
    @Override
    public ResponseEntity<List<String>> getCA() {
        final Optional<String> acceptHeader = getAcceptHeader();
        ResponseEntity<List<String>> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
            re = new ResponseEntity<>(cacrlService.getCa(), HttpStatus.OK);
        }
        return re;
    }
}
