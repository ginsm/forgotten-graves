package me.mgin.graves.registry;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class RegisterCommands {

  /**
   * Registers all of the commands associated with Forgotten Graves.
   */
  public static void register() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
      registerGravesCommands(dispatcher)
    );
  }

  /**
   * Currently registers 1 command:
   * reload - Reloads the AutoConfig config.
   * 
   * Planned commands:
   * lookup - Provides a list of all graves (unless a player is selected)
   * config - 
   * @param dispatcher
   */
  private static void registerGravesCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
      literal("graves")
        .executes(context -> Commands.graves(context))
        .then(literal("reload")
          .requires(source -> source.hasPermissionLevel(4))
          .executes(context -> Commands.reload(context)))
    );
  }


  /**
   * This class contains various command handler methods.
   */
  private static class Commands {

    static final int graves(CommandContext<ServerCommandSource> context) {
      context.getSource().sendError(
        new TranslatableText("text.forgottengraves.command.graves")
      );
      return Command.SINGLE_SUCCESS;
    }
    
    static final int reload(CommandContext<ServerCommandSource> context) {
      AutoConfig.getConfigHolder(GravesConfig.class).load();
      context.getSource().sendFeedback(
        new TranslatableText("text.forgottengraves.command.reload")
          .formatted(Formatting.GRAY),
        true
      );
      return Command.SINGLE_SUCCESS;
    }

  }

}
