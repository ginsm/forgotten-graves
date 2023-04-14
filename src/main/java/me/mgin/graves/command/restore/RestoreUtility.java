package me.mgin.graves.command.restore;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class RestoreUtility {
    /**
     * Attempts to retrieve an optional profile argument from the command context, returning null
     * if it is not present.
     *
     * @param context {@code CommandContext<ServerCommandSource>}
     * @param position int
     * @return GameProfile or null
     */
    public static GameProfile getOptionalProfileArgument(CommandContext<ServerCommandSource> context,
                                                         String name, int position) throws CommandSyntaxException {
        if (context.getInput().split(" ").length > (position - 1)) {
            return GameProfileArgumentType.getProfileArgument(context, name).iterator().next();
        }
        return null;
    }
}
