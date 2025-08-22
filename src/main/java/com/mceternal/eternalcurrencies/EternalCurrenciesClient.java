package com.mceternal.eternalcurrencies;

import com.mceternal.eternalcurrencies.gui.CurrencyShopScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class EternalCurrenciesClient {

    // Constant for the key mapping name
    public static final String SHOP_KEY_NAME = "key." + EternalCurrencies.MODID + ".open_shop";

    // Key mapping for opening the shop screen
    public static final KeyMapping KEY_OPEN_SHOP = new KeyMapping(
            SHOP_KEY_NAME,  // Name of the key mapping
            GLFW.GLFW_KEY_APOSTROPHE,  // The specific key (apostrophe)
            "keys." + EternalCurrencies.MODID + ".category"  // Category for the key in translation files
    );

    // Constructor for EternalCurrenciesClient class
    public EternalCurrenciesClient() {
        // Initialize client-side integration with FTB Quests if loaded
        if (EternalCurrencies.FTBQ_LOADED) {
            // QuestsIntegrationClient.setupRewardGuiProviders();
            // QuestsIntegrationClient.setupTaskGuiProviders();
        }

        // Register this class to listen for events
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Method to open the shop screen
    public static void openShopScreen() {
        CurrencyShopScreen shopScreen = new CurrencyShopScreen();
        Minecraft.getInstance().setScreen(shopScreen);  // Set the current screen to the shop screen
    }

    // Event handler for client tick events
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.ClientTickEvent.Phase.END && KEY_OPEN_SHOP.consumeClick() && Minecraft.getInstance().isWindowActive()) {
            openShopScreen();  // Open the shop screen when the key is pressed and the window is active
        }
    }
}
