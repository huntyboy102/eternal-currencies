package com.mceternal.eternalcurrencies.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CurrencyData(ResourceLocation icon, boolean enabled, long defaultAmount, ChatFormatting textColor) {

    public CurrencyData(ResourceLocation icon, ChatFormatting textColor) {
        this(icon, true, 10L, textColor);
    }

    public CurrencyData(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readEnum(ChatFormatting.class));
    }

    //TODO add data-driven configurability for currency loss on death
    public static final Codec<CurrencyData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(CurrencyData::icon),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(CurrencyData::enabled),
            Codec.LONG.optionalFieldOf("defaultAmount", 10L).forGetter(CurrencyData::defaultAmount),
            ChatFormatting.CODEC.optionalFieldOf("textColor", ChatFormatting.WHITE).forGetter(CurrencyData::textColor)
    ).apply(inst, CurrencyData::new));

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeResourceLocation(icon);
        buf.writeEnum(textColor);
    }
}
