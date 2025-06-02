package com.mceternal.eternalcurrencies.gui;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mceternal.eternalcurrencies.data.shop.ShopCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class CurrencyShopScreen extends Screen {

    public static ResourceLocation lastCategory;
    private ResourceLocation currentCategory = lastCategory != null ? lastCategory : EternalCurrencies.resource("exchange");

    public CurrencyShopScreen() {
        super(Component.translatable("screen.eternalcurrencies.shop.name"));
    }

    @Override
    protected void init() {
        loadEntries();
        addCategoryButtons();
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void changeCategory(ResourceLocation newCategory) {
        EternalCurrencies.LOGGER.info("Changing category for {} from '{}' to '{}'", this, this.currentCategory, newCategory);
        this.currentCategory = newCategory;
        this.rebuildWidgets();
    }

    public void addEntry(ShopEntry<?> entry) {
        ShopEntryButton entryButton = new ShopEntryButton(100, this.children().size(), currentCategory, entry);
        this.addRenderableWidget(entryButton);
    }

    public void loadEntries() {
        Objects.requireNonNull(getShopCategories().get(currentCategory)).getEntries().forEach(entry -> {
            EternalCurrencies.LOGGER.info("Found ShopCategory {}", entry.id);
            this.addEntry(entry);
        });
    }

    public void addCategoryButtons() {
        int index = 1;
        for (ResourceKey<ShopCategory> key : getShopCategories().registryKeySet()) {
            ChangeCategoryButton button = new ChangeCategoryButton(index, key.location());
            this.addRenderableWidget(button);
            index++;
        }
    }

    public Registry<ShopCategory> getShopCategories() {
        return minecraft.level.registryAccess().registry(EternalCurrenciesRegistries.KEY_SHOP_CATEGORIES)
                .orElseThrow(() -> new RuntimeException("Could not get ShopCategory registry in "+ this));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
