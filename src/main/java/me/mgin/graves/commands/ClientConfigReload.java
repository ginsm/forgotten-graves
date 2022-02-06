package me.mgin.graves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ClientConfigReload {
	static public int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();

		if (source.getEntity()instanceof ServerPlayerEntity player) {
			PacketByteBuf buf = PacketByteBufs.create();
			ServerPlayNetworking.send(player, Constants.UPDATE_CLIENTSIDE_CONFIG, buf);

			source.sendFeedback(new TranslatableText("text.forgottengraves.command.reload").formatted(Formatting.GRAY),
					true);
		} else {
			source.sendError(new TranslatableText("error.forgottengraves.command.notplayer"));
		}

		return Command.SINGLE_SUCCESS;
	}
}
