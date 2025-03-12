package com.mceternal.eternalcurrencies.integration.ftbquests;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;

public class QuestsIntegration {

    public static final Icon COINS_ICON = Icon.getIcon(ResourceLocation.fromNamespaceAndPath(EternalCurrencies.MODID, "textures/currencies/coins.png"));

    public static void init() {
        Rewards.init();
        Tasks.init();
    }

    public static class Rewards {
        public static RewardType CURRENCY;

        public static void init() {
            CURRENCY = RewardTypes.register(ResourceLocation.fromNamespaceAndPath(EternalCurrencies.MODID, "currency"), CurrencyReward::new,
                    () -> COINS_ICON);
        }
    }

    public static class Tasks {
        public static TaskType CURRENCY;

        public static void init() {
            CURRENCY = TaskTypes.register(ResourceLocation.fromNamespaceAndPath(EternalCurrencies.MODID, "currency"), CurrencyTask::new,
                    () -> COINS_ICON);
        }
    }
}
