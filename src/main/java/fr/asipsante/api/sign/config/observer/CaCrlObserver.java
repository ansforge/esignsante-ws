/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.config.observer;

import fr.asipsante.api.sign.config.utils.CaCrlServiceLoader;
import fr.asipsante.api.sign.service.ICACRLService;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * The Class CaCrlObserver.
 */
@Component
public class CaCrlObserver {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(CaCrlObserver.class);

    /** The GlobalConf object. */
    @Autowired
    private IGlobalConf globalConf;

    /** The service ca crl. */
    @Autowired
    private ICACRLService serviceCaCrl;

    /**
     * Add observer.
     */
    @PostConstruct
    public void addObserver() {
        // PostConstruct bean to be able to return void
        // declare this object as an IGlobalConf observer
        globalConf.addObserver(this);
    }

    /**
     * update.
     */
    public void update() {
        try {
            // reload CAs and CRLs on detected change in GlobalConf.
            CaCrlServiceLoader.loadCaCrl(serviceCaCrl, globalConf.getCa());
        } catch (final IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
