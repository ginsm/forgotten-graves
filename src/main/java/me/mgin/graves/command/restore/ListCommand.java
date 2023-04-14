package me.mgin.graves.command.restore;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import static me.mgin.graves.command.utility.ArgumentUtility.getIntegerArgument;
import static me.mgin.graves.command.utility.ArgumentUtility.getProfileArgument;

public class ListCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        GameProfile player = getProfileArgument(context, "player", 4);
        Integer page = getIntegerArgument(context, "page", 5); // -1 if no page selected

        // Determine which player's graves should be listed
        GameProfile target = player != null ? player : context.getSource().getPlayer().getGameProfile();

        // Reassign page to 1 if the page was not given
        if (page == -1) page = 1;
        int endOfPage = page * 5; // Used to determine end of page
        int startOfPage = endOfPage - 5;

        // Get the requested player's state
        PlayerState playerState = ServerState.getPlayerState(server, target.getId());

        // Ensure page doesn't exceed the amount of recordable restores or amount of stored graves
        GravesConfig config = GravesConfig.getConfig();
        if (startOfPage >= config.server.storedGravesAmount || startOfPage > playerState.graves.size()) {
            System.out.println("There are no results for page " + page + " for player " + target.getName() + ".");
            return Command.SINGLE_SUCCESS;
        }

        // Page header
        System.out.println("Graves for " + target.getName() + " (" + (endOfPage - 4) + "-" + endOfPage + ")" + ":");

        // This should paginate, 5 entries per page up until max.
        for (int i = startOfPage; i < playerState.graves.size(); i++) {
            // Exit prematurely if page limit has been reached
            if (i == endOfPage) break;

            // Graves are nbt compounds
            NbtCompound grave = (NbtCompound) playerState.graves.get(i);

            // Get coordinates, created time, and dimension
            int x = grave.getInt("x");
            int y = grave.getInt("y");
            int z = grave.getInt("z");
            long created = grave.getLong("mstime");
            String dimension = grave.getString("dimension");

            // Just print a log for now; increment i to start list at 1
            System.out.println((i + 1) + ": (" + dimension + ") x" + x + " y" + y + " " + "z" + z + " at " + created);
        }

        // Get PlayerState for requested player (or issuer if no player was provided)
        return Command.SINGLE_SUCCESS;
    }
}
