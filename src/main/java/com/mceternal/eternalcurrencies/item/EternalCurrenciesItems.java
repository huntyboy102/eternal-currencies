package com.mceternal.eternalcurrencies.item;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EternalCurrenciesItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EternalCurrencies.MODID);

    public static final RegistryObject<Item> CHEQUE = ITEMS.register("currency_item",
            () -> new ItemCurrencyCheque(new Item.Properties()));

    public static final RegistryObject<Item> DEBIT_CARD = ITEMS.register("debit_card",
            () -> new ItemDebitCard(new Item.Properties()));


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
