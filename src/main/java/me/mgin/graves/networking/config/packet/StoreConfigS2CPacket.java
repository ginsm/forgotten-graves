package me.mgin.graves.networking.config.packet;

import me.mgin.graves.client.GravesClient;
import me.mgin.graves.config.GravesConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class StoreConfigS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        GravesConfig config = GravesConfig.deserialize(_buf.readString());
        GravesClient.SERVER_CONFIG = config;
    }
}
