package com.mceternal.eternalcurrencies.api.capability;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.data.CurrencySavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This implementation of ICurrencies stores a UUID to reference a {@link DirectCurrencyHolder} within {@link CurrencySavedData}, which is accessed as if it were the balance of the Capability's Object.
 * <p> The reason for this is that it allows multiple Objects to share the same balance, which is stored externally.
 */
public class ReferenceCurrencyHolder implements ICurrencies {

    public static final String NBTKEY_HOLDER_REFERENCE = "reference_uuid";

    /**
     * Constructs a new instance of ReferenceCurrencyHolder with the given reference UUID and server supplier.
     *
     * @param referenceUUID The UUID that references the DirectCurrencyHolder within CurrencySavedData.
     * @param serverSupplier A supplier to retrieve the MinecraftServer instance.
     */
    public ReferenceCurrencyHolder(UUID referenceUUID, Supplier<MinecraftServer> serverSupplier) {
        this.referenceUUID = referenceUUID;
        this.serverSupplier = serverSupplier;
    }

    protected UUID referenceUUID; // The UUID referencing a DirectCurrencyHolder in CurrencySavedData
    private final Supplier<MinecraftServer> serverSupplier; // A supplier to retrieve the MinecraftServer instance

    @Override
    public long getCurrency(ResourceLocation currency) {
        return applyAndSave(c -> c.getCurrency(currency));
    }

    @Override
    public void setBalance(ResourceLocation currency, long amount) {
        acceptAndSave(c -> c.setBalance(currency, amount));
    }

    @Override
    public boolean tryTake(ResourceLocation currency, long amount, long threshold) {
        return applyAndSave(c -> c.tryTake(currency, amount, threshold));
    }

    @Override
    public void take(ResourceLocation currency, long amount) {
        acceptAndSave(c -> c.take(currency, amount));
    }

    @Override
    public void add(ResourceLocation currency, long amount) {
        acceptAndSave(c -> c.add(currency, amount));
    }

    @Override
    public boolean hasAtleast(Map<ResourceLocation, Long> otherCurrencies) {
        return applyAndSave(currencies -> currencies.hasAtleast(otherCurrencies));
    }

    @Override
    public Map<ResourceLocation, Long> getCurrencyMap() {
        return applyAndSave(ICurrencies::getCurrencyMap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID(NBTKEY_HOLDER_REFERENCE, this.referenceUUID);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        try {
            this.referenceUUID = tag.getUUID(NBTKEY_HOLDER_REFERENCE);
        } catch (NullPointerException e) {
            EternalCurrencies.LOGGER.info("Caught NullPointerException loading ReferenceCurrencyHolder capability for a player, repairing with a new instance.");
            CurrencySavedData saved = getCurrencies();
            DirectCurrencyHolder savedHolder = saved.getOrCreate(this.referenceUUID);
            savedHolder.deserializeNBT(tag);
            saved.setDirty();
        }
    }

    @Override
    public void copy(ICurrencies cap) {
        if (cap instanceof ReferenceCurrencyHolder otherReferenceHolder)
            this.updateReference(otherReferenceHolder.referenceUUID);
    }

    /**
     * Applies a consumer to the DirectCurrencyHolder referenced by referenceUUID and saves any changes.
     *
     * @param toRun The Consumer function that performs operations on the DirectCurrencyHolder.
     */
    public void acceptAndSave(Consumer<ICurrencies> toRun) {
        CurrencySavedData saved = getCurrencies();
        toRun.accept(saved.getOrCreate(referenceUUID));
        saved.setDirty();
    }

    /**
     * Applies a function to the DirectCurrencyHolder referenced by referenceUUID, saves any changes, and returns the result.
     *
     * @param toRun The Function that performs operations on the DirectCurrencyHolder and returns a value.
     * @param <T>   The return type of the function.
     * @return The result of applying the function to the DirectCurrencyHolder.
     */
    public <T> T applyAndSave(Function<ICurrencies, T> toRun) {
        CurrencySavedData saved = getCurrencies();
        T t = toRun.apply(saved.getOrCreate(referenceUUID));
        saved.setDirty();
        return t;
    }

    /**
     * Retrieves the CurrencySavedData instance from the server's overworld storage.
     *
     * @return The CurrencySavedData instance.
     */
    public CurrencySavedData getCurrencies() {
        try {
            return serverSupplier.get().overworld().getDataStorage().computeIfAbsent(CurrencySavedData::load, CurrencySavedData::new, "eternal-currencies");
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed getting CurrencySavedData.", e);
        }
    }

    /**
     * Updates the reference to a new UUID.
     *
     * @param holderReference The new reference UUID.
     */
    public void updateReference(UUID holderReference) {
        this.referenceUUID = holderReference;
    }

    /**
     * Retrieves the current reference UUID.
     *
     * @return The reference UUID.
     */
    public UUID getReference() {
        return this.referenceUUID;
    }
}
