package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopRequirement;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mceternal.eternalcurrencies.data.shop.ShopCategory;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

public class EternalCurrenciesRegistries {

    public static final ResourceKey<Registry<CurrencyData>> KEY_CURRENCIES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("currencies"));

    public static final ResourceKey<Registry<ShopCategory>> KEY_SHOP_CATEGORIES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("shop_categories"));

    public static final ResourceKey<Registry<MapCodec<? extends ShopEntry>>> KEY_SHOP_ENTRY_TYPES =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("shop_entry_types"));

    public static final ResourceKey<Registry<MapCodec<? extends ShopRequirement>>> SHOP_REQUIREMENT =
            ResourceKey.createRegistryKey(EternalCurrencies.resource("purchase_requirement_types"));


    public static Supplier<IForgeRegistry<CurrencyData>> CURRENCIES = () -> RegistryManager.ACTIVE.getRegistry(KEY_CURRENCIES);

    public static Supplier<IForgeRegistry<ShopCategory>> SHOP_CATEGORIES = () -> RegistryManager.ACTIVE.getRegistry(KEY_SHOP_CATEGORIES);

    public static Supplier<IForgeRegistry<MapCodec<? extends ShopEntry>>> SHOP_ENTRY_TYPES = null;

    public static Supplier<IForgeRegistry<MapCodec<? extends ShopRequirement>>> PURCHASE_REQUIREMENT_TYPES = null;


    public static void addRegistries(NewRegistryEvent event) {
        EternalCurrencies.LOGGER.info("created Shop Entry Type registry!");
        SHOP_ENTRY_TYPES = event.create(RegistryBuilder.of(KEY_SHOP_ENTRY_TYPES.location()));
        PURCHASE_REQUIREMENT_TYPES = event.create(RegistryBuilder.of(SHOP_REQUIREMENT.location()));
    }

    public static void registerCurrencies(DataPackRegistryEvent.NewRegistry event) {
        EternalCurrencies.LOGGER.info("registered Currencies!");
        event.dataPackRegistry(KEY_CURRENCIES, CurrencyData.CODEC, CurrencyData.CODEC);
        event.dataPackRegistry(KEY_SHOP_CATEGORIES, ShopCategory.CODEC, ShopCategory.CODEC);
    }

}
