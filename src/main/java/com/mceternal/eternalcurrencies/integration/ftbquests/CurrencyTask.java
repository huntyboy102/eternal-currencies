package com.mceternal.eternalcurrencies.integration.ftbquests;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class CurrencyTask extends Task {
    private ResourceLocation currency;
    private long amount;

    public CurrencyTask(long id, Quest quest) {
        this(id, quest, EternalCurrencies.CURRENCY_COINS, 10);
    }

    public CurrencyTask(long id, Quest quest, ResourceLocation currency, long amount) {
        super(id, quest);
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public long getMaxProgress() {
        return amount;
    }

    public ResourceLocation getCurrency() {
        return currency;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public TaskType getType() {
        return QuestsIntegration.Tasks.CURRENCY;
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
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        EternalCurrenciesAPI.getCurrencies(player).ifPresent(currencies -> {
            if(currencies.tryTake(currency, amount))
                teamData.addProgress(this, amount);
        });
    }

    @Override
    public boolean submitItemsOnInventoryChange() {
        return false;
    }

    @Override
    public boolean consumesResources() {
        return true;
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return super.autoSubmitOnPlayerTick();
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
    @OnlyIn(Dist.CLIENT)
    public MutableComponent getButtonText() {
        return Component.literal(Long.toString(getMaxProgress()));
    }
}
