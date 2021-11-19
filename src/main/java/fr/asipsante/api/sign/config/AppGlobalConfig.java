/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.config;

import fr.asipsante.api.sign.config.provider.IeSignSanteConfigurationsProvider;
import fr.asipsante.api.sign.config.provider.impl.ESignSanteSanteConfigurationsJson;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
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
        final IeSignSanteConfigurationsProvider confProvider = new ESignSanteSanteConfigurationsJson();
        return confProvider.load();
    }

}
