package me.mgin.graves.networking.config.packet;

import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.command.utility.ConfigSetter;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;


public class SetClientConfigS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        // Extract the buf data
        CommandContextData data = CommandContextData.deserialize(_buf.readString());

        // Create config setter instance and set config
        ConfigSetter setter = new ConfigSetter(client, null);
        setter.setConfig(data);
    }
}
