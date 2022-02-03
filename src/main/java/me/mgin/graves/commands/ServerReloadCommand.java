package me.mgin.graves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ServerReloadCommand {
	static public int execute(CommandContext<ServerCommandSource> context) {
		AutoConfig.getConfigHolder(GravesConfig.class).load();
		context.getSource().sendFeedback(
				new TranslatableText("text.forgottengraves.command.reloadserver").formatted(Formatting.GRAY), true);
		return Command.SINGLE_SUCCESS;
	}

}
