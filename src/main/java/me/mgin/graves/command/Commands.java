package me.mgin.graves.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.mgin.graves.command.config.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    /**
     * Registers all server-side commands associated with Forgotten Graves.
     */
    public static void registerServerCommands() {

        // Config commands
        LiteralArgumentBuilder<ServerCommandSource> config = literal("config")
                .then(
                    literal("reload").executes(ReloadConfigCommand::execute)
                        .then(literal("client").executes(ReloadConfigCommand::execute).requires(Commands::isOperator))
                        .then(literal("server").executes(ReloadConfigCommand::execute).requires(Commands::isOperator))
                )
                .then(
                    literal("reset").executes(ResetConfigCommand::execute)
                        .then(literal("client").executes(ResetConfigCommand::execute).requires(Commands::isOperator))
                        .then(literal("server").executes(ResetConfigCommand::execute).requires(Commands::isOperator))
            )
                .then(
                    literal("applyToServer").executes(ApplyConfigC2SCommand::execute).requires(Commands::isOperator)
                );

        LiteralArgumentBuilder<ServerCommandSource> players = literal("players").requires(Commands::isOperator)
            .executes(PlayersCommand::execute);

        LiteralArgumentBuilder<ServerCommandSource> restore = literal("restore").requires(Commands::isOperator)
            .then(argument("player", GameProfileArgumentType.gameProfile())
                .then(argument("graveid", IntegerArgumentType.integer(1))
                    .executes(RestoreCommand::execute)
                    // optional
                    .then(argument("recipient", GameProfileArgumentType.gameProfile())
                        .executes(RestoreCommand::execute)
                        // optional
                        .then(argument("showlist", BoolArgumentType.bool())
                            .executes(RestoreCommand::execute)
                        )
                    )
                )
            );

        LiteralArgumentBuilder<ServerCommandSource> list = literal("list")
            .executes(ListCommand::execute)
            .then(argument("page", IntegerArgumentType.integer(1)).executes(ListCommand::execute))
            .then(argument("player", GameProfileArgumentType.gameProfile()).requires(Commands::isOperator)
                .executes(ListCommand::execute)
                // optional
                .then(argument("page", IntegerArgumentType.integer(1)).executes(ListCommand::execute)
                    // optional
                    .then(argument("recipient", GameProfileArgumentType.gameProfile()).executes(ListCommand::execute))
                )
            );

        LiteralArgumentBuilder<ServerCommandSource> delete = literal("delete").requires(Commands::isOperator)
            .then(argument("player", GameProfileArgumentType.gameProfile())
                    .then(argument("graveid", IntegerArgumentType.integer(1))
                        .executes(DeleteCommand::execute)
                        // optional
                        .then(argument("showlist", BoolArgumentType.bool())
                            .executes(DeleteCommand::execute)
                            // optional
                            .then(argument("recipient", GameProfileArgumentType.gameProfile())
                                .executes(DeleteCommand::execute)
                            )
                        )
                    )
            );


        // Register commands
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, dedicated, access) -> dispatcher.register(
                literal("graves")
                    .then(list)
                    .then(players)
                    .then(restore)
                    .then(delete)
                    .then(config)
            )
        );
    }
    
    private static boolean isOperator(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }
}
