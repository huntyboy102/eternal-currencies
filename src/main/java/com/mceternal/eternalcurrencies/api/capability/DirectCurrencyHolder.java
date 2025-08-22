package com.mceternal.eternalcurrencies.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * This implementation of ICurrencies will store balance directly onto the Object, as a {@link Map Map{ResourceLocation, Long}} under the "eternalcurrencies:currencies" Capability.
 */
public class DirectCurrencyHolder implements ICurrencies {

    /**
     * The map to store currency balances. Keys are {@link ResourceLocation} objects representing different currencies and values are the balance amounts.
     */
    private Map<ResourceLocation, Long> balances = new HashMap<>();

    /**
     * Retrieves the current balance for a given currency.
     *
     * @param currency The {@link ResourceLocation} representing the currency to retrieve.
     * @return The balance for the specified currency. Returns 0L if the currency is not found.
     */
    @Override
    public long getCurrency(ResourceLocation currency) {
        return balances.getOrDefault(currency, 0L);
    }

    /**
     * Sets the balance for a given currency.
     *
     * @param currency The {@link ResourceLocation} representing the currency to set.
     * @param amount   The new balance amount for the specified currency.
     */
    @Override
    public void setBalance(ResourceLocation currency, long amount) {
        balances.put(currency, amount);
    }

    /**
     * Tries to take a certain amount of currency from the holder's total balance, given a threshold. If the current balance plus the threshold is greater than or equal to the desired amount, it deducts the specified amount.
     *
     * @param currency The {@link ResourceLocation} representing the currency to take.
     * @param amount   The amount of currency to try to take.
     * @param threshold A threshold value that must be met for the take operation to succeed.
     * @return true if the operation is successful, false otherwise.
     */
    @Override
    public boolean tryTake(ResourceLocation currency, long amount, long threshold) {
        long currentAmount = getCurrency(currency);
        if ((currentAmount + threshold) >= amount) {
            balances.merge(currency, amount, (existingAmount, addedAmount) -> Long.sum(existingAmount, -addedAmount));
            return true;
        }
        return false;
    }

    /**
     * Takes a certain amount of currency from the holder's total balance.
     *
     * @param currency The {@link ResourceLocation} representing the currency to take.
     * @param amount   The amount of currency to take.
     */
    @Override
    public void take(ResourceLocation currency, long amount) {
        balances.merge(currency, amount, (existingAmount, addedAmount) -> Long.sum(existingAmount, -addedAmount));
    }

    /**
     * Adds a certain amount of currency to the holder's total balance.
     *
     * @param currency The {@link ResourceLocation} representing the currency to add.
     * @param amount   The amount of currency to add.
     */
    @Override
    public void add(ResourceLocation currency, long amount) {
        balances.merge(currency, amount, Long::sum);
    }

    /**
     * Checks if the holder has at least the specified amounts of each currency in the provided map.
     *
     * @param otherCurrencies A {@link Map} where keys are {@link ResourceLocation}s representing currencies and values are the required balance amounts.
     * @return true if the holder's balances meet or exceed the required amounts, false otherwise.
     */
    @Override
    public boolean hasAtleast(Map<ResourceLocation, Long> otherCurrencies) {
        return otherCurrencies.entrySet().stream()
                .allMatch(entry ->
                        balances.containsKey(entry.getKey()) && balances.get(entry.getKey()) >= entry.getValue());
    }

    /**
     * Retrieves a copy of the holder's currency map.
     *
     * @return A {@link Map} containing all the currencies and their corresponding balances.
     */
    @Override
    public Map<ResourceLocation, Long> getCurrencyMap() {
        return new HashMap<>(balances);
    }

    /**
     * Serializes the holder's currency data into an NBT tag.
     *
     * @return A {@link CompoundTag} containing the serialized currency data.
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        balances.forEach((id, amount) -> tag.putLong(id.toString(), amount));
        return tag;
    }

    /**
     * Deserializes the holder's currency data from an NBT tag.
     *
     * @param tag The {@link CompoundTag} containing the serialized currency data to deserialize.
     */
    @Override
    public void deserializeNBT(CompoundTag tag) {
        balances.clear();
        tag.getAllKeys().forEach(identifier -> balances.put(new ResourceLocation(identifier), tag.getLong(identifier)));
    }

    /**
     * Copies all the currency data from another ICurrencies instance into this one.
     *
     * @param cap The {@link ICurrencies} instance whose currency data should be copied.
     */
    @Override
    public void copy(ICurrencies cap) {
        balances.clear();
        balances.putAll(cap.getCurrencyMap());
    }
}
