/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.scheduled;

import fr.gouv.esante.api.sign.config.utils.CaCrlServiceLoader;
import fr.gouv.esante.api.sign.service.ICACRLService;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * The Class RefreshConfigurations.
 * On télécharge les CRL par défaut une fois toutes les 24 heures du moment du lancement de l'appli
 * Si config.crl.scheduling est renseigné, le rechargement suit l'expression cron.
 */
@Component
@ComponentScan("fr.gouv.esante.api.sign")
public class RefreshConfigurations {

    /**
     * The log.
     */
    Logger log = LoggerFactory.getLogger(RefreshConfigurations.class);

    /** The global conf. */
    @Autowired
    private IGlobalConf globalConf;

    /** The cacrl service. */
    @Autowired
    private ICACRLService cacrlService;

    /** The cron configuration. */
    @Value("${config.crl.scheduling:}")
    private String cronConf;

    /**
     * Refresh crl.
     */
    @Scheduled(cron = "${config.crl.scheduling:-}")
    public void refreshCrl() {
        try {
            CaCrlServiceLoader.loadCaCrl(cacrlService, globalConf.getCa());
        } catch (final IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Refresh crl fixed rate.
     * 86400000 ms = 1 day
     */
    @Scheduled(fixedRate = 86400000)
    public void refreshCrlDefault() {
        if (cronConf == null || "".equals(cronConf)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MILLISECOND, 86400000);
            log.info("Téléchargement des CRLs, prochain téléchargement à : {}", calendar.getTime());
            refreshCrl();
        } else {
            log.info("Votre expression cron est : {}", cronConf);
        }
    }
}
