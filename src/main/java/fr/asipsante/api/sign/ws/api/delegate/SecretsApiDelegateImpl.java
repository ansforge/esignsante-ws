/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.asipsante.api.sign.ws.api.SecretsApiDelegate;
import fr.asipsante.api.sign.ws.model.HashedSecret;
import fr.asipsante.api.sign.ws.model.Secret;
import fr.asipsante.api.sign.ws.util.Secrets;

/**
 * The Class SecretsApiDelegateImpl.
 */
@Service
public class SecretsApiDelegateImpl extends ApiDelegate implements SecretsApiDelegate {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(SecretsApiDelegateImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.asipsante.api.sign.ws.api.SecretsApiDelegate#generateSecureSecretHash(fr.
     * asipsante.api.sign.ws.model.Secret)
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
