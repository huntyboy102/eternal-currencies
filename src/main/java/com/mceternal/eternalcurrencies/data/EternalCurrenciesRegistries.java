package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

public class EternalCurrenciesRegistries {

    public static final ResourceKey<Registry<CurrencyData>> KEY_CURRENCIES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("currencies"));

    public static final ResourceKey<Registry<MapCodec<? extends ShopEntry>>> KEY_SHOP_ENTRY_TYPES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("shop_entry_types"));


    public static Supplier<IForgeRegistry<CurrencyData>> CURRENCIES = () -> RegistryManager.ACTIVE.getRegistry(KEY_CURRENCIES);

    public static Supplier<IForgeRegistry<MapCodec<? extends ShopEntry>>> SHOP_ENTRY_TYPES = null;


    public static void addRegistries(NewRegistryEvent event) {
        EternalCurrencies.LOGGER.info("created Shop Entry Type registry!");
        //RegistryBuilder<MapCodec<? extends ShopEntry>> builder = ;
        //builder.setName(KEY_SHOP_ENTRY_TYPES.location());
        SHOP_ENTRY_TYPES = event.create(RegistryBuilder.of(KEY_SHOP_ENTRY_TYPES.location()));
    }

    public static void registerCurrencies(DataPackRegistryEvent.NewRegistry event) {
        EternalCurrencies.LOGGER.info("registered Currencies!");
        event.dataPackRegistry(EternalCurrenciesRegistries.KEY_CURRENCIES, CurrencyData.CODEC, CurrencyData.CODEC);
    }

}
