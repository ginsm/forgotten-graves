package me.mgin.graves.registry;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RegisterCommands {

  /**
   * Registers all of the commands associated with Forgotten Graves.
   */
  public static void register() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
      registerGravesCmd(dispatcher);
    });
  }

  /**
   * Currently registers 1 command:
   * reload - Reloads the AutoConfig config.
   * 
   * Planned command:
   * lookup - Provides a list of all graves (unless a player is selected)
   * @param dispatcher
   */
  private static void registerGravesCmd(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
      CommandManager.literal("graves")
        .requires(source -> source.hasPermissionLevel(4))
        .then(CommandManager.literal("reload")
          .executes(context -> {
            AutoConfig.getConfigHolder(GravesConfig.class).load();
            return Command.SINGLE_SUCCESS;
          })
        )
    );
  }
  
}
