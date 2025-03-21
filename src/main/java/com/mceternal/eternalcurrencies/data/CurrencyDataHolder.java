package com.mceternal.eternalcurrencies.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface CurrencyDataHolder {

    Map<ResourceLocation, CurrencyData> getAllCurrencies();
}
