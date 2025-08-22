package com.mceternal.eternalcurrencies.capability;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.capability.ICurrencies;
import com.mceternal.eternalcurrencies.api.capability.ReferenceCurrencyHolder;
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

    /**
     * Attaches the ICurrencies capability to the given player entity.
     *
     * @param event The AttachCapabilitiesEvent for attaching capabilities to entities.
     */
    @SubscribeEvent
    public static void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            // Create a new ICurrencies instance with the player's UUID and the server instance
            ICurrencies playerCurrenciesCap = new ReferenceCurrencyHolder(player.getUUID(), () -> player.level().getServer());
            // Create a LazyOptional containing the ICurrencies instance
            LazyOptional<ICurrencies> opt = LazyOptional.of(() -> playerCurrenciesCap);
            // Get the ICurrencies capability
            Capability<ICurrencies> capability = ICurrencies.CAPABILITY;

            // Create an ICapabilityProvider that handles serialization and deserialization of the capability
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
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
                    if (cap == capability) {
                        return opt.cast();
                    }
                    return LazyOptional.empty();
                }
            };

            // Attach the ICurrencies capability to the player entity
            event.addCapability(EternalCurrencies.CURRENCIES_CAP_NAME, provider);
        }
    }

    /**
     * Handles the persistence of the ICurrencies capability when a player respawns.
     *
     * @param event The PlayerEvent.Clone event for handling player cloning (respawning).
     */
    @SubscribeEvent
    public static void persistCapability(PlayerEvent.Clone event) {
        if (event.isWasDeath() && !event.getEntity().level().isClientSide()) {
            Player deadPlayer = event.getOriginal();
            // Revive the capabilities of the original player
            deadPlayer.reviveCaps();

            // Copy the ICurrencies capability from the original player to the new player
            event.getEntity().getCapability(ICurrencies.CAPABILITY).ifPresent(cap ->
                    deadPlayer.getCapability(ICurrencies.CAPABILITY).ifPresent(cap::copy));

            // Invalidate the capabilities of the original player
            deadPlayer.invalidateCaps();
        }
    }
}
