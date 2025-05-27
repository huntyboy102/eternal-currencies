package com.mceternal.eternalcurrencies.integration.jei;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.item.EternalCurrenciesItems;
import com.mceternal.eternalcurrencies.item.ItemCurrencyCheque;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return EternalCurrencies.resource("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EternalCurrenciesItems.CHEQUE.get(), (stack, context) -> {
            String identifier = "";
            if(stack.hasTag() && ItemCurrencyCheque.validateCurrencyRoot(stack.getTag())) {
                ListTag currenciesTag = stack.getTag().getList(ItemCurrencyCheque.KEY_CURRENCY_TAG, 10);
                boolean first = true;
                for (Tag tag : currenciesTag) {
                    if(tag instanceof CompoundTag entryTag
                            && ItemCurrencyCheque.validateCurrencyTag(entryTag)) {
                        String currencyId = entryTag.getString(ItemCurrencyCheque.KEY_CURRENCY_TYPE).replace(":", ".");
                        identifier = identifier.concat(first ? currencyId : "_"+ currencyId);

                        if(first) first = false;
                    }
                }
                return identifier;
            }

            return identifier;
        });
    }
}
