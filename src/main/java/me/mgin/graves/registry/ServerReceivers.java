package me.mgin.graves.registry;

import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerReceivers {
	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(Constants.SEND_CLIENT_CONFIG,
				(server, player, handler, buf, sender) -> {
					GravesConfig config = GravesConfig.deserialize(buf.readString());
					Graves.clientConfigs.put(player.getGameProfile(), config);
				});
	}
}
