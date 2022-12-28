package me.mgin.graves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class GravesCommand {
	static public int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();

		source.sendError(Text.translatable("command.generic:error.not-yet-implemented"));

		return Command.SINGLE_SUCCESS;
	}
}
