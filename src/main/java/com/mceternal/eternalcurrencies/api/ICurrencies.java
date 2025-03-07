package com.mceternal.eternalcurrencies.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Map;

@AutoRegisterCapability
public interface ICurrencies {

    long getCurrency(ResourceLocation currencyType);

    void setBalance(ResourceLocation currency, long amount);

    boolean tryTake(ResourceLocation currency, long amount);

    void take(ResourceLocation currency, long amount);

    void add(ResourceLocation currencyType, long amount);

    Map<ResourceLocation, Long> getCurrencyMap();

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);

    void copy(ICurrencies cap);
}
