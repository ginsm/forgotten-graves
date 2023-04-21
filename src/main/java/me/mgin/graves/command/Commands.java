package me.mgin.graves.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.mgin.graves.command.config.*;
import me.mgin.graves.config.ConfigOptions;
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
        LiteralArgumentBuilder<ServerCommandSource> serverCommands = literal("server").requires(s -> s.hasPermissionLevel(4));

        // Set Config Commands (Both server and client)
        LiteralArgumentBuilder<ServerCommandSource> setConfigCommands = literal("set")
            // Boolean Args
            .then(literal("graves")
                .then(argument("graves", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("graveCoordinates")
                .then(argument("graveCoordinates", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("decayBreaksItems")
                .then(argument("decayBreaksItems", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("floatInAir")
                .then(argument("floatInAir", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("floatInWater")
                .then(argument("floatInWater", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("floatInLava")
                .then(argument("floatInLava", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            .then(literal("graveRobbing").requires(s -> s.hasPermissionLevel(4))
                .then(argument("graveRobbing", BoolArgumentType.bool()).executes(SetConfigCommand::execute))
            )
            // Integer Args
            .then(literal("maxCustomXPLevel")
                .then(argument("maxCustomXPLevel", IntegerArgumentType.integer(0)).executes(SetConfigCommand::execute))
            )
            .then(literal("decayModifier")
                .then(argument("decayModifier", IntegerArgumentType.integer(0, 100)).executes(SetConfigCommand::execute))
            )
            .then(literal("OPOverrideLevel").requires(s -> s.hasPermissionLevel(4))
                .then(argument("OPOverrideLevel", IntegerArgumentType.integer(-1, 4)).executes(SetConfigCommand::execute))
            )
            // Enum Args
            .then(literal("retrievalType")
                .then(argument("retrievalType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.retrievalType))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("dropType")
                .then(argument("dropType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.dropType))
                    .executes(SetConfigCommand::execute)
                )
            )
            .then(literal("expStorageType")
                .then(argument("expStorageType", StringArgumentType.string())
                    .suggests(ConfigOptions.suggest(ConfigOptions.expStorageType))
                    .executes(SetConfigCommand::execute)
                )
            )
            // Client Options
            .then(literal("clientOptions").requires(s -> s.hasPermissionLevel(4))
                .then(literal("add")
                    .then(argument("clientOptions:add", StringArgumentType.string())
                        .suggests(ConfigOptions.suggest(
                            ConfigOptions.buildSet(ConfigOptions.main, ConfigOptions.itemDecay, ConfigOptions.floating)
                        ))
                        .executes(SetConfigCommand::execute)
                    )
                )
                .then(literal("remove")
                    .then(argument("clientOptions:remove", StringArgumentType.string())
                        .suggests(ConfigOptions.suggest(
                            ConfigOptions.buildSet(ConfigOptions.main, ConfigOptions.itemDecay, ConfigOptions.floating)
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

        LiteralArgumentBuilder<ServerCommandSource> restore = literal("restore").requires(s -> s.hasPermissionLevel(4))
            // Command to restore graves
            .then(argument("player", GameProfileArgumentType.gameProfile())
                .then(argument("graveid", IntegerArgumentType.integer(1))
                    .executes(RestoreCommand::execute)
                    // optional recipient arg
                    .then(argument("recipient", GameProfileArgumentType.gameProfile())
                        .executes(RestoreCommand::execute)
                    )
                )
            );

        // Command to list graves
        LiteralArgumentBuilder<ServerCommandSource> list = literal("list")
            .executes(ListCommand::execute)
            .then(argument("player", GameProfileArgumentType.gameProfile())
                .executes(ListCommand::execute)
                .then(argument("page", IntegerArgumentType.integer(1))
                    .executes(ListCommand::execute)
                )
            );


        // Register commands
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, dedicated, access) -> dispatcher.register(
                literal("graves")
                    .then(list)
                    .then(restore)
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
}
