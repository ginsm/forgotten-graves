package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.ConfigNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReloadConfigCommand {
    /**
     * Reloads the client or server's config depending on the given input.
     *
     * @param context CommandContext.ServerCommandSource
     * @return Command.SINGLE_SUCCESS
     */
    public static int execute(CommandContext<ServerCommandSource> context) {
        CommandContextData data = new CommandContextData(context);

        if (data.IS_SERVER) {
            executeOnServer(context, data);
        } else {
            executeOnClient(context, data);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Reloads the server config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    private static void executeOnServer(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        Boolean sendCommandFeedback = data.SEND_COMMAND_FEEDBACK;

        // Reload the config
        AutoConfig.getConfigHolder(GravesConfig.class).load();

        // Alert the user
        Text text = Text.translatable("command.server.config.reload:success").formatted(Formatting.GREEN);

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            if (sendCommandFeedback) player.sendMessage(text);
        } else {
            if (sendCommandFeedback) source.sendFeedback(text, true);
        }
    }

    /**
     * Reloads the client config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    private static void executeOnClient(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        Boolean sendCommandFeedback = data.SEND_COMMAND_FEEDBACK;

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, ConfigNetworking.RELOAD_CONFIG_S2C, PacketByteBufs.create());

            if (sendCommandFeedback)
                player.sendMessage(Text.translatable("command.config.reload:success").formatted(Formatting.GREEN));
        } else {
            source.sendError(Text.translatable("command.generic:error.not-player"));
        }
    }
}
