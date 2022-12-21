package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerReloadConfig {
	static public int execute(CommandContext<ServerCommandSource> context) {
		AutoConfig.getConfigHolder(GravesConfig.class).load();
		context.getSource().sendFeedback(
				Text.translatable("command.server.config.reload:success").formatted(Formatting.GRAY), true);

		return Command.SINGLE_SUCCESS;
	}

}
