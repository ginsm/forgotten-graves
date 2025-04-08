package me.mgin.graves.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.mgin.graves.command.config.*;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.util.ArrayUtil;
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
        // Server commands
        LiteralArgumentBuilder<ServerCommandSource> serverCommands = literal("server").requires(Commands::isOperator);

        // Set Config Commands (Both server and client)
        LiteralArgumentBuilder<ServerCommandSource> setConfigCommands = literal("set")
            // Boolean Args
            .then(literal("graves")
                .then(argument("graves", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("disableInPvP")
                .then(argument("disableInPvP", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("graveCoordinates")
                .then(argument("graveCoordinates", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("decayBreaksItems")
                .then(argument("decayBreaksItems", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("sinkInAir")
                .then(argument("sinkInAir", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("sinkInWater")
                .then(argument("sinkInWater", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("sinkInLava")
                .then(argument("sinkInLava", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("sinkThroughBlocks")
                .then(argument("sinkThroughBlocks", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("replaceBlocks")
                .then(argument("replaceBlocks", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("graveRobbing").requires(Commands::isOperator)
                .then(argument("graveRobbing", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("destructiveDeleteCommand").requires(Commands::isOperator)
                .then(argument("destructiveDeleteCommand", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("sneakSwapsDropType").requires(Commands::isOperator)
                .then(argument("sneakSwapsDropType", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("decayEnabled").requires(Commands::isOperator)
                .then(argument("decayEnabled", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            // Integer Args
            .then(literal("cap")
                .then(argument("cap", IntegerArgumentType.integer(-1)).executes(SetConfigCommand::execute))
            )
            .then(literal("percentage")
                .then(argument("percentage", IntegerArgumentType.integer(0, 100)).executes(SetConfigCommand::execute))
            )
            .then(literal("decayModifier")
                .then(argument("decayModifier", IntegerArgumentType.integer(0, 100)).executes(SetConfigCommand::execute))
            )
            .then(literal("OPOverrideLevel").requires(Commands::isOperator)
                .then(argument("OPOverrideLevel", IntegerArgumentType.integer(-1, 4)).executes(SetConfigCommand::execute))
            )
            .then(literal("storedGravesAmount").requires(Commands::isOperator)
                .then(argument("storedGravesAmount", IntegerArgumentType.integer(0, 40)).executes(SetConfigCommand::execute))
            )
            .then(literal("minStageTimeSeconds").requires(Commands::isOperator)
                .then(argument("minStageTimeSeconds", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            .then(literal("maxStageTimeSeconds").requires(Commands::isOperator)
                .then(argument("maxStageTimeSeconds", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            .then(literal("freshGraveDecayChance").requires(Commands::isOperator)
                .then(argument("freshGraveDecayChance", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            .then(literal("oldGraveDecayChance").requires(Commands::isOperator)
                .then(argument("oldGraveDecayChance", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            .then(literal("weatheredGraveDecayChance").requires(Commands::isOperator)
                .then(argument("weatheredGraveDecayChance", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            // Enum Args
            .then(literal("retrievalType")
                .then(argument("retrievalType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("retrievalType")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("dropType")
                .then(argument("dropType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("dropType")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("expStorageType")
                .then(argument("expStorageType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("expStorageType")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("percentageType")
                .then(argument("percentageType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("percentageType")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("capType")
                .then(argument("capType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("capType")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("decayRobbing")
                .then(argument("decayRobbing", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("decayRobbing")))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("mergeOrder")
                .then(argument("mergeOrder", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.enums.get("mergeOrder")))
                    .executes(SetConfigCommand::execute)
                )
            )
            // Client Options
            .then(literal("clientOptions").requires(Commands::isOperator)
                .then(literal("add")
                    .then(argument("clientOptions:add", StringArgumentType.string())
                        .suggests(ConfigOptions.suggest(
                            ArrayUtil.merge(
                                ConfigOptions.options.get("main"),
                                ConfigOptions.options.get("experience"),
                                ConfigOptions.options.get("sink")
                            )
                        ))
                        .executes(SetConfigCommand::execute)
                    )
                )
                .then(literal("remove")
                    .then(argument("clientOptions:remove", StringArgumentType.string())
                        .suggests(ConfigOptions.suggest(
                            ArrayUtil.merge(
                                ConfigOptions.options.get("main"),
                                ConfigOptions.options.get("experience"),
                                ConfigOptions.options.get("sink")
                            )
                        ))
                        .executes(SetConfigCommand::execute)
                    )
                )
            );

        // Common Client & Server Config Commands
        LiteralArgumentBuilder<ServerCommandSource> commonConfigCommands = literal("config")
            .then(literal("reload").executes(ReloadConfigCommand::execute))
            .then(literal("reset").executes(ResetConfigCommand::execute))
            .then(literal("list").executes(ListConfigCommand::execute))
            .then(setConfigCommands);

        LiteralArgumentBuilder<ServerCommandSource> serverConfigCommands = literal("config")
            .then(literal("sync").executes(C2SSyncConfigCommand::execute));

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

        // Command to list graves
        LiteralArgumentBuilder<ServerCommandSource> list = literal("list")
            .executes(ListCommand::execute)
            .then(argument("page", IntegerArgumentType.integer(1))
                .executes(ListCommand::execute)
                // optional
                .then(argument("player", GameProfileArgumentType.gameProfile()).requires(Commands::isOperator)
                    .executes(ListCommand::execute)
                    // optional
                    .then(argument("recipient", GameProfileArgumentType.gameProfile())
                        .executes(ListCommand::execute)
                    )
                )
            );

        // Command to delete a stored grave
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
                    // Client config commands
                    .then(commonConfigCommands)
                    // Server config commands
                    .then(serverCommands
                        .then(commonConfigCommands)
                        .then(serverConfigCommands)
                    )
            )
        );
    }
    
    private static boolean isOperator(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }
}
