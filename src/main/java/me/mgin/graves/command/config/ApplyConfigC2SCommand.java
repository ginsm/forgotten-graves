package me.mgin.graves.command.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

//? if <1.20.5 {
import me.mgin.graves.networking.config.ConfigNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//?} else {
import me.mgin.graves.networking.config.payload.RequestConfigS2CPayload;
//?}
import me.mgin.graves.util.Responder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ApplyConfigC2SCommand {
    static public int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Responder res = new Responder(source.getPlayer(), source.getServer());

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            if (player.hasPermissionLevel(4)) {
                //? if <1.20.5 {
                ServerPlayNetworking.send(player, ConfigNetworking.REQUEST_CONFIG_S2C, PacketByteBufs.create());
                //?} else {
                ServerPlayNetworking.send(player, new RequestConfigS2CPayload());
                //?}
                res.sendSuccess(Text.translatable("command.server.config.sync:success"), null);
            } else {
                res.sendError(Text.translatable("command.generic.error.no-permission"), null);
            }
        } else {
            res.sendError(Text.translatable("command.generic:error.not-player"), null);
        }

        return Command.SINGLE_SUCCESS;
    }
}