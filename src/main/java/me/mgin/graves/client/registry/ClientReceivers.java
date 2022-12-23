package me.mgin.graves.client.registry;

import me.mgin.graves.client.commands.SetClientConfig;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientReceivers {
	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(Constants.RELOAD_CLIENT_CONFIG,
				(client, handler, buf, sender) -> GravesConfig.getConfig().reload());

		ClientPlayNetworking.registerGlobalReceiver(Constants.RESET_CLIENT_CONFIG,
				(client, handler, buf, sender) -> {
					ConfigHolder<GravesConfig> holder = AutoConfig.getConfigHolder(GravesConfig.class);
					holder.setConfig(new GravesConfig());
					holder.save();
				});

		ClientPlayNetworking.registerGlobalReceiver(Constants.SET_CLIENT_CONFIG,
				(client, handler, buf, sender) -> SetClientConfig.execute(buf));
	}
}
