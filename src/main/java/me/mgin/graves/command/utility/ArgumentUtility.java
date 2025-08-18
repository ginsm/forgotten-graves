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
     */
    public static GameProfile getProfileArgument(CommandContext<ServerCommandSource> context, String name)
         {
        try {
            return GameProfileArgumentType.getProfileArgument(context, name).iterator().next();
        } catch(IllegalArgumentException | CommandSyntaxException e) {
            return null;
        }
    }

    /**
     * Attempts to retrieve an integer argument from the command context, returning -1
     * if it is not present.
     */
    public static int getIntegerArgument(CommandContext<ServerCommandSource> context, String name) {
        try {
            return context.getArgument(name, Integer.class);
        } catch(IllegalArgumentException e) {
            return -1;
        }
    }

    /**
     * Attempts to retrieve an integer argument from the command context, returning false
     * if it is not present.
     */
    public static boolean getBooleanArgument(CommandContext<ServerCommandSource> context, String name) {
        try {
            return context.getArgument(name, Boolean.class);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean issuedToServer(CommandContext<ServerCommandSource> context) {
        String[] input = context.getInput().trim().split("\\s+");
        return input[input.length - 1].equalsIgnoreCase("server");
    }
}
