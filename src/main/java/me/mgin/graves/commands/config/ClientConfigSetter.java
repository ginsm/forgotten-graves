package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import me.mgin.graves.networking.ConfigNetworking;
import me.mgin.graves.util.ArrayUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientConfigSetter {
	public static int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		Boolean sendCommandFeedback = source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

		if (source.getEntity() instanceof ServerPlayerEntity player) {
			// Determine option, type, and value. Generate a buf to send the data
			// to the client.
			String type = determineArgumentType(context);
			String option = determineOptionName(context, type.equals("literal"));
			Object value = parseArgumentForValue(context, type, option);
			PacketByteBuf buf = generateBuf(context, option, value, type, sendCommandFeedback);

            // Dispatch the buf to the client and tell it to set clientside config
            ServerPlayNetworking.send(player, ConfigNetworking.SET_CONFIG_S2C, buf);
        } else {
            source.sendError(Text.translatable("command.generic:error.not-player"));
        }
        return Command.SINGLE_SUCCESS;
    }

	private static String determineArgumentType(CommandContext<ServerCommandSource> context) {
		String node = context.getNodes().get(context.getNodes().size() - 1).toString();
		Pattern pattern = Pattern.compile("(integer|BoolArgumentType|literal|string)");
		Matcher matcher = pattern.matcher(node);
		if (!matcher.find()) throw new IllegalStateException("Unexpected value: " + node);
		return matcher.group();
	}

	public static String determineOptionName(CommandContext<ServerCommandSource> context, Boolean literal) {
		// Commands with type "literal" need to look at the input in order to
		// derive the config option's name.
		if (literal) {
			String[] input = context.getInput().split(" ");
			return input[ArrayUtil.indexOf(input, "set") + 1];
		}

		// Gathers the option name from the argument name (in case of literals,
		// it returns the name of the literal).
		List<ParsedCommandNode<ServerCommandSource>> nodes = context.getNodes();
		return nodes.get(nodes.size() - 1).getNode().getName();
	}

	private static Object parseArgumentForValue(CommandContext<ServerCommandSource> context, String type, String option) {
		return switch (type) {
			case "BoolArgumentType" -> context.getArgument(option, Boolean.class);
			case "string" -> context.getArgument(option, String.class);
			case "integer" -> context.getArgument(option, Integer.class);
			case "literal" -> determineOptionName(context, false);
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}

	private static PacketByteBuf generateBuf(CommandContext<ServerCommandSource> context, String option, Object value, String type, Boolean sendCommandFeedback) {
		PacketByteBuf buf = PacketByteBufs.create();
		NbtCompound nbt = new NbtCompound();
		nbt.putString("option", option);
		nbt.putString("type", type);
		nbt.putString("input", context.getInput());
		nbt.putBoolean("sendCommandFeedback", sendCommandFeedback);

		switch (value.getClass().getSimpleName()) {
			case "Boolean" -> nbt.putBoolean("value", (Boolean) value);
			case "Integer" -> nbt.putInt("value", (Integer) value);
			case "String" -> nbt.putString("value", (String) value);
		}

		buf.writeNbt(nbt);
		return buf;
	}
}
