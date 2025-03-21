package com.mceternal.eternalcurrencies.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CurrencyData(ResourceLocation icon, boolean enabled) {

    public CurrencyData(ResourceLocation icon) {
        this(icon, true);
    }

    public CurrencyData(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    //TODO add data-driven configurability for currency loss on death
    public static final Codec<CurrencyData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(CurrencyData::icon),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(CurrencyData::enabled)
    ).apply(inst, CurrencyData::new));

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeResourceLocation(icon);
    }
}
