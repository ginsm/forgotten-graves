package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.networking.ConfigNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class ClientReloadConfig {
	static public int execute(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		Boolean sendCommandFeedback = source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, ConfigNetworking.RELOAD_CONFIG_S2C, PacketByteBufs.create());

			if (sendCommandFeedback)
				source.sendFeedback(
					Text.translatable("command.config.reload:success").formatted(Formatting.GREEN),
					true
				);
		} else {
			source.sendError(Text.translatable("command.generic:error.not-player"));
		}

		return Command.SINGLE_SUCCESS;
	}
}
