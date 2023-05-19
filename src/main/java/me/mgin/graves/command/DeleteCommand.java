package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.Responder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static me.mgin.graves.command.utility.ArgumentUtility.*;

public class DeleteCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        Responder res = new Responder(context);

        // Get command arguments
        GameProfile player = getProfileArgument(context, "player", 3);
        int graveId = getIntegerArgument(context, "graveid", 4);
        boolean showList = getBooleanArgument(context, "showlist", 5);
        GameProfile recipient = getProfileArgument(context, "recipient", 6);

        // Handle an invalid player
        if (player == null) {
            res.sendError(Text.translatable("command.generic.error.specify-player"), null);
            return Command.SINGLE_SUCCESS;
        }

        // Get player name and state
        String name = player.getName();
        PlayerState playerState = ServerState.getPlayerState(server, player.getId());

        // Ensure grave exists
        if (playerState.graves.size() >= graveId) {
            // Removing 1 since IDs are one-based indexed for interface.
            playerState.graves.remove(graveId - 1);

            // Mark server state dirty
            ServerState.getServerState(server).markDirty();
            res.sendInfo(Text.translatable("command.delete.deleted-grave", graveId, player.getName()), null);
        } else {
            res.sendError(Text.translatable("command.generic.error.grave-doesnt-exist", name), null);
        }

        // This portion runs the list command after clearing a grave if set to true; mostly used when clearing
        // said grave from the list command itself.
        if (showList) {
            int page;

            // Resolve the page number
            if (graveId % 5 == 0) {
                page = graveId / 5;
            } else {
                page = (int) (Math.floor((double) graveId / 5) + 1);
            }

            // Run the List Command
            ListCommand.executeWithoutCommand(res, player, recipient, page, server, context.getSource().getPlayer());
        }

        return Command.SINGLE_SUCCESS;
    }
}
