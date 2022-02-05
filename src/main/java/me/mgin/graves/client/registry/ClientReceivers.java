package me.mgin.graves.client.registry;

import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientReceivers {
	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(Constants.UPDATE_CLIENTSIDE_CONFIG,
				(client, handler, buf, sender) -> {
					GravesConfig.getConfig().reload();
				});
	}
}
