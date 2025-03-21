package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EternalCurrenciesRegistries {
    public static final ResourceKey<Registry<CurrencyData>> CURRENCY_DATA =
            ResourceKey.createRegistryKey(new ResourceLocation(EternalCurrencies.MODID, "currencies"));
}
