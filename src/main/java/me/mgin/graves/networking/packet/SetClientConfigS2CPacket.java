package me.mgin.graves.networking.packet;

import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.command.utility.ConfigSetter;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
