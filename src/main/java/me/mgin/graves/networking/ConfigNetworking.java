package me.mgin.graves.networking;

import me.mgin.graves.Graves;
import me.mgin.graves.networking.packets.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ConfigNetworking {
    // Client Identifiers
    public static final Identifier SYNC_CONFIG_C2S = new Identifier(Graves.MOD_ID, "sync_config_c2s");
    public static final Identifier STORE_CONFIG_C2S = new Identifier(Graves.MOD_ID, "store_config_c2s");

    // Server Identifiers
    public static final Identifier REQUEST_CONFIG_S2C = new Identifier(Graves.MOD_ID, "request_config_c2s");
    public static final Identifier RELOAD_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reload_config_s2c");
    public static final Identifier SET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "set_config_s2c");
    public static final Identifier RESET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reset_config_s2c");

    /**
     * Registers Client-to-Server packet receivers
     */
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_CONFIG_C2S, SyncConfigC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(STORE_CONFIG_C2S, StoreConfigC2SPacket::receive);
    }

    /**
     * Registers Server-to-Client packet receivers
     */
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(REQUEST_CONFIG_S2C, RequestConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_CONFIG_S2C, ReloadClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RESET_CONFIG_S2C, ResetClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SET_CONFIG_S2C, SetClientConfigS2CPacket::receive);
    }
}
