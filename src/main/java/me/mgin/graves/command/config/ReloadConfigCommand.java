package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.util.Responder;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ReloadConfigCommand {
    /**
     * Reloads the client or server's config depending on the given input.
     *
     * @param context CommandContext.ServerCommandSource
     * @return Command.SINGLE_SUCCESS
     */
    public static int execute(CommandContext<ServerCommandSource> context) {
        CommandContextData data = new CommandContextData(context);
        ServerCommandSource source = context.getSource();
        Responder res = new Responder(source.getPlayer(), source.getServer());

        if (data.IS_SERVER) {
            executeOnServer(context, res);
        } else {
            executeOnClient(context, res);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Reloads the server config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param res    Responder
     */
    private static void executeOnServer(CommandContext<ServerCommandSource> context, Responder res) {
        AutoConfig.getConfigHolder(GravesConfig.class).load();
        res.sendSuccess(Text.translatable("command.server.config.reload:success"), null);
    }

    /**
     * Reloads the client config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param res    Responder
     */
    private static void executeOnClient(CommandContext<ServerCommandSource> context, Responder res) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, ConfigNetworking.RELOAD_CONFIG_S2C, PacketByteBufs.create());
            res.sendSuccess(Text.translatable("command.config.reload:success"), null);
        } else {
            res.sendError(Text.translatable("command.generic:error.not-player"), null);
        }
    }
}
