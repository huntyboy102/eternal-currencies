package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EternalCurrenciesRegistries {
    public static final ResourceKey<Registry<CurrencyType>> CURRENCY_TYPE =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(EternalCurrencies.MODID, "currencies"));
}
