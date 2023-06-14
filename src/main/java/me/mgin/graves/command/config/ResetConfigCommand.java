package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ResetConfigCommand {
    /**
     * Resets the client or server's config depending on the given input.
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
     * Resets the server config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    public static void executeOnServer(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        boolean sendCommandFeedback = data.SEND_COMMAND_FEEDBACK;

        // Reset the config
        GravesConfig.setConfig(new GravesConfig());
        GravesConfig.getConfig().save();

        // Alert the user
        Text text = Text.translatable("command.server.config.reset:success").formatted(Formatting.GREEN);

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            if (sendCommandFeedback) player.sendMessage(text);
        } else {
            if (sendCommandFeedback) source.sendFeedback(() -> text, true);
        }
    }

    /**
     * Resets the client config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    public static void executeOnClient(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        boolean sendCommandFeedback = data.SEND_COMMAND_FEEDBACK;

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, ConfigNetworking.RESET_CONFIG_S2C, PacketByteBufs.create());

            if (sendCommandFeedback)
                player.sendMessage(Text.translatable("command.config.reset:success").formatted(Formatting.GREEN));
        } else {
            source.sendError(Text.translatable("command.generic:error.not-player"));
        }
    }
}
