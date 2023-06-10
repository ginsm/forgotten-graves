package me.mgin.graves.command.utility;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class ArgumentUtility {
    /**
     * Attempts to retrieve a profile argument from the command context, returning null
     * if it is not present.
     *
     * @param context {@code CommandContext<ServerCommandSource>}
     * @param position int
     * @return GameProfile or null
     */
    public static GameProfile getProfileArgument(CommandContext<ServerCommandSource> context, String name, int position)
        throws CommandSyntaxException {
        if (context.getInput().split(" ").length > (position - 1)) {
            return GameProfileArgumentType.getProfileArgument(context, name).iterator().next();
        }
        return null;
    }

    /**
     * Attempts to retrieve an integer argument from the command context, returning -1
     * if it is not present.
     *
     * @param context {@code CommandContext<ServerCommandSource>}
     * @param position int
     * @return int or null
     */
    public static int getIntegerArgument(CommandContext<ServerCommandSource> context, String name, int position)
        throws CommandSyntaxException {
        if (context.getInput().split(" ").length > (position - 1)) {
            return context.getArgument(name, Integer.class);
        }
        return -1;
    }

    /**
     * Attempts to retrieve an integer argument from the command context, returning -1
     * if it is not present.
     *
     * @param context {@code CommandContext<ServerCommandSource>}
     * @param position int
     * @return int or null
     */
    public static boolean getBooleanArgument(CommandContext<ServerCommandSource> context, String name, int position)
        throws CommandSyntaxException {
        if (context.getInput().split(" ").length > (position - 1)) {
            return context.getArgument(name, Boolean.class);
        }
        return false;
    }
}
