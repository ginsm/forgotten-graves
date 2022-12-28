package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class ServerReloadConfig {
	static public int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		Boolean sendCommandFeedback = source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

		AutoConfig.getConfigHolder(GravesConfig.class).load();

		if (sendCommandFeedback)
			source.sendFeedback(
				Text.translatable("command.server.config.reload:success").formatted(Formatting.GRAY),
				true
			);

		return Command.SINGLE_SUCCESS;
	}

}
