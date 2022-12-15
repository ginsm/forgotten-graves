package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientConfigSetter {
	public static int execute(CommandContext<ServerCommandSource> context) {
		String option = determineOption(context);
		String type = determineArgumentType(context);
		Object value = determineValue(context, type, option);
		ClientSetConfig.execute(context, option, value, type);
		return Command.SINGLE_SUCCESS;
	}

	private static String determineArgumentType(CommandContext<ServerCommandSource> context) {
		String node = context.getNodes().get(context.getNodes().size() - 1).toString();
		Pattern pattern = Pattern.compile("(integer|BoolArgumentType|literal)");
		Matcher matcher = pattern.matcher(node);
		if (!matcher.find()) throw new IllegalStateException("Unexpected value: " + node);
		return matcher.group();
	}

	private static String determineOption(CommandContext<ServerCommandSource> context) {
		String[] input = context.getInput().split(" ");
		return input[ArrayUtils.indexOf(input, "set") + 1];
	}

	private static Object determineValue(CommandContext<ServerCommandSource> context, String type, String option) {
		return switch (type) {
			case "BoolArgumentType" -> BoolArgumentType.getBool(context, "value");
			case "StringArgumentType" -> StringArgumentType.getString(context, "value");
			case "integer" -> IntegerArgumentType.getInteger(context, "value");
			case "literal" -> context.getNodes().get(context.getNodes().size() - 1).getNode().getName();
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}
}
