package com.mceternal.eternalcurrencies.api.shop;

import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract class representing a shop entry.
 * A shop entry defines what can be purchased, its costs, and any requirements to purchase it.
 *
 * @param <T> The type of the shop entry.
 */
public abstract class ShopEntry<T> {

    public static final Codec<ShopEntry> DISPATCH_CODEC =
            ExtraCodecs.lazyInitializedCodec(() -> EternalCurrenciesRegistries.SHOP_ENTRY_TYPES.get().getCodec().dispatch(ShopEntry::codec, MapCodec::codec));

    /**
     * The unique identifier for this shop entry.
     */
    public final String id;

    /**
     * A map of currency IDs and their associated costs for this shop entry.
     */
    public final Map<ResourceLocation, Long> costs;

    /**
     * A list of requirements that must be met to purchase this shop entry.
     */
    public final List<ShopRequirement> requirements;

    /**
     * Constructs a new instance of ShopEntry with the given ID, costs, and requirements.
     *
     * @param id The unique identifier for the shop entry.
     * @param costs A map of currency IDs and their associated costs.
     * @param requirements A list of requirements to purchase this shop entry.
     */
    public ShopEntry(String id, Map<ResourceLocation, Long> costs, List<ShopRequirement> requirements) {
        this.id = id;
        this.costs = costs;
        this.requirements = requirements;
    }

    /**
     * Constructs a new instance of ShopEntry with the given ID, currency, cost, and no requirements.
     *
     * @param id The unique identifier for the shop entry.
     * @param currency The resource location of the currency used to purchase this entry.
     * @param cost The cost of purchasing this entry in the specified currency.
     */
    public ShopEntry(String id, ResourceLocation currency, long cost) {
        this(id, Map.of(currency, cost), List.of());
    }

    /**
     * Constructs a new instance of ShopEntry with the given ID, currency, and cost, along with the specified requirements.
     *
     * @param id The unique identifier for the shop entry.
     * @param currency The resource location of the currency used to purchase this entry.
     * @param cost The cost of purchasing this entry in the specified currency.
     * @param requirements A list of requirements to purchase this shop entry.
     */
    public ShopEntry(String id, ResourceLocation currency, long cost, List<ShopRequirement> requirements) {
        this(id, Map.of(currency, cost), requirements);
    }

    /**
     * Returns a base codec for the fields common to all shop entries.
     *
     * @param inst The record codec builder instance.
     * @param <T> The type of the shop entry.
     * @return A products codec representing the fields of the shop entry.
     */
    public static <T extends ShopEntry> Products.P3<RecordCodecBuilder.Mu<T>, String, Map<ResourceLocation, Long>, List<ShopRequirement>> baseShopEntryFields(RecordCodecBuilder.Instance<T> inst) {
        return inst.group(
                //TODO make a codec that associates Currency IDs (ResourceLocation) to Long.
                Codec.STRING.fieldOf("id").forGetter(e -> e.id),
                Codec.simpleMap(ResourceLocation.CODEC, Codec.LONG,
                        Keyable.forStrings(() ->
                                EternalCurrenciesAPI.getRegisteredCurrencies().keySet().stream().map(ResourceLocation::toString)
                        )).fieldOf("costs").forGetter(e -> e.costs),
                Codec.list(ShopRequirement.DISPATCH_CODEC).optionalFieldOf("requirements", List.of()).forGetter(e -> e.requirements)
        );
    }

    /**
     * Returns the default name for this shop entry.
     *
     * @return The default name of the shop entry.
     */
    public Component getName() {
        return getDefaultName();
    }

    /**
     * Abstract method to get the default name for this shop entry.
     *
     * @return The default name of the shop entry.
     */
    public abstract Component getDefaultName();

    /**
     * Returns the content codec for this shop entry type.
     *
     * @return The content codec for this shop entry type.
     */
    public abstract Codec<T> contentCodec();

    /**
     * Returns the codec for this shop entry type.
     *
     * @return The codec for this shop entry type.
     */
    public abstract MapCodec<? extends ShopEntry> codec();

    /**
     * Determines if this shop entry can be purchased by the given server player.
     *
     * @param holder The server player attempting to purchase this shop entry.
     * @return True if the shop entry can be purchased, false otherwise.
     */
    public boolean canPurchase(ServerPlayer holder) {
        if (!requirements.stream().allMatch(req -> req.meetsRequirement(holder)))
            return false;
        AtomicBoolean pass = new AtomicBoolean(false);
        EternalCurrenciesAPI.getCurrencies(holder).ifPresent(currencies -> {
            if (currencies.hasAtleast(this.costs))
                pass.set(true);
        });
        return pass.get();
    }

    /**
     * Performs the actions to purchase this shop entry for the given server player.
     *
     * @param holder The server player purchasing this shop entry.
     */
    public void purchase(ServerPlayer holder) {
        EternalCurrenciesAPI.getCurrencies(holder).ifPresent(currencies ->
                this.costs.forEach(currencies::take));
    }

    /**
     * Abstract method to determine if this shop entry can be automatically purchased.
     *
     * @return True if this shop entry can be automatically purchased, false otherwise.
     */
    public abstract boolean autoPurchasable();
}
