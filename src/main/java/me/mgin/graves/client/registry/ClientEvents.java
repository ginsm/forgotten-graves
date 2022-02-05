package me.mgin.graves.client.registry;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.ActionResult;

public class ClientEvents {
	/**
	 * Register client-side event handlers
	 */
	static public void register() {
		// Join Dedicated Server
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			GravesConfig config = GravesConfig.getConfig();
			config.sendToServer();
		});

		// Save Config
		AutoConfig.getConfigHolder(GravesConfig.class).registerLoadListener((manager, config) -> {
			config.sendToServer();
			return ActionResult.SUCCESS;
		});

		// Load Config
		AutoConfig.getConfigHolder(GravesConfig.class).registerSaveListener((manager, config) -> {
			config.sendToServer();
			return ActionResult.SUCCESS;
		});
	}
}
