package com.mceternal.eternalcurrencies.data.shop.requirement;

import com.mceternal.eternalcurrencies.api.shop.ShopRequirement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementRequirement extends ShopRequirement<ResourceLocation> {

    public static final MapCodec<AdvancementRequirement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("advancement").forGetter(e -> e.target)
    ).apply(inst, AdvancementRequirement::new));

    public AdvancementRequirement(ResourceLocation target) {
        super(target);
    }

    @Override
    public Codec<ResourceLocation> contentCodec() {
        return ResourceLocation.CODEC;
    }

    @Override
    public MapCodec<? extends ShopRequirement> codec() {
        return CODEC;
    }

    @Override
    public boolean meetsRequirement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(target);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }


}
