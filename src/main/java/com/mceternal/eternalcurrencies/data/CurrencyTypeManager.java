package com.mceternal.eternalcurrencies.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class CurrencyTypeManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final HolderLookup.Provider registries;
    private Map<ResourceLocation, CurrencyType> currencies = ImmutableMap.of();

    public CurrencyTypeManager(HolderLookup.Provider registries) {
        super(GSON, "currencies");
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonFiles, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, CurrencyType> builder = ImmutableMap.builder();
        //var ops = this.registries != null ? registries : JsonOps.INSTANCE;
        jsonFiles.forEach((location, json) -> {
            EternalCurrencies.LOGGER.info("file location: {}", location.toString());
            CurrencyType.CODEC.parse(JsonOps.INSTANCE, json).result().ifPresentOrElse((currency) -> builder.put(location, currency),
                    //TODO use the File's ResourceLocation as the registry entry's. currently it uses one defined in the file.
                    () -> EternalCurrencies.LOGGER.error("Currency '{}' has errors.", location));
        });

        this.currencies = builder.build();
    }

    public Map<ResourceLocation, CurrencyType> getAllCurrencies() {
        return currencies;
    }
}
