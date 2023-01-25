package me.mgin.graves.networking.config.packet;

import me.mgin.graves.config.GravesConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncConfigC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf _buf, PacketSender sender) {
        GravesConfig config = GravesConfig.deserialize(_buf.readString());
        GravesConfig.setConfig(config);
        GravesConfig.getConfig().save();
    }
}
