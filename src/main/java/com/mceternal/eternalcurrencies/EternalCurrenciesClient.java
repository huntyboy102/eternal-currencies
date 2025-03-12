package com.mceternal.eternalcurrencies;

import com.mceternal.eternalcurrencies.integration.ftbquests.QuestsIntegrationClient;

import static com.mceternal.eternalcurrencies.EternalCurrencies.FTBQ_LOADED;

public class EternalCurrenciesClient implements Runnable {

    @Override
    public void run() {
        EternalCurrencies.LOGGER.info("EternalCurrenciesClient run!");

        if(FTBQ_LOADED) {
            //QuestsIntegrationClient.setupRewardGuiProviders();
            //QuestsIntegrationClient.setupTaskGuiProviders();
        }
    }
}
