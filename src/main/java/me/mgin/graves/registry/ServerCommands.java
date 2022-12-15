package me.mgin.graves.registry;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.mgin.graves.commands.*;
import me.mgin.graves.commands.config.ClientConfigSetter;
import me.mgin.graves.commands.config.ClientReloadConfig;
import me.mgin.graves.commands.config.ServerReloadConfig;
import me.mgin.graves.commands.config.ServerSyncConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ServerCommands {

	/**
	 * Registers all the commands associated with Forgotten Graves.
	 */
	public static void register() {
		// Register server-side commands
		CommandRegistrationCallback.EVENT.register(
			(dispatcher, dedicated, access) -> dispatcher.register(
				literal("graves").executes(GravesCommand::execute)
					.then(literal("server").requires(source -> source.hasPermissionLevel(4))
						.then(literal("config")
							.then(literal("reload").executes(ServerReloadConfig::execute))
							.then(literal("sync").executes(ServerSyncConfig::execute))
						)
					)
					.then(literal("config")
						.then(literal("reload").executes(ClientReloadConfig::execute))
						.then(literal("set")
							// Boolean Args
							.then(literal("graves")
								.then(argument("value", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
							)
							.then(literal("graveCoordinates")
								.then(argument("value", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
							)
							.then(literal("decayBreaksItems")
								.then(argument("value", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
							)
							.then(literal("graveRobbing")
								.then(argument("value", BoolArgumentType.bool()).executes(ClientConfigSetter::execute))
							)
							// Integer Args
							.then(literal("maxCustomXPLevel")
								.then(argument("value", IntegerArgumentType.integer(0)).executes(ClientConfigSetter::execute))
							)
							.then(literal("decayModifier")
								.then(argument("value", IntegerArgumentType.integer(0, 100)).executes(ClientConfigSetter::execute))
							)
							.then(literal("OPOverrideLevel")
								.then(argument("value", IntegerArgumentType.integer(-1, 4)).executes(ClientConfigSetter::execute))
							)
							// Enums
							.then(literal("retrievalType")
								.then(literal("USE").executes(ClientConfigSetter::execute))
								.then(literal("BREAK").executes(ClientConfigSetter::execute))
								.then(literal("BOTH").executes(ClientConfigSetter::execute))
							)
							.then(literal("dropType")
								.then(literal("DROP").executes(ClientConfigSetter::execute))
								.then(literal("INVENTORY").executes(ClientConfigSetter::execute))
							)
							.then(literal("expStorageType")
								.then(literal("ALL").executes(ClientConfigSetter::execute))
								.then(literal("DEFAULT").executes(ClientConfigSetter::execute))
								.then(literal("CUSTOM").executes(ClientConfigSetter::execute))
							)
						)
					)
			)
		);
	}

}
