package me.mgin.graves.client.registry;

import me.mgin.graves.client.GravesClient;
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
      GravesConfig configData = GravesConfig.getConfig();
      GravesClient.sendServerClientConfig(configData);
    });

    // Save Config
    AutoConfig.getConfigHolder(GravesConfig.class).registerLoadListener((manager, newConfigData) -> {
      GravesClient.sendServerClientConfig(newConfigData);
      return ActionResult.SUCCESS;
    });

    // Load Config
    AutoConfig.getConfigHolder(GravesConfig.class).registerSaveListener((manager, configData) -> {
      GravesClient.sendServerClientConfig(configData);
      return ActionResult.SUCCESS;
    });
  }
}
