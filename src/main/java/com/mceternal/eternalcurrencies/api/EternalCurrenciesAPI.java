package com.mceternal.eternalcurrencies.api;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.data.CurrencyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class EternalCurrenciesAPI {

    /**
     * Gets all registered Currencies.
     * @return Map of all currencies in the Currency Registry.
     */
    public static Map<ResourceLocation, CurrencyType> getRegisteredCurrencies() {
        return EternalCurrencies.getCurrencyManager().getAllCurrencies();
    }

    /**
     * Gets a Player's Currency Capability.
     * </p> Recommended when doing multiple operations
     * @param player Player to get the Currency Capability of
     * @return Player's Currency Capability, use {@link LazyOptional#ifPresent} to safely execute.
     */
    public static LazyOptional<ICurrencies> getCurrencies(Player player) {
        return player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY);
    }

    /**
     * Sets the Player's Balance for the specified Currency to the given Amount
     * @param player Player to set the Balance of
     * @param currency Currency to target
     * @param amount target Amount to set the Currency to
     */
    public static void setBalanceFor(Player player, ResourceLocation currency, long amount) {
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                currencies.setBalance(currency, amount));
    }

    /**
     * Fetch the Player's Balance for the specified Currency.
     * @param player Player to fetch the Balance of
     * @param currency Currency to fetch the Player's Balance for
     * @return Player's Balance for the Currency
     */
    public static long getBalanceFor(Player player, ResourceLocation currency) {
        AtomicLong amount = new AtomicLong(-1L);
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies -> {
            amount.set(currencies.getCurrency(currency));
        });

        return amount.get();
    }

    /**
     * Adds the specified Amount to the Player's Balance for the specified Currency
     * @param player Player to add Balance to
     * @param currency Currency to add to the Player's Balance of
     * @param amount Amount to add to the Player's Balance
     */
    public static void addBalanceFor(Player player, ResourceLocation currency, long amount) {
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies -> {
            currencies.add(currency, amount);
        });
    }

    /**
     * Removes the specified Amount from the Player's Balance for the specified Currency, but only if it would not cause their Balance to go below 0.
     * @param player Player to remove Currency from
     * @param currency Currency to remove from the Player's Balance of
     * @param amount Amount to remove from the Player's Balance
     * @return If the Player's Balance could be decreased without going below 0
     */
    public static boolean takeBalanceFor(Player player, ResourceLocation currency, long amount) {
        AtomicBoolean success = new AtomicBoolean(false);
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                success.set(currencies.tryTake(currency, amount)));
        return success.get();
    }

    /**
     * Removes the specified amount from the Player's Balance for the specified Currency, but only if it would not go below the Threshold
     * </p> Can be used to allow "Debt" up to a limit.
     * @param player Player to remove Currency from
     * @param currency Currency to remove from the Player's Balance of
     * @param amount Amount to remove from the Player's Balance
     * @param threshold Minimum amount
     * @return If the Player's Balance could be decreased without going below the Threshold
     */
    public static boolean takeBalanceWithThreshold(Player player, ResourceLocation currency, long amount, long threshold) {
        AtomicBoolean success = new AtomicBoolean(false);
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                success.set(currencies.tryTake(currency, amount, threshold)));
        return success.get();
    }

    /**
     * Removes the specified Amount from the Player's Balance for the specified Currency, can go infinitely into the negatives.
     * @param player Player to remove Balance from
     * @param currency Currency to remove from the Player's Balance of
     * @param amount Amount to remove from the Player's Balance
     */
    public static void takeAnywayFor(Player player, ResourceLocation currency, long amount) {
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                currencies.take(currency, amount));
    }
}
