package com.mceternal.eternalcurrencies.api.shop;

import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public abstract class ShopRequirement<T> {

    public static final Codec<ShopRequirement> DISPATCH_CODEC =
            ExtraCodecs.lazyInitializedCodec(() -> EternalCurrenciesRegistries.PURCHASE_REQUIREMENT_TYPES.get().getCodec().dispatch(ShopRequirement::codec, MapCodec::codec));

    public final T target;

    public ShopRequirement(T target) {
        this.target = target;
    }

    public abstract Codec<T> contentCodec();

    public abstract MapCodec<? extends ShopRequirement> codec();

    public abstract boolean meetsRequirement(ServerPlayer player);
}
