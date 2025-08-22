package com.mceternal.eternalcurrencies.gui;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChangeCategoryButton extends Button {

    // The category associated with this button
    public final ResourceLocation category;

    // Constructor for the ChangeCategoryButton
    public ChangeCategoryButton(int index, ResourceLocation category) {
        super(80, 5 + (15 * index), 20, 15, Component.literal(category.toString()), button -> {}, DEFAULT_NARRATION);
        this.category = category;
        // Set the tooltip for the button to display a translatable string
        this.setTooltip(Tooltip.create(Component.translatable("eternalcurrencies.shop.category." + category)));
    }

    // Method called when the button is pressed
    public void onPress() {
        // Get the current screen from Minecraft instance
        Screen screen = Minecraft.getInstance().screen;

        // Log a message to indicate if the current screen is an instance of CurrencyShopScreen
        EternalCurrencies.LOGGER.info("CurrencyShopScreen: {}", screen instanceof CurrencyShopScreen);

        // Check if the current screen is an instance of CurrencyShopScreen
        if(screen instanceof CurrencyShopScreen shopScreen) {
            // If it is, call the changeCategory method on the shopScreen with the associated category
            shopScreen.changeCategory(this.category);
        }
    }
}
