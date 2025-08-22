package com.mceternal.eternalcurrencies.data;

import com.mceternal.eternalcurrencies.api.capability.DirectCurrencyHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CurrencySavedData extends SavedData {

    // Unique identifier for this saved data
    public static final String DATA_ID = "eternal-currencies";

    // Map to hold all DirectCurrencyHolder objects, using UUID as the key
    private final Map<UUID, DirectCurrencyHolder> holders = new HashMap<>();

    /**
     * Saves the current state of the CurrencySavedData to a CompoundTag.
     *
     * @param pCompoundTag The tag to save the data into.
     * @return The same CompoundTag with the saved data.
     */
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        // Iterate over all holders and serialize their data
        holders.keySet().forEach(holderID ->
                pCompoundTag.put(holderID.toString(), holders.get(holderID).serializeNBT()));
        return pCompoundTag;
    }

    /**
     * Loads the CurrencySavedData from a CompoundTag.
     *
     * @param tag The tag containing the saved data.
     * @return A new instance of CurrencySavedData with the loaded data.
     */
    public static CurrencySavedData load(CompoundTag tag) {
        // Create a new instance of CurrencySavedData
        CurrencySavedData data = new CurrencySavedData();
        // Iterate over all keys in the tag
        tag.getAllKeys().forEach(key -> {
            DirectCurrencyHolder holder = new DirectCurrencyHolder();
            UUID holderUUID = UUID.fromString(key);
            Tag holderTag = tag.get(key);
            if (holderTag instanceof CompoundTag holderCompound) {
                // Deserialize the holder data and add it to the holders map
                holder.deserializeNBT(holderCompound);
                data.holders.put(holderUUID, holder);
            }
        });
        return data;
    }

    /**
     * Retrieves or creates a DirectCurrencyHolder for a given UUID.
     *
     * @param id The UUID of the player or address.
     * @return The DirectCurrencyHolder for the specified UUID.
     */
    public DirectCurrencyHolder getOrCreate(UUID id) {
        if (holders.containsKey(id)) {
            // Return existing holder
            return this.get(id);
        } else {
            // Create a new holder and add it to the map
            DirectCurrencyHolder newHolder = new DirectCurrencyHolder();
            this.holders.put(id, newHolder);
            return newHolder;
        }
    }

    /**
     * Retrieves a DirectCurrencyHolder for a given UUID.
     *
     * @param id The UUID of the player or address.
     * @return The DirectCurrencyHolder for the specified UUID, or null if it doesn't exist.
     */
    public DirectCurrencyHolder get(UUID id) {
        return holders.get(id);
    }

    /**
     * Checks if a DirectCurrencyHolder exists for a given UUID.
     *
     * @param id The UUID of the player or address.
     * @return true if the holder exists, false otherwise.
     */
    public boolean doesAddressExist(UUID id) {
        return holders.containsKey(id);
    }

    /**
     * Retrieves a set of all UUIDs that have DirectCurrencyHolders.
     *
     * @return A set containing all the UUIDs.
     */
    public Set<UUID> getAllAddresses() {
        return holders.keySet();
    }

    /**
     * Retrieves or creates an instance of CurrencySavedData from the server's data storage.
     *
     * @param server The MinecraftServer instance.
     * @return An instance of CurrencySavedData.
     */
    public static CurrencySavedData getFromServer(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(CurrencySavedData::load, CurrencySavedData::new, CurrencySavedData.DATA_ID);
    }
}
