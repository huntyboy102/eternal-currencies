package com.mceternal.eternalcurrencies.api;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.data.CurrencyData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class EternalCurrenciesAPI {

    /**
     * Gets all registered Currencies.
     * @return Map of all currencies in the Currency Registry.
     */
    public static Map<ResourceLocation, CurrencyData> getRegisteredCurrencies() {
        return EternalCurrencies.getCurrencyHolder().getAllCurrencies();
    }

    public static CurrencyData getCurrencyData(ResourceLocation currency) {
        return EternalCurrencies.getCurrencyHolder().getAllCurrencies().get(currency);
    }

    public static void ifCurrencyRegistered(ResourceLocation currency, Consumer<CurrencyData> dataConsumer) {
        if(getRegisteredCurrencies().containsKey(currency)) {
            dataConsumer.accept(getCurrencyData(currency));
        }
    }

    public static MutableComponent getCurrencyTranslationComponent(ResourceLocation currency) {
        CurrencyData data = getCurrencyData(currency);
        return Component.translatable("currency."+ currency +".name")
                .setStyle(data != null ? Style.EMPTY.withColor(data.textColor()) : Style.EMPTY);
    }

    public static MutableComponent getCurrencyTranslationComponent(ResourceLocation currency, long amount) {
        CurrencyData data = getCurrencyData(currency);
        return Component.translatable("currency."+ currency +".name.with_amount", amount)
                .setStyle(data != null ? Style.EMPTY.withColor(data.textColor()) : Style.EMPTY);
    }

    /**
     * Gets an Object's Currency Capability.
     * </p> Recommended when doing multiple operations
     * @param provider Object to get the Currency Capability of
     * @return Object's Currency Capability, use {@link LazyOptional#ifPresent} to safely execute.
     */
    public static LazyOptional<ICurrencies> getCurrencies(ICapabilityProvider provider) {
        return provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER);
    }

    /**
     * Sets the Object's Balance for the specified Currency to the given Amount
     * @param provider Object to set the Balance of
     * @param currency Currency to target
     * @param amount target Amount to set the Currency to
     */
    public static void setBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(currencies ->
                currencies.setBalance(currency, amount));
    }

    /**
     * Fetch the Object's Balance for the specified Currency.
     * @param provider Object to fetch the Balance of
     * @param currency Currency to fetch the Object's Balance for
     * @return Object's Balance for the Currency
     */
    public static long getBalanceFor(ICapabilityProvider provider, ResourceLocation currency) {
        AtomicLong amount = new AtomicLong(0L);
        provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(currencies -> amount.set(currencies.getCurrency(currency)));

        return amount.get();
    }

    /**
     * Adds the specified Amount to the Object's Balance for the specified Currency
     * @param provider Object to add Balance to
     * @param currency Currency to add to the Object's Balance of
     * @param amount Amount to add to the Object's Balance
     */
    public static void addBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(currencies -> currencies.add(currency, amount));
    }

    /**
     * Removes the specified Amount from the Object's Balance for the specified Currency, but only if it would not cause their Balance to go below 0.
     * @param provider Object to remove Currency from
     * @param currency Currency to remove from the Object's Balance of
     * @param amount Amount to remove from the Object's Balance
     * @return If the Object's Balance could be decreased without going below 0
     */
    public static boolean takeBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        return takeBalanceWithThreshold(provider, currency, amount, 0);
    }

    /**
     * Removes the specified amount from the Object's Balance for the specified Currency, but only if it would not go below the Threshold
     * </p> Can be used to allow "Debt" up to a limit.
     * @param provider Object to remove Currency from
     * @param currency Currency to remove from the Object's Balance of
     * @param amount Amount to remove from the Object's Balance
     * @param threshold Minimum amount
     * @return If the Object's Balance could be decreased without going below the Threshold
     */
    public static boolean takeBalanceWithThreshold(ICapabilityProvider provider, ResourceLocation currency, long amount, long threshold) {
        AtomicBoolean success = new AtomicBoolean(false);
        provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(currencies ->
                success.set(currencies.tryTake(currency, amount, threshold)));
        return success.get();
    }

    /**
     * Removes the specified Amount from the Object's Balance for the specified Currency, can go infinitely into the negatives.
     * @param provider Object to remove Balance from
     * @param currency Currency to remove from the Object's Balance of
     * @param amount Amount to remove from the Object's Balance
     */
    public static void takeAnywayFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(currencies ->
                currencies.take(currency, amount));
    }
}
