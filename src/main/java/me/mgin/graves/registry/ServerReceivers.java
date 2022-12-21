package me.mgin.graves.registry;

import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

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
				String slug = nbt.getString("text");
				String text = "command.config.set:" + slug;

				String option = nbt.getString("option");
				String value = nbt.getString("value");

				switch (Objects.requireNonNull(slug)) {
					// Success messages
					case "success" -> player.sendMessage(
							Text.translatable(text, option, value).formatted(Formatting.GREEN)
					);
					case "success-client-options" -> clientOptionsAlert(player, nbt, text, Formatting.GREEN);
					// Error messages
					case "error.invalid-enum-value" -> player.sendMessage(
						Text.translatable(text, option).formatted(Formatting.RED)
					);
					case "error.invalid-config-option" -> player.sendMessage(
						Text.translatable(text, value).formatted(Formatting.RED)
					);
					case "error.nothing-changed-client-options" -> clientOptionsAlert(player, nbt, text, Formatting.RED);
				}
			});
	}

	private static void clientOptionsAlert(ServerPlayerEntity player, NbtCompound nbt, String text, Formatting color) {
		String option = nbt.getString("option");
		String value = nbt.getString("value");
		String[] options = option.split(":");

		// Get the action ("added to", "removed to", etc)
		Text action = Text.translatable(text + ":" + options[1]);

		player.sendMessage(Text.translatable(text, value, action).formatted(color).append(Text.literal("test").formatted(Formatting.RED)));
	}
}
