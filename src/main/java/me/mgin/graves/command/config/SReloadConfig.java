package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class SReloadConfig {
    static public int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Boolean sendCommandFeedback = source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

        AutoConfig.getConfigHolder(GravesConfig.class).load();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            if (sendCommandFeedback) player.sendMessage(
                Text.translatable("command.server.config.reload:success").formatted(Formatting.GREEN)
            );
        } else {
            if (sendCommandFeedback) source.sendFeedback(
                Text.translatable("command.server.config.reload:success").formatted(Formatting.GREEN),
                true
            );
        }

        return Command.SINGLE_SUCCESS;
    }

}
