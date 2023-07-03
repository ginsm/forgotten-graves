package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.Graves;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.NbtHelper;
import me.mgin.graves.util.Responder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;


public class ListConfigCommand {
    /**
     * Lists the server or client's clientOptions depending on the given input.
     *
     * @param context CommandContext.ServerCommandSource
     * @return Command.SINGLE_SUCCESS
     */
    public static int execute(CommandContext<ServerCommandSource> context) {
        CommandContextData data = new CommandContextData(context);
        ServerCommandSource source = context.getSource();
        Responder res = new Responder(source.getPlayer(), source.getServer());

        if (!data.SEND_COMMAND_FEEDBACK) return Command.SINGLE_SUCCESS;

        if (data.IS_SERVER) {
            executeOnServer(context, res);
        } else {
            executeOnClient(context, res);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * List the server's clientOptions.
     * @param context CommandContext.ServerCommandSource
     * @param res Responder
     */
    private static void executeOnServer(CommandContext<ServerCommandSource> context, Responder res) {
        GravesConfig config = GravesConfig.getConfig();

        try {
            NbtCompound nbt = NbtHelper.fromNbtProviderString(config.serialize());
            nbt.remove("palette");
            res.sendInfo(Text.translatable("command.server.config.list", NbtHelper.toPrettyPrintedText(nbt)), null);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List the client's clientOptions.
     * @param context CommandContext.ServerCommandSource
     * @param res Responder
     */
    private static void executeOnClient(CommandContext<ServerCommandSource> context, Responder res) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();

        if (player != null) {
            GravesConfig config = Graves.clientConfigs.get(player.getGameProfile());

            try {
                NbtCompound nbt = NbtHelper.fromNbtProviderString(config.serialize());
                nbt.remove("palette");
                res.sendInfo(Text.translatable("command.config.list", NbtHelper.toPrettyPrintedText(nbt)), null);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            res.sendError(Text.translatable("command.generic:error.not-player"), null);
        }
    }
}
