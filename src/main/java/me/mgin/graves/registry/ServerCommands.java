package me.mgin.graves.registry;

import static net.minecraft.server.command.CommandManager.literal;

import me.mgin.graves.commands.ClientConfigReload;
import me.mgin.graves.commands.GravesCommand;
import me.mgin.graves.commands.ServerReloadCommand;
import me.mgin.graves.commands.ServerSaveCommand;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ServerCommands {

	/**
	 * Registers all of the commands associated with Forgotten Graves.
	 */
	public static void register() {
		// Register server-side commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("graves")
				.executes(context -> GravesCommand.execute(context))
				.then(literal("config").then(literal("reload").executes(context -> ClientConfigReload.execute(context)))
						.then(literal("server").requires(source -> source.hasPermissionLevel(4))
								.then(literal("reload").executes(context -> ServerReloadCommand.execute(context)))
								.then(literal("set").executes(context -> ServerSaveCommand.execute(context)))))));
	}

}
