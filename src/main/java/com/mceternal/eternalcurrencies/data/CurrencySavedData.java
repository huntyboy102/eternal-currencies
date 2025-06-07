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

    public static final String DATA_ID = "eternal-currencies";

    //private static final Map<UUID, DirectCurrencyHolder> holders = new HashMap<>();

    private final Map<UUID, DirectCurrencyHolder> holders = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        holders.keySet().forEach(holderID ->
                pCompoundTag.put(holderID.toString(), holders.get(holderID).serializeNBT()));
        return pCompoundTag;
    }

    public static CurrencySavedData load(CompoundTag tag) {
        CurrencySavedData data = new CurrencySavedData();
        tag.getAllKeys().forEach(key -> {
            DirectCurrencyHolder holder = new DirectCurrencyHolder();
            UUID holderUUID = UUID.fromString(key);
            Tag holderTag = tag.get(key);
            if(holderTag instanceof CompoundTag holderCompound) {
                holder.deserializeNBT(holderCompound);
                data.holders.put(holderUUID, holder);
            }
        });
        return data;
    }

    public DirectCurrencyHolder getOrCreate(UUID id) {
        if(holders.containsKey(id)) {
            return this.get(id);
        } else {
            DirectCurrencyHolder newHolder = new DirectCurrencyHolder();
            this.holders.put(id, newHolder);
            return newHolder;
        }
    }

    public DirectCurrencyHolder get(UUID id) {
        return holders.get(id);
    }

    public boolean doesAddressExist(UUID id) {
        return holders.containsKey(id);
    }

    public Set<UUID> getAllAddresses() {
        return holders.keySet();
    }

    public static CurrencySavedData getFromServer(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(CurrencySavedData::load, CurrencySavedData::new, CurrencySavedData.DATA_ID);
    }
    /*
        {
            UUID: {
                "eternalcurrencies:coins": 100,
                "eternalcurrencies:eternabux": 10
            },
            UUID: {
                "eternalcurrencies:coins": 10000,
                "eternalcurrencies:eternabux": 400
            }
        }
     */
}
