/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config;

import fr.gouv.esante.api.sign.config.provider.IEsignsanteConfigurationsProvider;
import fr.gouv.esante.api.sign.config.provider.impl.EsignsanteConfigurationsJson;
import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * The type App global config.
 */
@Configuration
public class AppGlobalConfig {

    /**
     * Load configuration file.
     *
     * @return the global configuration
     */
    @Bean
    @Lazy
    public IGlobalConf loadConfiguration() {
        final IEsignsanteConfigurationsProvider confProvider = new EsignsanteConfigurationsJson();
        return confProvider.load();
    }

}
