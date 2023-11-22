/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.gouv.esante.api.sign.ws.api.SecretsApiDelegate;
import fr.gouv.esante.api.sign.ws.model.HashedSecret;
import fr.gouv.esante.api.sign.ws.model.Secret;
import fr.gouv.esante.api.sign.ws.util.Secrets;

/**
 * The Class SecretsApiDelegateImpl.
 */
public class SecretsApiDelegateImpl extends ApiDelegate implements SecretsApiDelegate {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(SecretsApiDelegateImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.gouv.esante.api.sign.ws.api.SecretsApiDelegate#generateSecureSecretHash(fr.
     * gouv.esante.api.sign.ws.model.Secret)
     */
    @Override
    public ResponseEntity<HashedSecret> generateSecureSecretHash(final Secret secret) {
        ResponseEntity<HashedSecret> re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!"".equals(secret.getPlainSecret())) {
            final HashedSecret hs = new HashedSecret();
            hs.setSecureSecretHash(Secrets.hash(secret.getPlainSecret()));
            re = new ResponseEntity<>(hs, HttpStatus.OK);
        }

        return re;
    }

}
