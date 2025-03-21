package com.mceternal.eternalcurrencies.integration.ftbquests;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import dev.ftb.mods.ftblibrary.config.EnumConfig;
import dev.ftb.mods.ftblibrary.config.LongConfig;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.ui.EditStringConfigOverlay;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

//TODO possibly deprecate this class. setting it up as desired may be too complicated.
public class QuestsIntegrationClient {

    public static void setupTaskGuiProviders() {
        QuestsIntegration.Tasks.CURRENCY.setGuiProvider((panel, quest, callback) -> {
            LongConfig config = new LongConfig(1L, Long.MAX_VALUE);
            config.setValue(10L);

            EditStringConfigOverlay<Long> overlay = new EditStringConfigOverlay<>(panel.getGui(), config, accepted -> {
                if(accepted) {
                    CurrencyTask task = new CurrencyTask(0L, quest);
                    callback.accept(task);
                }
                panel.run();
            }, QuestsIntegration.Tasks.CURRENCY.getDisplayName()).atMousePosition();
            overlay.setExtraZlevel(300);
            panel.getGui().pushModalPanel(overlay);
        });
    }

    public static void setupRewardGuiProviders() {
        QuestsIntegration.Rewards.CURRENCY.setGuiProvider((panel, quest, callback) -> {
            LongConfig config = new LongConfig(1L, Long.MAX_VALUE);
            config.setValue(10L);

            EditStringConfigOverlay<Long> overlay = new EditStringConfigOverlay<>(panel.getGui(), config, accepted -> {
                if(accepted)
                    callback.accept(new CurrencyReward(0L, quest, new ResourceLocation(EternalCurrencies.MODID, "coins"), config.getValue()));

                panel.run();
            }, QuestsIntegration.Rewards.CURRENCY.getDisplayName()).atMousePosition();
            panel.getGui().pushModalPanel(overlay);

            List<ResourceLocation> currencies = EternalCurrenciesAPI.getRegisteredCurrencies().keySet().stream().toList();

            EnumConfig<ResourceLocation> enumConfig = new EnumConfig<>(NameMap.of(new ResourceLocation(EternalCurrencies.MODID, "coins"), currencies)
                    .nameKey(value -> "currency."+ value +".name")
                    .icon(value -> Icon.getIcon(EternalCurrenciesAPI.getCurrencyData(value).icon()))
                    .create());
        });
    }
}
