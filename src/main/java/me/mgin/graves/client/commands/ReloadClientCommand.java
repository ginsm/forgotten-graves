package me.mgin.graves.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import me.mgin.graves.config.GravesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ReloadClientCommand {
  static public int execute(CommandContext<FabricClientCommandSource> context) {
    AutoConfig.getConfigHolder(GravesConfig.class).load();
    context.getSource().sendFeedback(
      new TranslatableText("text.forgottengraves.command.reload")
        .formatted(Formatting.GRAY)
    );
    return Command.SINGLE_SUCCESS;
  }
}
