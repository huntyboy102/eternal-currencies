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

public class CurrencyDataManager extends SimpleJsonResourceReloadListener implements CurrencyDataHolder {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final HolderLookup.Provider registries;
    private Map<ResourceLocation, CurrencyData> currencies = ImmutableMap.of();

    public CurrencyDataManager(HolderLookup.Provider registries) {
        super(GSON, "currencies");
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonFiles, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, CurrencyData> builder = ImmutableMap.builder();
        //var ops = this.registries != null ? registries : JsonOps.INSTANCE;
        jsonFiles.forEach((location, json) -> {
            EternalCurrencies.LOGGER.info("file location: {}", location.toString());
            CurrencyData.CODEC.parse(JsonOps.INSTANCE, json).result().ifPresentOrElse((currency) -> {
                if(currency.enabled())
                    builder.put(location, currency);
                },
                    () -> EternalCurrencies.LOGGER.error("Error parsing CurrencyData file '{}'.", location));
        });

        this.currencies = builder.build();
    }

    public Map<ResourceLocation, CurrencyData> getAllCurrencies() {
        return currencies;
    }
}
