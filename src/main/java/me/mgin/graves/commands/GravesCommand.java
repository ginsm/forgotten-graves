package me.mgin.graves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class GravesCommand {
	static public int execute(CommandContext<ServerCommandSource> context) {
		context.getSource().sendError(new TranslatableText("text.forgottengraves.command.graves"));
		return Command.SINGLE_SUCCESS;
	}
}
