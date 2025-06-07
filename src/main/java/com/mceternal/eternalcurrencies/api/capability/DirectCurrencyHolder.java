package com.mceternal.eternalcurrencies.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * This implementation of ICurrencies will store balance directly onto the Object, as a {@link Map Map{ResourceLocation, Long}} under the "eternalcurrencies:currencies" Capability.
 */
public class DirectCurrencyHolder implements ICurrencies {

    Map<ResourceLocation, Long> balances = new HashMap<>();

    @Override
    public long getCurrency(ResourceLocation currency) {
        return balances.getOrDefault(currency, 0L);
    }

    @Override
    public void setBalance(ResourceLocation currency, long amount) {
        balances.put(currency, amount);
    }

    @Override
    public boolean tryTake(ResourceLocation currency, long amount, long threshold) {
        long currentAmount = getCurrency(currency);
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
    public boolean hasAtleast(Map<ResourceLocation, Long> otherCurrencies) {
        return otherCurrencies.entrySet().stream()
                .allMatch(entry ->
                        balances.containsKey(entry.getKey()) && balances.get(entry.getKey()) >= entry.getValue());
    }

    @Override
    public Map<ResourceLocation, Long> getCurrencyMap() {
        return balances;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        balances.forEach((id, amount) -> tag.putLong(id.toString(), amount));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        tag.getAllKeys().forEach(identifier -> balances.put(new ResourceLocation(identifier), tag.getLong(identifier)));
    }

    @Override
    public void copy(ICurrencies cap) {
        balances = cap.getCurrencyMap();
    }
}
