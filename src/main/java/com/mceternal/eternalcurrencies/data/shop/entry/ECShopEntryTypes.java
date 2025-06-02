package com.mceternal.eternalcurrencies.data.shop.entry;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ECShopEntryTypes {

    public static final DeferredRegister<MapCodec<? extends ShopEntry>> SHOP_ENTRY_TYPES =
            DeferredRegister.create(EternalCurrenciesRegistries.KEY_SHOP_ENTRY_TYPES, EternalCurrencies.MODID);

    public static final RegistryObject<MapCodec<ItemShopEntry>> ITEM =
            SHOP_ENTRY_TYPES.register("item", () -> ItemShopEntry.CODEC);

    public static final RegistryObject<MapCodec<CommandShopEntry>> COMMAND =
            SHOP_ENTRY_TYPES.register("command", () -> CommandShopEntry.CODEC);

    public static final RegistryObject<MapCodec<PotionEffectShopEntry>> POTION_EFFECT =
            SHOP_ENTRY_TYPES.register("potion_effect", () -> PotionEffectShopEntry.CODEC);

    /* TODO:
         SpawnEntityShopEntry
     */

    public static void register(IEventBus bus) {
        SHOP_ENTRY_TYPES.register(bus);
    }

}
