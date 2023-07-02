package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.command.utility.ConfigSetter;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.util.Responder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SetConfigCommand {
    /**
     * Sets the client or server's config depending on the given input.
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
     * Sets the server config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    private static void executeOnServer(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        ConfigSetter setter = new ConfigSetter(null, source);
        setter.setConfig(data);
    }

    /**
     * Sets the client config based on the passed data.
     *
     * @param context CommandContext.ServerCommandSource
     * @param data    CommandContextData
     */
    private static void executeOnClient(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            // Generate a buf to send the command data to client
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(data.serialize());
            ServerPlayNetworking.send(player, ConfigNetworking.SET_CONFIG_S2C, buf);
        } else {
            Responder res = new Responder(source.getPlayer(), source.getServer());
            res.sendError(Text.translatable("command.generic:error.not-player"), null);
        }
    }
}
