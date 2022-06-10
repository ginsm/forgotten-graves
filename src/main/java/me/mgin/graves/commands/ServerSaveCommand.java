package me.mgin.graves.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.Graves;
import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ServerSaveCommand {
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
						new TranslatableText("text.forgottengraves.command.serversave").formatted(Formatting.GRAY),
						true);
			} else {
				source.sendError(new TranslatableText("error.forgottengraves.command.serversave.fail")
						.formatted(Formatting.GRAY));
			}
		} else {
			source.sendError(new TranslatableText("error.forgottengraves.command.notplayer"));
		}

		return Command.SINGLE_SUCCESS;
	}
}
