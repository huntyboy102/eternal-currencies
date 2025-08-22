package com.mceternal.eternalcurrencies.item;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EternalCurrenciesItems {

    // Deferred register for items, using the mod's ID
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EternalCurrencies.MODID);

    // Registry object for the currency item
    public static final RegistryObject<Item> CHEQUE = ITEMS.register("currency_item",
            () -> new ItemCurrencyCheque(new Item.Properties()));

    // Registry object for the debit card item
    public static final RegistryObject<Item> DEBIT_CARD = ITEMS.register("debit_card",
            () -> new ItemDebitCard(new Item.Properties()));

    // Method to register items with the event bus
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
