package com.mceternal.eternalcurrencies.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ClientCurrencyHolder implements CurrencyDataHolder {

    private final Map<ResourceLocation, CurrencyData> currencies;

    public ClientCurrencyHolder(Map<ResourceLocation, CurrencyData> currencyData) {
        this.currencies = currencyData;
    }

    @Override
    public Map<ResourceLocation, CurrencyData> getAllCurrencies() {
        return currencies;
    }
}
