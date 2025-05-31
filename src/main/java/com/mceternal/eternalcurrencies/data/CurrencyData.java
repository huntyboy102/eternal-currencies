package com.mceternal.eternalcurrencies.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public record CurrencyData(ResourceLocation icon, ChatFormatting textColor, boolean transferable) {

    //TODO add data-driven configurability for currency loss on death
    public static final Codec<CurrencyData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(CurrencyData::icon),
            ChatFormatting.CODEC.optionalFieldOf("textColor", ChatFormatting.WHITE).forGetter(CurrencyData::textColor),
            Codec.BOOL.optionalFieldOf("transferable", true).forGetter(CurrencyData::transferable)
    ).apply(inst, CurrencyData::new));
}
