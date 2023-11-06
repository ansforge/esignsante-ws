/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.ws.api.delegate;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.gouv.esante.api.sign.ws.api.ConfigurationsApiDelegate;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import fr.gouv.esante.api.sign.ws.model.Conf;
import fr.gouv.esante.api.sign.ws.util.WsVars;

/**
 * The Class ConfigurationsApiDelegateImpl.
 */
@Service
public class ConfigurationsApiDelegateImpl extends ApiDelegate implements ConfigurationsApiDelegate {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(ConfigurationsApiDelegateImpl.class);

    /** The global configurations. */
    @Autowired
    private IGlobalConf globalConf;

    /**
     * Gets the configurations.
     *
     * @return the configurations
     */
    @Override
    public ResponseEntity<Conf> getConfigurations() {
        final Optional<String> acceptHeader = getAcceptHeader();
        ResponseEntity<Conf> re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
            try {
                re = new ResponseEntity<>(globalConf.mapConfigs().getConfigs(), HttpStatus.OK);
            } catch (final CertificateException | IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Le header Accept:application/json est absent.");
        }
        return re;
    }
}
