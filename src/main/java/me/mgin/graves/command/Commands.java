package me.mgin.graves.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import me.mgin.graves.command.config.SReloadConfig;
import me.mgin.graves.command.config.C2SSyncConfig;
import me.mgin.graves.command.config.CSetConfig;
import me.mgin.graves.command.config.CReloadConfig;
import me.mgin.graves.command.config.CResetConfig;
import me.mgin.graves.config.ConfigOptions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    /**
     * Registers all server-side commands associated with Forgotten Graves.
     */
    public static void registerServerCommands() {
        // Register server-side commands
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, dedicated, access) -> dispatcher.register(literal("graves")
                // This is set to 2 to allow command blocks to run the commands
                .then(literal("server").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("config")
                        .then(literal("reload").executes(SReloadConfig::execute))
                        .then(literal("sync").executes(C2SSyncConfig::execute))
                    )
                )
                .then(literal("config")
                    .then(literal("reload").executes(CReloadConfig::execute))
                    .then(literal("reset").executes(CResetConfig::execute))
                    .then(literal("set")
                        // Boolean Args
                        .then(literal("graves")
                            .then(argument("graves", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("graveCoordinates")
                            .then(argument("graveCoordinates", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("decayBreaksItems")
                            .then(argument("decayBreaksItems", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("graveRobbing").requires(source -> source.hasPermissionLevel(2))
                            .then(argument("graveRobbing", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("floatInAir")
                            .then(argument("floatInAir", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("floatInWater")
                            .then(argument("floatInWater", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        .then(literal("floatInLava")
                            .then(argument("floatInLava", BoolArgumentType.bool()).executes(CSetConfig::execute))
                        )
                        // Integer Args
                        .then(literal("maxCustomXPLevel")
                            .then(argument("maxCustomXPLevel", IntegerArgumentType.integer(0)).executes(CSetConfig::execute))
                        )
                        .then(literal("decayModifier")
                            .then(argument("decayModifier", IntegerArgumentType.integer(0, 100)).executes(CSetConfig::execute))
                        )
                        .then(literal("OPOverrideLevel").requires(source -> source.hasPermissionLevel(2))
                            .then(argument("OPOverrideLevel", IntegerArgumentType.integer(-1, 4)).executes(CSetConfig::execute))
                        )
                        // Enums
                        .then(literal("retrievalType")
                            .then(argument("retrievalType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.retrievalType))
                                .executes(CSetConfig::execute)
                            )
                        )
                        .then(literal("dropType")
                            .then(argument("dropType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.dropType))
                                .executes(CSetConfig::execute)
                            )
                        )
                        .then(literal("expStorageType")
                            .then(argument("expStorageType", StringArgumentType.string())
                                .suggests(ConfigOptions.suggest(ConfigOptions.expStorageType))
                                .executes(CSetConfig::execute)
                            )
                        )
                        // Client Options
                        .then(literal("clientOptions").requires(source -> source.hasPermissionLevel(2))
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
                                    .executes(CSetConfig::execute)
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
                                    .executes(CSetConfig::execute)
                                )
                            )
                        )
                    )
                )
            )
        );
    }
}
