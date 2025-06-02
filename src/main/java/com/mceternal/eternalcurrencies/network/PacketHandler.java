package com.mceternal.eternalcurrencies.network;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(
            EternalCurrencies.resource("main"))
            .serverAcceptedVersions(version -> true)
            .clientAcceptedVersions(version -> true)
            .networkProtocolVersion(() -> "1.0")
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(C2SBuyShopEntryPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SBuyShopEntryPacket::encode)
                .decoder(C2SBuyShopEntryPacket::new)
                .consumerMainThread(C2SBuyShopEntryPacket::handle)
                .noResponse()
                .add();
    }

    public static void sendToServer(Object packet) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllClients(Object packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }
}
