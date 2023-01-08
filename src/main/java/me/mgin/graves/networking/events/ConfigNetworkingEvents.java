package me.mgin.graves.networking.events;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.ActionResult;

public class ConfigNetworkingEvents {
    /**
     * Registers client-side event handlers.
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
     * Register server-side event handlers.
     */
    public static void registerServerEvents() {
        // None yet!
    }
}
