package com.mceternal.eternalcurrencies.api.shop;

import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public abstract class ShopRequirement<T> {

    public static final Codec<ShopRequirement> DISPATCH_CODEC =
            ExtraCodecs.lazyInitializedCodec(() -> EternalCurrenciesRegistries.SHOP_REQUIREMENT_TYPES.get().getCodec().dispatch(ShopRequirement::codec, MapCodec::codec));

    /**
     * The target object for this shop requirement.
     */
    public final T target;

    /**
     * Constructs a new instance of ShopRequirement with the given target.
     *
     * @param target The target object for this shop requirement.
     */
    public ShopRequirement(T target) {
        this.target = target;
    }

    /**
     * Returns the content codec for this shop requirement type.
     *
     * @return The content codec for this shop requirement type.
     */
    public abstract Codec<T> contentCodec();

    /**
     * Returns the codec for this shop requirement type.
     *
     * @return The codec for this shop requirement type.
     */
    public abstract MapCodec<? extends ShopRequirement> codec();

    /**
     * Determines if the given server player meets this shop requirement.
     *
     * @param player The server player to check.
     * @return True if the player meets this requirement, false otherwise.
     */
    public abstract boolean meetsRequirement(ServerPlayer player);
}
