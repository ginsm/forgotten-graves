package me.mgin.graves.networking.config.event;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.HashSet;
import java.util.Set;

public class ConfigNetworkingEvents {
    /**
     * This set is used to send every active player the server configuration
     * whenever it's saved or reloaded.
     */
    private static Set<ServerPlayerEntity> listeners = new HashSet<>();

    /**
     * Registers client-side event handlers related to networking.
     */
    static public void registerClientEvents() {
        // Join Dedicated Server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            GravesConfig config = GravesConfig.getConfig();
            sendConfigC2S(config);
        });

        // Save Config
        AutoConfig.getConfigHolder(GravesConfig.class).registerLoadListener((manager, config) -> {
            sendConfigC2S(config);
            return ActionResult.SUCCESS;
        });

        // Load Config
        AutoConfig.getConfigHolder(GravesConfig.class).registerSaveListener((manager, config) -> {
            sendConfigC2S(config);
            return ActionResult.SUCCESS;
        });
    }

    /**
     * Registers server-side event handlers networking.
     */
    public static void registerServerEvents() {
        // Remove client configs on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            // Remove player from listeners
            listeners.remove(handler.player);

            // Remove config
            GameProfile profile = handler.player.getGameProfile();
            Graves.clientConfigs.remove(profile);
        });

        // Send player server config on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            listeners.add(handler.player);
            sendConfigS2C(GravesConfig.getConfig(), handler.player);
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            // Save Config
            AutoConfig.getConfigHolder(GravesConfig.class).registerLoadListener((manager, config) -> {
                // Need to send config to all players
                listeners.forEach((player) -> {
                    sendConfigS2C(config, player);
                });
                return ActionResult.SUCCESS;
            });

            // Load Config
            AutoConfig.getConfigHolder(GravesConfig.class).registerSaveListener((manager, config) -> {
                listeners.forEach((player) -> {
                    sendConfigS2C(config, player);
                });
                // Need to send config to all players
                return ActionResult.SUCCESS;
            });
        }
    }

    /**
     * Transmits JSON-formatted config data to the (dedicated) server. The data will
     * not be transmitted to integrated servers (single player).
     *
     * @param config GravesConfig
     */
    private static void sendConfigC2S(GravesConfig config) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Do not send a packet whilst in the menus
        if (client.world == null) {
            return;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(config.serialize());
        ClientPlayNetworking.send(ConfigNetworking.STORE_CONFIG_C2S, buf);
    }

    /**
     * Transmits JSON-formatted config data to the client; this is currently only used by the
     * {@link me.mgin.graves.mixin.ClientPlayerInteractionManagerMixin ClientPlayerInteractionManagerMixin}.
     *
     * @param config GravesConfig
     * @param player ServerPlayerEntity
     */
    private static void sendConfigS2C(GravesConfig config, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(config.serialize());
        ServerPlayNetworking.send(player, ConfigNetworking.STORE_CONFIG_S2C, buf);
    }

}
