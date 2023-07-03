package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.util.Responder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ResetConfigCommand {
    /**
     * Resets the client or server's config depending on the given input.
     *
     * @param context CommandContext.ServerCommandSource
     * @return Command.SINGLE_SUCCESS
     */
    public static int execute(CommandContext<ServerCommandSource> context) {
        CommandContextData data = new CommandContextData(context);
        ServerCommandSource source = context.getSource();
        Responder res = new Responder(source.getPlayer(), source.getServer());

        if (data.IS_SERVER) {
            executeOnServer(res);
        } else {
            executeOnClient(context, res);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Resets the server config based on the passed data.
     *
     * @param res Responder
     */
    public static void executeOnServer(Responder res) {
        // Reset the config
        GravesConfig.setConfig(new GravesConfig());
        GravesConfig.getConfig().save();

        res.sendSuccess(Text.translatable("command.server.config.reset:success"), null);
    }

    /**
     * Resets the client config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param res Responder
     */
    public static void executeOnClient(CommandContext<ServerCommandSource> context, Responder res) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            ServerPlayNetworking.send(player, ConfigNetworking.RESET_CONFIG_S2C, PacketByteBufs.create());
            res.sendSuccess(Text.translatable("command.config.reset:success"), null);
        } else {
            res.sendError(Text.translatable("command.generic:error.not-player"), null);
        }
    }
}
