package com.mceternal.eternalcurrencies.capability;

import com.mceternal.eternalcurrencies.api.ICurrencies;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PlayerCurrencyCapability implements ICurrencies {

    Map<ResourceLocation, Long> balances = new HashMap<>();

    @Override
    public long getCurrency(ResourceLocation currency) {
        return balances.get(currency);
    }

    @Override
    public void setBalance(ResourceLocation currency, long amount) {
        balances.put(currency, amount);
    }

    @Override
    public boolean tryTake(ResourceLocation currency, long amount) {
        return tryTake(currency, amount, 0);
    }

    @Override
    public boolean tryTake(ResourceLocation currency, long amount, long threshold) {
        long currentAmount = balances.get(currency);
        if((currentAmount + threshold) >= amount) {
            balances.merge(currency, amount, (existingAmount, addedAmount) -> Long.sum(existingAmount, -addedAmount));
            return true;
        }
        return false;
    }

    @Override
    public void take(ResourceLocation currency, long amount) {
        balances.merge(currency, amount, (existingAmount, addedAmount) -> Long.sum(existingAmount, -addedAmount));
    }

    @Override
    public void add(ResourceLocation currency, long amount) {
        balances.merge(currency, amount, Long::sum);
    }

    @Override
    public Map<ResourceLocation, Long> getCurrencyMap() {
        return balances;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        balances.forEach((id, amount) -> tag.put(id.toString(), LongTag.valueOf(amount)));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        tag.getAllKeys().forEach(identifier -> balances.put(ResourceLocation.parse(identifier), tag.getLong(identifier)));
    }

    @Override
    public void copy(ICurrencies cap) {
        balances = cap.getCurrencyMap();
    }
}
