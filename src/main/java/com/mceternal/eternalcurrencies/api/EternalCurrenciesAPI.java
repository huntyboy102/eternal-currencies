package com.mceternal.eternalcurrencies.api;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.data.CurrencyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class EternalCurrenciesAPI {

    /**
     * Gets all registered Currencies.
     * @return List of all currencies in the Currency Registry.
     */
    public static List<CurrencyType> getRegisteredCurrencies() {
        EternalCurrencies.LOGGER.info("EternalCurrenciesAPI#getRegisteredCurrencies() was accessed!");
        return EternalCurrencies.getCurrencyManager().getAllCurrencies();
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
    public static boolean removeBalanceFor(Player player, ResourceLocation currency, long amount) {
        AtomicBoolean success = new AtomicBoolean(false);
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                success.set(currencies.tryTake(currency, amount)));
        return success.get();
    }

    /**
     * Removes the specified amount from the Player's Balance for the specified Currency
     * @param player
     * @param currency
     * @param amount
     * @param threshold
     * @return
     */
    public static boolean removeBalanceWithThreshold(Player player, ResourceLocation currency, long amount, long threshold) {
        return false;
    }

    /**
     * Removes the specified Amount from the Player's Balance for the specified Currency, can go infinitely into the negatives.
     * @param player Player to remove Balance from
     * @param currency Currency to remove from the Player's Balance of
     * @param amount Amount to remove from the Player's Balance
     */
    public static void removeAnywayFor(Player player, ResourceLocation currency, long amount) {
        player.getCapability(CurrenciesCapabilities.PLAYER_CURRENCY).ifPresent(currencies ->
                currencies.take(currency, amount));
    }
}
