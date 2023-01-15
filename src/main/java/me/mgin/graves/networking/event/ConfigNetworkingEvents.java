package me.mgin.graves.networking.event;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.ActionResult;

public class ConfigNetworkingEvents {
    /**
     * Registers client-side event handlers related to networking.
     */
    static public void registerClientEvents() {
        // Join Dedicated Server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            GravesConfig config = GravesConfig.getConfig();
            config.storeOnServer();
        });

        // Save Config
        AutoConfig.getConfigHolder(GravesConfig.class).registerLoadListener((manager, config) -> {
            config.storeOnServer();
            return ActionResult.SUCCESS;
        });

        // Load Config
        AutoConfig.getConfigHolder(GravesConfig.class).registerSaveListener((manager, config) -> {
            config.storeOnServer();
            return ActionResult.SUCCESS;
        });
    }

    /**
     * Registers server-side event handlers networking.
     */
    public static void registerServerEvents() {
        // Remove client configs on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            GameProfile profile = handler.player.getGameProfile();
            Graves.clientConfigs.remove(profile);
        });
    }
}
