package com.mceternal.eternalcurrencies.gui;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mceternal.eternalcurrencies.data.shop.ShopCategory;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class CurrencyShopScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("eternalcurrencies", "textures/shop/background.png");
    private final int backgroundWidth = 860;
    private final int backgroundHeight = 875;

    public static ResourceLocation lastCategory;
    private ResourceLocation currentCategory = lastCategory != null ? lastCategory : EternalCurrencies.resource("exchange");

    public CurrencyShopScreen() {
        super(Component.literal("Currency Shop"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - backgroundWidth) / 2;
        int centerY = (this.height - backgroundHeight) / 2;
        //this.addRenderableWidget(new ShopEntryButton(centerX + 10, 0, null, null));

        loadEntries();
        addCategoryButtons();
    }

    @Override
    public Component getTitle() {
        return super.getTitle();
    }

    //TODO: Fix scaling
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        // Determine max width/height to fit nicely
        int maxWidth = (int)(this.width * 0.8);
        int maxHeight = (int)(this.height * 0.8);

        // Keep aspect ratio
        float aspectRatio = (float) backgroundWidth / backgroundHeight;

        int drawWidth = maxWidth;
        int drawHeight = (int)(drawWidth / aspectRatio);

        if(drawHeight > maxHeight) {
            drawHeight = maxHeight;
            drawWidth = (int)(drawHeight * aspectRatio);
        }

        int x = (this.width - drawWidth) / 2;
        int y = (this.height - drawHeight) / 2;

        pGuiGraphics.blit(
                BACKGROUND_TEXTURE,
                x, y,
                0, 0,
                drawWidth, drawHeight,
                drawWidth, drawHeight
        );

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
