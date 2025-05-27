package com.mceternal.eternalcurrencies.integration.ftbquests;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class CurrencyReward extends Reward {
    private ResourceLocation currency;
    private long amount;

    public CurrencyReward(long id, Quest q) {
        this(id, q, EternalCurrencies.CURRENCY_COINS, 10);
    }

    public CurrencyReward(long id, Quest q, ResourceLocation currencyName, long amount) {
        super(id, q);
        this.currency = currencyName;
        this.amount = amount;
    }

    public ResourceLocation getCurrency() {
        return currency;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public RewardType getType() {
        return QuestsIntegration.Rewards.CURRENCY;
    }

    @Override
    public void claim(ServerPlayer serverPlayer, boolean notify) {
        EternalCurrenciesAPI.addBalanceFor(serverPlayer, currency, amount);
        if(notify)
            new DisplayRewardToastMessage(this.id,
                    EternalCurrenciesAPI.getCurrencyTranslationComponent(currency, amount),
                    Icon.getIcon(EternalCurrenciesAPI.getCurrencyData(currency).icon())
            ).sendTo(serverPlayer);
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("currency", currency.toString());
        nbt.putLong("amount", amount);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        currency = new ResourceLocation(nbt.getString("currency"));
        amount = nbt.getLong("amount");
    }

    @Override
    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(currency.toString());
        buffer.writeLong(amount);
    }

    @Override
    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        currency = new ResourceLocation(buffer.readUtf());
        amount = buffer.readLong();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        List<ResourceLocation> currencies = EternalCurrenciesAPI.getRegisteredCurrencies().keySet().stream().toList();

        config.addEnum("currency", currency, value -> currency = value, NameMap.of(currency, currencies)
                .name(value -> Component.literal(value.toString()))
                .icon(value -> Icon.getIcon(EternalCurrenciesAPI.getCurrencyData(value).icon()))
                .create(), EternalCurrencies.CURRENCY_COINS);

        config.addLong("amount", amount, value -> amount = value, 10L, 1L, Long.MAX_VALUE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        return EternalCurrenciesAPI.getCurrencyTranslationComponent(currency, amount);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        return Icon.getIcon(EternalCurrenciesAPI.getCurrencyData(currency).icon());
    }

    @Override
    public String getButtonText() {
        return Long.toString(amount);
    }

    @Override
    public boolean ignoreRewardBlocking() {
        return true;
    }
}
