package com.mceternal.eternalcurrencies.data.shop.requirement;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopRequirement;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ECShopRequirementTypes {

    public static final DeferredRegister<MapCodec<? extends ShopRequirement>> SHOP_REQUIREMENT =
            DeferredRegister.create(EternalCurrenciesRegistries.SHOP_REQUIREMENT, EternalCurrencies.MODID);

    public static final RegistryObject<MapCodec<? extends ShopRequirement>> ADVANCEMENT =
            SHOP_REQUIREMENT.register("advancement", () -> AdvancementRequirement.CODEC);

    public static final RegistryObject<MapCodec<? extends ShopRequirement>> FTB_QUEST = EternalCurrencies.FTBQ_LOADED
            ? SHOP_REQUIREMENT.register("ftb_quest", () -> FTBQuestRequirement.CODEC)
            : null;

    /* TODO:
        FTBQuestRequirement - probably works idk, shop doesn't really work atm
     */


    public static void register(IEventBus bus) {
        SHOP_REQUIREMENT.register(bus);
    }
}
