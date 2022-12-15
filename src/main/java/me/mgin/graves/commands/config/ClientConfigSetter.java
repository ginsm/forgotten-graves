package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientConfigSetter {
	public static int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		if (source.getEntity() instanceof ServerPlayerEntity player) {
			// Determine option, type, and value. Generate a buf to send the data
			// to the client.
			String option = determineOption(context);
			String type = determineArgumentType(context);
			Object value = determineValue(context, type, option);
			PacketByteBuf buf = generateBuf(option, value, type);

			// Dispatch the buf to the client and tell it to set clientside config
			ServerPlayNetworking.send(player, Constants.SET_CLIENTSIDE_CONFIG, buf);

			// TODO - Move this to ClientReceivers and figure out the substitutes
			source.sendFeedback(
				Text.translatable("text.forgottengraves.command.set", option,value),
				true
			);
		} else {
			source.sendError(Text.translatable("error.forgottengraves.command.notplayer"));
		}
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

	private static PacketByteBuf generateBuf(String option, Object value, String type) {
		PacketByteBuf buf = PacketByteBufs.create();
		NbtCompound nbt = new NbtCompound();
		nbt.putString("option", option);
		nbt.putString("type", type);

		switch (value.getClass().getSimpleName()) {
			case "Boolean" -> nbt.putBoolean("value", (Boolean) value);
			case "Integer" -> nbt.putInt("value", (Integer) value);
			case "String" -> nbt.putString("value", (String) value);
		}

		buf.writeNbt(nbt);
		return buf;
	}
}
