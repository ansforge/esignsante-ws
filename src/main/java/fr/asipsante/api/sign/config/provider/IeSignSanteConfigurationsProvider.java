/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.config.provider;

import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;

/**
 * Interface IeSignSanteConfigurationsProvider.
 */
public interface IeSignSanteConfigurationsProvider {

    /**
     * load global conf.
     *
     * @return IGlobalConf global conf
     */
    IGlobalConf load();

}
