package me.mgin.graves.networking.config;

import me.mgin.graves.Graves;
import me.mgin.graves.client.GravesClient;
import me.mgin.graves.config.GravesConfig;
//? if <1.20.5 {
import me.mgin.graves.networking.config.packet.*;
//?}
//? if >=1.20.5 {
import me.mgin.graves.networking.config.payload.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ConfigNetworking {
    //? if <1.20.5 {
    // Client Identifiers (legacy)
    public static final Identifier APPLY_CONFIG_C2S = new Identifier(Graves.MOD_ID, "apply_config_c2s");
    public static final Identifier STORE_CONFIG_C2S = new Identifier(Graves.MOD_ID, "store_config_c2s");

    // Server Identifiers (legacy)
    public static final Identifier REQUEST_CONFIG_S2C = new Identifier(Graves.MOD_ID, "request_config_c2s");
    public static final Identifier RELOAD_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reload_config_s2c");
    public static final Identifier RESET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reset_config_s2c");
    public static final Identifier STORE_CONFIG_S2C = new Identifier(Graves.MOD_ID, "store_config_s2c");
    //?}

    /**
     * Registers payload types for 1.20.5+. Must be called before registering receivers.
     */
    public static void registerPayloadTypes() {
        //? if >=1.20.5 {
        // Client-to-Server payloads
        PayloadTypeRegistry.playC2S().register(ApplyConfigC2SPayload.ID, ApplyConfigC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StoreConfigC2SPayload.ID, StoreConfigC2SPayload.CODEC);

        // Server-to-Client payloads
        PayloadTypeRegistry.playS2C().register(RequestConfigS2CPayload.ID, RequestConfigS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ReloadClientConfigS2CPayload.ID, ReloadClientConfigS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResetClientConfigS2CPayload.ID, ResetClientConfigS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StoreConfigS2CPayload.ID, StoreConfigS2CPayload.CODEC);
        //?}
    }

    /**
     * Registers Client-to-Server packet receivers.
     */
    public static void registerC2SPackets() {
        //? if <1.20.5 {
        ServerPlayNetworking.registerGlobalReceiver(APPLY_CONFIG_C2S, ApplyConfigC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(STORE_CONFIG_C2S, StoreConfigC2SPacket::receive);
        //?} else {
        ServerPlayNetworking.registerGlobalReceiver(ApplyConfigC2SPayload.ID, (payload, context) -> {
            GravesConfig config = GravesConfig.deserialize(payload.configJson());
            GravesConfig.setConfig(config);
            GravesConfig.getConfig().save();
        });
        ServerPlayNetworking.registerGlobalReceiver(StoreConfigC2SPayload.ID, (payload, context) -> {
            GravesConfig config = GravesConfig.deserialize(payload.configJson());
            Graves.clientConfigs.put(context.player().getGameProfile(), config);
        });
        //?}
    }

    /**
     * Registers Server-to-Client packet receivers.
     */
    public static void registerS2CPackets() {
        //? if <1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(REQUEST_CONFIG_S2C, RequestConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RELOAD_CONFIG_S2C, ReloadClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RESET_CONFIG_S2C, ResetClientConfigS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STORE_CONFIG_S2C, StoreConfigS2CPacket::receive);
        //?} else {
        ClientPlayNetworking.registerGlobalReceiver(RequestConfigS2CPayload.ID, (payload, context) -> {
            ClientPlayNetworking.send(new ApplyConfigC2SPayload(GravesConfig.getConfig().serialize()));
        });
        ClientPlayNetworking.registerGlobalReceiver(ReloadClientConfigS2CPayload.ID, (payload, context) -> {
            GravesConfig.getConfig().reload();
        });
        ClientPlayNetworking.registerGlobalReceiver(ResetClientConfigS2CPayload.ID, (payload, context) -> {
            GravesConfig.getConfig().resetConfig().save();
        });
        ClientPlayNetworking.registerGlobalReceiver(StoreConfigS2CPayload.ID, (payload, context) -> {
            GravesClient.SERVER_CONFIG = GravesConfig.deserialize(payload.configJson());
        });
        //?}
    }
}
