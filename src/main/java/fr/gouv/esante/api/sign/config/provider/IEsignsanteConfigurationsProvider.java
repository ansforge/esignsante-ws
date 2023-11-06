/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config.provider;

import fr.gouv.esante.api.sign.ws.bean.config.IGlobalConf;

/**
 * Interface IeSignSanteConfigurationsProvider.
 */
public interface IEsignsanteConfigurationsProvider {

    /**
     * load global conf.
     *
     * @return IGlobalConf global conf
     */
    IGlobalConf load();

}
