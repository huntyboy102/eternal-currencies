package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EternalCurrenciesRegistries {

    public static final ResourceKey<Registry<CurrencyData>> KEY_CURRENCIES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("currencies"));

    public static final ResourceKey<Registry<MapCodec<? extends ShopEntry>>> KEY_SHOP_ENTRY_TYPES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("shop_entry_types"));


    public static Supplier<IForgeRegistry<CurrencyData>> CURRENCIES = () -> RegistryManager.ACTIVE.getRegistry(KEY_CURRENCIES);

    public static Registry<MapCodec<? extends ShopEntry>> SHOP_ENTRY_TYPES = BuiltInRegistries.REGISTRY.;


    public static void addRegistries(NewRegistryEvent event) {
        EternalCurrencies.LOGGER.info("created Shop Entry Type registry!");

    }

}
