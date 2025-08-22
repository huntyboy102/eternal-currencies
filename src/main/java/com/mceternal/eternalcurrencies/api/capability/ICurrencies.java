package com.mceternal.eternalcurrencies.api.capability;

import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Map;

@AutoRegisterCapability
public interface ICurrencies {

    /**
     * The capability instance for ICurrencies.
     */
    Capability<ICurrencies> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * Retrieves the balance for a specified currency.
     *
     * @param currency The currency to fetch the balance of.
     * @return The amount in the balance. Returns 0L if the currency is not found.
     */
    long getCurrency(ResourceLocation currency);

    /**
     * Sets the balance for a specified currency.
     *
     * @param currency The currency to set the balance of.
     * @param amount   The new balance amount.
     */
    void setBalance(ResourceLocation currency, long amount);

    /**
     * Attempts to take a specified amount from the balance for a specified currency without going below 0.
     * <p> Returns false if the balance is insufficient, and true if it is sufficient.
     *
     * @param currency The currency to try and take from.
     * @param amount   The amount to take from the balance.
     * @return True if the balance can be reduced by this much without going below 0, otherwise false.
     */
    default boolean tryTake(ResourceLocation currency, long amount) {
        return tryTake(currency, amount, 0);
    }

    /**
     * Attempts to take a specified amount from the balance for a specified currency without going below a given threshold.
     * <p> Returns false if the balance is insufficient, and true if it is sufficient.
     *
     * @param currency The currency to try and take from.
     * @param amount   The amount to take from the balance.
     * @param threshold The minimum amount that can remain in the balance.
     * @return True if the balance can be reduced by this much without going below the threshold, otherwise false.
     */
    boolean tryTake(ResourceLocation currency, long amount, long threshold);

    /**
     * Takes a specified amount from the balance for a specified currency.
     * <p> Can drain indefinitely into the negatives.
     *
     * @param currency The currency to take from.
     * @param amount   The amount to take from the balance.
     */
    void take(ResourceLocation currency, long amount);

    /**
     * Adds a specified amount to the balance for a specified currency.
     *
     * @param currency The currency to add to.
     * @param amount   The amount to add to the balance.
     */
    void add(ResourceLocation currency, long amount);

    /**
     * Tests if this object contains at least the amount of all currencies in the provided map.
     * Used in {@link ShopEntry#canPurchase(ServerPlayer)} for purchasing items from a shop entry.
     *
     * @param otherCurrencies The map of currencies to compare against.
     * @return True if this map has at least the amount of all currencies in the provided map, otherwise false.
     */
    boolean hasAtleast(Map<ResourceLocation, Long> otherCurrencies);

    /**
     * Gets the associated map used to store currency balances.
     *
     * @return The map containing the object's balance.
     */
    Map<ResourceLocation, Long> getCurrencyMap();

    /**
     * Serializes this capability instance into an NBT tag.
     *
     * @return An NBT tag representing the serialized data of this capability.
     */
    CompoundTag serializeNBT();

    /**
     * Deserializes the given serialized data into this capability instance.
     *
     * @param tag The NBT tag containing the serialized data to deserialize.
     */
    void deserializeNBT(CompoundTag tag);

    /**
     * Copies the provided capability instance to this instance.
     *
     * @param cap Another ICurrencies capability instance.
     */
    void copy(ICurrencies cap);
}
