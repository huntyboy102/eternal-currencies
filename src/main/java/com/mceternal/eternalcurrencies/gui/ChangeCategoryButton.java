package com.mceternal.eternalcurrencies.gui;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChangeCategoryButton extends Button {

    public final ResourceLocation category;

    public ChangeCategoryButton(int index, ResourceLocation category) {
        super(80, 5 + (15 * index), 20, 15, Component.literal(category.toString()), button -> {}, DEFAULT_NARRATION);
        this.category = category;
        this.setTooltip(Tooltip.create(Component.translatable("eternalcurrencies.shop.category."+ category)));
    }

    public void onPress() {
        Screen screen = Minecraft.getInstance().screen;
        EternalCurrencies.LOGGER.info("CurrencyShopScreen: {}", screen instanceof CurrencyShopScreen);
        if(screen instanceof CurrencyShopScreen shopScreen) {
            shopScreen.changeCategory(this.category);
        }
    }
}
