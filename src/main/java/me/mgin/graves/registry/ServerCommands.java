package me.mgin.graves.registry;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.mgin.graves.commands.*;
import me.mgin.graves.commands.ConfigSetter;
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
							.then(literal("reload").executes(ServerReloadCommand::execute))
							.then(literal("sync").executes(ServerSaveCommand::execute))
						)
					)
					.then(literal("config")
						.then(literal("reload").executes(ClientConfigReload::execute))
						.then(literal("set")
							// Boolean Args
							.then(literal("graves")
								.then(argument("value", BoolArgumentType.bool()).executes(ConfigSetter::execute))
							)
							.then(literal("graveCoordinates")
								.then(argument("value", BoolArgumentType.bool()).executes(ConfigSetter::execute))
							)
							.then(literal("decayBreaksItems")
								.then(argument("value", BoolArgumentType.bool()).executes(ConfigSetter::execute))
							)
							.then(literal("graveRobbing")
								.then(argument("value", BoolArgumentType.bool()).executes(ConfigSetter::execute))
							)
							// Integer Args
							.then(literal("maxCustomXPLevel")
								.then(argument("value", IntegerArgumentType.integer(0)).executes(ConfigSetter::execute))
							)
							.then(literal("decayModifier")
								.then(argument("value", IntegerArgumentType.integer(0, 100)).executes(ConfigSetter::execute))
							)
							.then(literal("OPOverrideLevel")
								.then(argument("value", IntegerArgumentType.integer(-1, 4)).executes(ConfigSetter::execute))
							)
							// Enums
							.then(literal("retrievalType")
								.then(literal("USE").executes(ConfigSetter::execute))
								.then(literal("BREAK").executes(ConfigSetter::execute))
								.then(literal("BOTH").executes(ConfigSetter::execute))
							)
							.then(literal("dropType")
								.then(literal("DROP").executes(ConfigSetter::execute))
								.then(literal("INVENTORY").executes(ConfigSetter::execute))
							)
							.then(literal("expStorageType")
								.then(literal("ALL").executes(ConfigSetter::execute))
								.then(literal("DEFAULT").executes(ConfigSetter::execute))
								.then(literal("CUSTOM").executes(ConfigSetter::execute))
							)
						)
					)
			)
		);
	}

}
