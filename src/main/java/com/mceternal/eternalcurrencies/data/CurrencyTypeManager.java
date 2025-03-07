package com.mceternal.eternalcurrencies.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public class CurrencyTypeManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final HolderLookup.Provider registries;
    private List<CurrencyType> currencies = ImmutableList.of();

    public CurrencyTypeManager(HolderLookup.Provider registries) {
        super(GSON, "currencies");
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonFiles, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableList.Builder<CurrencyType> builder = ImmutableList.builder();
        //var ops = this.registries != null ? registries : JsonOps.INSTANCE;
        jsonFiles.forEach((location, json) -> {
            CurrencyType.CODEC.parse(JsonOps.INSTANCE, json).result().ifPresent(builder::add); //TODO use the File's ResourceLocation as the registry entry's. currently it uses one defined in the file.
        });

        this.currencies = builder.build();
    }

    public List<CurrencyType> getAllCurrencies() {
        return currencies;
    }
}
