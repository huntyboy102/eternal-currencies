package com.mceternal.eternalcurrencies.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record CurrencyData(ResourceLocation icon) {

    //TODO add data-driven configurability for currency loss on death
    public static final Codec<CurrencyData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(CurrencyData::icon)
    ).apply(inst, CurrencyData::new));
}
