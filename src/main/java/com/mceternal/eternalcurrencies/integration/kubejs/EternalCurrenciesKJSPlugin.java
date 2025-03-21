package com.mceternal.eternalcurrencies.integration.kubejs;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class EternalCurrenciesKJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        EternalCurrencies.LOGGER.info("registered KubeJS Bindings for Eternal Currencies!");
        event.add("EternalCurrenciesAPI", EternalCurrenciesAPI.class);
    }
}
