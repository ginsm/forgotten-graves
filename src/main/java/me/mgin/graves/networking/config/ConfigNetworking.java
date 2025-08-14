package me.mgin.graves.networking.config;

import me.mgin.graves.Graves;
import me.mgin.graves.networking.config.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ConfigNetworking {
    // Client Identifiers
    public static final Identifier STORE_CONFIG_C2S = new Identifier(Graves.MOD_ID, "store_config_c2s");

    // Server Identifiers
    public static final Identifier RELOAD_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reload_config_s2c");
    public static final Identifier RESET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reset_config_s2c");
    public static final Identifier STORE_CONFIG_S2C = new Identifier(Graves.MOD_ID, "store_config_s2c");

    /**
     * Registers Client-to-Server packet receivers
     */
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(STORE_CONFIG_C2S, StoreConfigC2SPacket::receive);
    }

    /**
     * Registers Server-to-Client packet receivers
     */
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_CONFIG_S2C, ReloadClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RESET_CONFIG_S2C, ResetClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STORE_CONFIG_S2C, StoreConfigS2CPacket::receive);
    }
}
