package me.mgin.graves.registry;

import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.mgin.graves.util.ConfigCommandUtil.extractNbtValue;

public class ServerReceivers {
	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(Constants.SEND_CLIENT_CONFIG,
			(server, player, handler, buf, sender) -> {
				GravesConfig config = GravesConfig.deserialize(buf.readString());
				Graves.clientConfigs.put(player.getGameProfile(), config);
			});

		ServerPlayNetworking.registerGlobalReceiver(Constants.SET_CLIENT_CONFIG_DONE,
			(server, player, handler, buf, sender) -> {
				NbtCompound nbt = buf.readNbt();
				String text = nbt.getString("text");
				String fullText = String.format("config.set.%s", text);

				switch (text) {
					case "error.missing-nbt", "error.invalid-enum-value" -> {
						player.sendMessage(
							Text.translatable(fullText).formatted(Formatting.RED)
						);
					}
					case "error.invalid-config-option" -> {
						String option = nbt.getString("option");
						Object value = extractNbtValue(nbt, option, nbt.getString("type"));
						player.sendMessage(
							Text.translatable(fullText, value.toString()).formatted(Formatting.RED)
						);
					}
					case "success" -> {
						String option = nbt.getString("option");
						String value = nbt.getString("value");

						// Customized alert for clientOptions setting
						if (option.contains(":")) {
							String[] options = option.split(":");
							String action = options[1].equals("add") ? "added to" : "removed from";
							player.sendMessage(
								Text.translatable(fullText + "-client-options", value, action).formatted(Formatting.GREEN)
							);
							break;
						}

						player.sendMessage(
								Text.translatable(fullText, option, value).formatted(Formatting.GREEN)
						);
					}
				}
			});
	}
}
