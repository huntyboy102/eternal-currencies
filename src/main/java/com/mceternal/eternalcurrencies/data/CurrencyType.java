package com.mceternal.eternalcurrencies.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record CurrencyType(ResourceLocation icon) {

    public static final Codec<CurrencyType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(CurrencyType::icon)
    ).apply(inst, CurrencyType::new));
}
