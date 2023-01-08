package me.mgin.graves.networking.packets;

import me.mgin.graves.client.commands.SetClientConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class SetClientConfigS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        SetClientConfig.execute(client, _buf);
    }
}
