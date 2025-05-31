package com.mceternal.eternalcurrencies;

import com.mceternal.eternalcurrencies.gui.CurrencyShopScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class EternalCurrenciesClient implements Runnable {

    @Override
    public void run() {
        if(EternalCurrencies.FTBQ_LOADED) {
            //QuestsIntegrationClient.setupRewardGuiProviders();
            //QuestsIntegrationClient.setupTaskGuiProviders();
        }
    }
}
