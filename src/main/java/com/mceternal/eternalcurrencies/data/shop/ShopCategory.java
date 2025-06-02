package com.mceternal.eternalcurrencies.data.shop;

import com.mceternal.eternalcurrencies.api.shop.ShopRequirement;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ShopCategory {

    public static final Codec<ShopCategory> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(e -> e.icon),
            Codec.list(ShopEntry.DISPATCH_CODEC).fieldOf("entries").forGetter(e -> e.entries),
            Codec.list(ShopRequirement.DISPATCH_CODEC).optionalFieldOf("requirements", List.of()).forGetter(e -> e.requirements)
    ).apply(inst, ShopCategory::new));

    public final ResourceLocation icon;
    private final List<ShopEntry> entries;
    private final List<ShopRequirement> requirements;

    public ShopCategory(ResourceLocation icon, List<ShopEntry> entries, List<ShopRequirement> requirements) {
        this.icon = icon;
        this.entries = entries;
        this.requirements = requirements;
    }

    public List<ShopRequirement> getRequirements() {
        return requirements;
    }

    public List<ShopEntry> getEntries() {
        return entries;
    }

    public Optional<ShopEntry> getEntry(String entryName) {
        return entries.stream().dropWhile(entry -> !Objects.equals(entry.id, entryName)).findFirst();
    }
}
