package me.mgin.graves.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class ClientResetConfig {
    static public int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Boolean sendCommandFeedback = source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send(player, Constants.RESET_CLIENT_CONFIG, buf);

            if (sendCommandFeedback)
                player.sendMessage(Text.translatable("command.config.reset:success").formatted(Formatting.GREEN));
        } else {
            source.sendError(Text.translatable("command.generic:error.not-player"));
        }
        return Command.SINGLE_SUCCESS;
    }
}
