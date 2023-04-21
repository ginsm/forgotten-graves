package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.Graves;
import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.NbtHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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

        if (!data.SEND_COMMAND_FEEDBACK) return Command.SINGLE_SUCCESS;

        if (data.IS_SERVER) {
            executeOnServer(context, data);
        } else {
            executeOnClient(context, data);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * List the server's clientOptions.
     * @param context CommandContext.ServerCommandSource
     * @param data CommandContextData
     */
    private static void executeOnServer(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();
        GravesConfig config = GravesConfig.getConfig();

        try {
            // Convert config to Nbt format
            NbtCompound nbt = NbtHelper.fromNbtProviderString(config.serialize());
            Text text = Text.translatable("command.config.list", NbtHelper.toPrettyPrintedText(nbt));
            nbt.remove("palette");

            if (source.getEntity() instanceof ServerPlayerEntity player) {
                player.sendMessage(text);
            } else {
                source.sendFeedback(text, true);
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List the client's clientOptions.
     * @param context CommandContext.ServerCommandSource
     * @param data CommandContextData
     */
    private static void executeOnClient(CommandContext<ServerCommandSource> context, CommandContextData data) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            GravesConfig config = Graves.clientConfigs.get(player.getGameProfile());

            try {
                // Convert config to Nbt format
                NbtCompound nbt = NbtHelper.fromNbtProviderString(config.serialize());
                nbt.remove("palette");
                Text text = Text.translatable("command.client.config.list", NbtHelper.toPrettyPrintedText(nbt));

                player.sendMessage(text);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            source.sendError(Text.translatable("command.generic:error.not-player"));
        }
    }

}
