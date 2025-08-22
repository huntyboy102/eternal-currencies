package com.mceternal.eternalcurrencies.api;

import com.mceternal.eternalcurrencies.api.capability.ICurrencies;
import com.mceternal.eternalcurrencies.data.CurrencyData;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EternalCurrenciesAPI {

    /**
     * Gets all registered Currencies. May return an empty Map if the Currency Registry cannot be accessed.
     *
     * @param registryAccess The RegistryAccess instance to access the Currency Registry.
     * @return Map of all currencies in the Currency Registry.
     */
    public static Map<ResourceLocation, CurrencyData> getRegisteredCurrencies(RegistryAccess registryAccess) {
        Optional<Registry<CurrencyData>> currencyReg = registryAccess.registry(EternalCurrenciesRegistries.KEY_CURRENCIES);
        return currencyReg.map(currencyData -> currencyData
                .asLookup()
                .listElements()
                .collect(Collectors.toMap(
                        entry -> entry.key().location(),
                        Holder.Reference::get
                ))).orElse(Map.of());
    }

    /**
     * Gets all registered Currencies using the static Level variable. May return an empty map if the Currency Registry cannot be accessed.
     *
     * @return Map of all currencies in the Currency Registry.
     */
    @OnlyIn(Dist.CLIENT)
    public static Map<ResourceLocation, CurrencyData> getRegisteredCurrencies() {
        Level level = Minecraft.getInstance().level;
        return level != null
                ? getRegisteredCurrencies(level.registryAccess())
                : Map.of();
    }

    /**
     * Gets a specific currency data based on the provided currency resource location.
     *
     * @param currency The resource location of the currency.
     * @return The currency data if found, or null otherwise.
     */
    @OnlyIn(Dist.CLIENT)
    public static CurrencyData getCurrencyData(ResourceLocation currency) {
        Level level = Minecraft.getInstance().level;
        return level != null
                ? getCurrencyData(currency, level.registryAccess())
                : null;
    }

    /**
     * Gets a specific currency data based on the provided resource location and registry access.
     *
     * @param currency The resource location of the currency.
     * @param registryAccess The RegistryAccess instance to access the Currency Registry.
     * @return The currency data for the specified currency.
     */
    public static CurrencyData getCurrencyData(ResourceLocation currency, RegistryAccess registryAccess) {
        return registryAccess.registry(EternalCurrenciesRegistries.KEY_CURRENCIES)
                .orElseThrow(() -> new RuntimeException("Currency registry could not be fetched while attempting to fetch CurrencyData for '" + currency.toString() + "'."))
                .get(currency);
    }

    /**
     * Gets a translation component for the specified currency.
     *
     * @param currency The resource location of the currency.
     * @return A mutable component with the translation key for the currency.
     */
    public static MutableComponent getCurrencyTranslationComponent(ResourceLocation currency) {
        return Component.translatable("currency." + currency + ".name");
    }

    /**
     * Gets a translation component for the specified currency with the given amount.
     *
     * @param currency The resource location of the currency.
     * @param amount The amount to include in the translation key.
     * @return A mutable component with the translation key and amount for the currency.
     */
    public static MutableComponent getCurrencyTranslationComponent(ResourceLocation currency, long amount) {
        return Component.translatable("currency." + currency + ".name.with_amount", amount);
    }

    /**
     * Gets an Object's Currency Capability.
     *
     * @param provider The object to get the Currency Capability of.
     * @return A LazyOptional containing the object's Currency Capability, use {@link LazyOptional#ifPresent} to safely execute operations.
     */
    public static LazyOptional<ICurrencies> getCurrencies(ICapabilityProvider provider) {
        return provider.getCapability(ICurrencies.CAPABILITY);
    }

    /**
     * Sets the Object's Balance for the specified Currency to the given amount.
     *
     * @param provider The object to set the Balance of.
     * @param currency The currency to target.
     * @param amount The target amount to set the Currency to.
     */
    public static void setBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies ->
                currencies.setBalance(currency, amount));
    }

    /**
     * Fetches the Object's Balance for the specified Currency.
     *
     * @param provider The object to fetch the Balance of.
     * @param currency The currency to fetch the Object's Balance for.
     * @return The Object's Balance for the Currency.
     */
    public static long getBalanceFor(ICapabilityProvider provider, ResourceLocation currency) {
        AtomicLong amount = new AtomicLong(0L);
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies -> amount.set(currencies.getCurrency(currency)));

        return amount.get();
    }

    /**
     * Adds the specified Amount to the Object's Balance for the specified Currency.
     *
     * @param provider The object to add Balance to.
     * @param currency The currency to add to the Object's Balance of.
     * @param amount The amount to add to the Object's Balance.
     */
    public static void addBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies -> currencies.add(currency, amount));
    }

    /**
     * Removes the specified Amount from the Object's Balance for the specified Currency, but only if it would not cause their Balance to go below 0.
     *
     * @param provider The object to remove Currency from.
     * @param currency The currency to remove from the Object's Balance of.
     * @param amount The amount to remove from the Object's Balance.
     * @return If the Object's Balance could be decreased without going below 0.
     */
    public static boolean takeBalanceFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        return takeBalanceWithThreshold(provider, currency, amount, 0);
    }

    /**
     * Removes the specified Amount from the Object's Balance for the specified Currency, but only if it would not go below the Threshold.
     *
     * @param provider The object to remove Currency from.
     * @param currency The currency to remove from the Object's Balance of.
     * @param amount The amount to remove from the Object's Balance.
     * @param threshold The minimum amount allowed.
     * @return If the Object's Balance could be decreased without going below the Threshold.
     */
    public static boolean takeBalanceWithThreshold(ICapabilityProvider provider, ResourceLocation currency, long amount, long threshold) {
        AtomicBoolean success = new AtomicBoolean(false);
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies ->
                success.set(currencies.tryTake(currency, amount, threshold)));
        return success.get();
    }

    /**
     * Removes the specified Amount from the Object's Balance for the specified Currency, can go infinitely into the negatives.
     *
     * @param provider The object to remove Balance from.
     * @param currency The currency to remove from the Object's Balance of.
     * @param amount The amount to remove from the Object's Balance.
     */
    public static void takeAnywayFor(ICapabilityProvider provider, ResourceLocation currency, long amount) {
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies ->
                currencies.take(currency, amount));
    }
}
