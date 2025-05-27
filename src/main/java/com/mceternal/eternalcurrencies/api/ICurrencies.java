package com.mceternal.eternalcurrencies.api;

import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Map;

@AutoRegisterCapability
public interface ICurrencies {

    /**
     * Get the Balance for the specified Currency
     * @param currency Currency to fetch the Balance of
     * @return Amount in the Balance
     */
    long getCurrency(ResourceLocation currency);

    /**
     * Set the Balance for the specified currency
     * @param currency Currency to set the Balance of
     * @param amount Amount to set Balance to
     */
    void setBalance(ResourceLocation currency, long amount);

    /**
     * Attempts to take the specified Amount from the Balance for the specified Currency, without going below 0.
     * </p> Returns false if the Balance is insufficient, and true if it is sufficient.
     * @param currency Currency to try and take from
     * @param amount Amount to take from the Balance
     * @return If the Balance can be reduced by this much without going below 0
     */
    boolean tryTake(ResourceLocation currency, long amount);

    /**
     * Attempts to take the specified Amount from the Balance for the specified Currency, without going below the Threshold.
     * </p> Returns false if the Balance is insufficient, and true if it is sufficient.
     * @param currency Currency to try and take from the Balance of
     * @param amount Amount to take from the Balance
     * @param threshold Threshold to avoid going below
     * @return If the Balance can be reduced by this much without going below the Threshold
     */
    boolean tryTake(ResourceLocation currency, long amount, long threshold);

    /**
     * Takes the specified Amount from the Balance for the specified Currency.
     * </p> Can drain indefinitely into the negatives.
     * @param currency Currency to take from the Balance of
     * @param amount Amount to take from the Balance
     */
    void take(ResourceLocation currency, long amount);

    /**
     * Adds the specified Amount the Balance for the specified Currency
     * @param currency Currency to add to the Balance of
     * @param amount Amount to add to the Balance
     */
    void add(ResourceLocation currency, long amount);

    /**
     * Test if this object contains atleast the Amount of all Currencies in the provided map.
     * Used in {@link ShopEntry#canPurchase(ServerPlayer) ShopEntry.purchase}.
     * @param otherCurrencies Map of Currencies to compare against.
     * @return If this map has atleast the Amount of all Currencies in the provided map.
     */
    boolean hasAtleast(Map<ResourceLocation, Long> otherCurrencies);

    /**
     * Gets the associated Map used to store Currency
     * @return Map of the object's Balance
     */
    Map<ResourceLocation, Long> getCurrencyMap();

    /**
     * Serializes this Capability
     * @return This object, Serialized.
     */
    CompoundTag serializeNBT();

    /**
     * Deserializes the given serialized data into this object.
     * @param tag data to Deserialize
     */
    void deserializeNBT(CompoundTag tag);

    /**
     * Copies the provided Capability instance to this instance
     * @param cap Another {@link ICurrencies} capability
     */
    void copy(ICurrencies cap);
}
