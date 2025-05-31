package com.mceternal.eternalcurrencies.capability;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.capability.CurrenciesCapabilities;
import com.mceternal.eternalcurrencies.api.capability.CurrencyHolderCapability;
import com.mceternal.eternalcurrencies.api.capability.ICurrencies;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber
public class PlayerCurrencyManager {

    @SubscribeEvent
    public static void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {

            ICurrencies playerCurrenciesCap = new CurrencyHolderCapability();
            LazyOptional<ICurrencies> opt = LazyOptional.of(() -> playerCurrenciesCap);
            Capability<ICurrencies> capability = CurrenciesCapabilities.CURRENCY_BEARER;

            ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
                @Override
                public CompoundTag serializeNBT() {
                    return playerCurrenciesCap.serializeNBT();
                }

                @Override
                public void deserializeNBT(CompoundTag compoundTag) {
                    playerCurrenciesCap.deserializeNBT(compoundTag);
                }

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {

                    if(cap == capability) {
                        return opt.cast();
                    }
                    return LazyOptional.empty();
                }
            };

            event.addCapability(EternalCurrencies.CURRENCIES_CAP_NAME, provider);
        }
    }

    @SubscribeEvent
    public static void persistCapability(PlayerEvent.Clone event) {
        //EternalCurrencies.LOGGER.info("wasDeath: {}, level.isClientside: {}", event.isWasDeath(), event.getEntity().level().isClientSide);
        if(event.isWasDeath() && !event.getEntity().level().isClientSide) {
            Player deadPlayer = event.getOriginal();
            deadPlayer.reviveCaps();

            event.getEntity().getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(cap ->
                    deadPlayer.getCapability(CurrenciesCapabilities.CURRENCY_BEARER).ifPresent(cap::copy));

            deadPlayer.invalidateCaps();
        }
    }
}
