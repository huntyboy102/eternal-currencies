package com.mceternal.eternalcurrencies.data.shop.entry;

import com.mceternal.eternalcurrencies.api.shop.ShopRequirement;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

public class PotionEffectShopEntry extends ShopEntry<MobEffectInstance> {

    public static final MapCodec<MobEffectInstance> EFFECT_INSTANCE_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.fieldOf("visible").forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.fieldOf("show_icon").forGetter(MobEffectInstance::showIcon)
    ).apply(inst, MobEffectInstance::new));

    public static final MapCodec<PotionEffectShopEntry> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EFFECT_INSTANCE_CODEC.fieldOf("effect").forGetter(e -> e.effect))
            .and(baseShopEntryFields(inst))
            .apply(inst, PotionEffectShopEntry::new));

    public final MobEffectInstance effect;

    public PotionEffectShopEntry(MobEffectInstance effect, String name, Map<ResourceLocation, Long> costs, List<ShopRequirement> requirements) {
        super(name, costs, requirements);
        this.effect = effect;
    }

    @Override
    public Component getDefaultName() {
        return effect.getEffect().getDisplayName();
    }

    @Override
    public Codec<MobEffectInstance> contentCodec() {
        return EFFECT_INSTANCE_CODEC.codec();
    }

    @Override
    public MapCodec<? extends ShopEntry> codec() {
        return CODEC;
    }

    @Override
    public void purchase(ServerPlayer holder) {
        super.purchase(holder);
        holder.addEffect(effect);
    }

    @Override
    public boolean autoPurchasable() {
        return false;
    }
}
