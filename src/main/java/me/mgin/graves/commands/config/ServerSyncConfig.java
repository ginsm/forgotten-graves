package me.mgin.graves.commands.config;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerSyncConfig {
	static public int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();

		if (source.getEntity()instanceof PlayerEntity player) {
			GameProfile profile = player.getGameProfile();
			GravesConfig config = Graves.clientConfigs.get(profile);

			if (config != null) {
				ConfigHolder<GravesConfig> holder = AutoConfig.getConfigHolder(GravesConfig.class);
				holder.setConfig(config);
				holder.save();
				source.sendFeedback(
						Text.translatable("command.server.config.sync:success").formatted(Formatting.GRAY), true);
			} else {
				source.sendError(
						Text.translatable("command.server.sync:error.unable-to-update").formatted(Formatting.GRAY));
			}
		} else {
			source.sendError(Text.translatable("command.generic:error.not-player"));
		}

		return Command.SINGLE_SUCCESS;
	}
}
