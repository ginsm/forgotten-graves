package me.mgin.graves.networking.config.packet;

//? if <1.20.5 {
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//?}
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class RequestConfigS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        //? if <1.20.5 {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(GravesConfig.getConfig().serialize());
        ClientPlayNetworking.send(ConfigNetworking.APPLY_CONFIG_C2S, buf);
        //?}
    }
}