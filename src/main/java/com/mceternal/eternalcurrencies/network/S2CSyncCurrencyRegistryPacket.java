package com.mceternal.eternalcurrencies.network;

import com.google.common.collect.ImmutableMap;
import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.data.ClientCurrencyHolder;
import com.mceternal.eternalcurrencies.data.CurrencyData;
import com.mceternal.eternalcurrencies.data.CurrencyDataHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class S2CSyncCurrencyRegistryPacket implements IntSupplier {

    public final CurrencyDataHolder currencyHolder;

    public S2CSyncCurrencyRegistryPacket() {
        this.currencyHolder = EternalCurrencies.CURRENCY_DATA_HOLDER;
    }

    public S2CSyncCurrencyRegistryPacket(FriendlyByteBuf buf) {
        //EternalCurrencies.LOGGER.info("received SyncCurrencyRegistryPacket from server");
        short size = buf.readShort();
        //short initialSize = size;
        //EternalCurrencies.LOGGER.info("# of currencies: {}", size);
        ImmutableMap.Builder<ResourceLocation, CurrencyData> currencyRegistry = new ImmutableMap.Builder<>();
        while (size > 0) {
            //EternalCurrencies.LOGGER.info("reading currency #{}", initialSize - size);
            currencyRegistry.put(buf.readResourceLocation(), new CurrencyData(buf));
            --size;
        }
        this.currencyHolder = new ClientCurrencyHolder(currencyRegistry.build());
        //EternalCurrencies.LOGGER.info("Built ClientCurrencyHolder from network");
    }

    public void encode(FriendlyByteBuf buf) {
        //EternalCurrencies.LOGGER.info("encoding SyncCurrencyRegistryPacket... {}", currencyHolder);
        buf.writeShort((short) currencyHolder.getAllCurrencies().size());
        currencyHolder.getAllCurrencies().forEach((location, data) -> {
            buf.writeResourceLocation(location);
            data.writeToBuffer(buf);
            //EternalCurrencies.LOGGER.info("wrote CurrencyData for '{}' to packet.", location);
        });
    }

    public void handle(Supplier<Context> contextSupplier) {
        Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EternalCurrencies.CURRENCY_DATA_HOLDER = this.currencyHolder;
            //EternalCurrencies.LOGGER.info("synchronzied Currency Registry to client.");
        });
        context.setPacketHandled(true);
        //EternalCurrencies.LOGGER.info("packet sender: {}", context.getSender() != null ? context.getSender().getName().getString() : "server");
    }

    @Override
    public int getAsInt() {
        return hashCode();
    }
}
