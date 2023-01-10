package me.mgin.graves.registry;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.mgin.graves.commands.*;
import me.mgin.graves.commands.config.*;
import me.mgin.graves.config.ConfigOptions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ServerCommands {

    /**
     * Registers all the commands associated with Forgotten Graves.
     */
    public static void register() {
        // Register server-side commands
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, dedicated, access) -> dispatcher.register(literal("graves").executes(GravesCommand::execute)
                // This is set to 2 to allow command blocks to run the commands
                .then(literal("server").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("config")
                        .then(literal("reload").executes(ServerReloadConfig::execute))
                        .then(literal("sync").executes(ServerSyncConfig::execute))
                    )
                )
                .then(literal("config")
                    .then(literal("reload").executes(ClientReloadConfig::execute))
                    .then(literal("reset").executes(ClientResetConfig::execute))
                    .then(literal("set")
                        // Boolean Args
                        .then(literal("graves")
                            .then(argument("graves", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("graveCoordinates")
                            .then(argument("graveCoordinates", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("decayBreaksItems")
                            .then(argument("decayBreaksItems", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("graveRobbing")
                            .then(argument("graveRobbing", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("floatInAir")
                            .then(argument("floatInAir", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("floatInWater")
                            .then(argument("floatInWater", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("floatInLava")
                            .then(argument("floatInLava", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
                        )
                        // Integer Args
                        .then(literal("maxCustomXPLevel")
                            .then(argument("maxCustomXPLevel", IntegerArgumentType.integer(0)).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("decayModifier")
                            .then(argument("decayModifier", IntegerArgumentType.integer(0, 100)).executes(ClientConfigSetter::execute))
                        )
                        .then(literal("OPOverrideLevel")
                            .then(argument("OPOverrideLevel", IntegerArgumentType.integer(-1, 4)).executes(ClientConfigSetter::execute))
                        )
                        // Enums
                        .then(literal("retrievalType")
                            .then(argument("retrievalType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.retrievalType))
                                .executes(ClientConfigSetter::execute)
                            )
                        )
                        .then(literal("dropType")
                            .then(argument("dropType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.dropType))
                                .executes(ClientConfigSetter::execute)
                            )
                        )
                        .then(literal("expStorageType")
                            .then(argument("expStorageType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.expStorageType))
                                .executes(ClientConfigSetter::execute)
                            )
                        )
                        // Client Options
                        .then(literal("clientOptions")
                            .then(literal("list")) // TODO
                            .then(literal("add")
                                .then(argument("clientOptions:add", StringArgumentType.string())
                                    .suggests(ConfigOptions.suggest(
                                        ConfigOptions.buildSet(
                                            ConfigOptions.main,
                                            ConfigOptions.itemDecay,
                                            ConfigOptions.floating
                                        )
                                    ))
                                    .executes(ClientConfigSetter::execute)
                                )
                            )
                            .then(literal("remove")
                                .then(argument("clientOptions:remove", StringArgumentType.string())
                                    .suggests(ConfigOptions.suggest(
                                        ConfigOptions.buildSet(
                                            ConfigOptions.main,
                                            ConfigOptions.itemDecay,
                                            ConfigOptions.floating
                                        )
                                    ))
                                    .executes(ClientConfigSetter::execute)
                                )
                            )
                        )
                    )
                )
            )
        );
    }
}
