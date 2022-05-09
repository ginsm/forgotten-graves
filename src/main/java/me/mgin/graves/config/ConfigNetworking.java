package me.mgin.graves.config;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;

import me.mgin.graves.Graves;
import me.mgin.graves.util.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

public class ConfigNetworking {
	/**
	 * Transmits JSON-formatted config data to the (dedicated) server. The data will
	 * not be transmitted to integrated servers (singleplayer).
	 *
	 * @param configData
	 */
	public void sendToServer() {
		MinecraftClient client = MinecraftClient.getInstance();

		// Do not send a packet whilst in the menus
		if (client.world == null)
			return;

		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(this.serialize());
		ClientPlayNetworking.send(Constants.SEND_CLIENT_CONFIG, buf);
	}

	/**
	 * Reload the config instance
	 */
	public void reload() {
		AutoConfig.getConfigHolder(GravesConfig.class).load();
	}

	/**
	 * JSONify this GravesConfig instance.
	 *
	 * @return
	 */
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	/**
	 * Convert a JSONified config to a GravesConfig instance.
	 *
	 * @param config
	 * @return GravesConfig instance
	 */
	public static GravesConfig deserialize(String config) {
		Gson gson = new Gson();
		return gson.fromJson(config, GravesConfig.class);
	}

	/**
	 * Resolves whether the option is being handled by the client or server and
	 * returns the appropriate GravesConfig instance.
	 *
	 * @param option
	 * @param profile
	 * @return GravesConfig
	 */
	public static GravesConfig resolveConfig(String option, GameProfile profile) {
		GravesConfig config = GravesConfig.getConfig();

		if (config.server.clientsideOptions.contains(option)) {
			GravesConfig clientConfig = Graves.clientConfigs.get(profile);

			if (clientConfig != null) {
				System.out.println("reached");
				return clientConfig;
			}
		}

		return config;
	}
}
