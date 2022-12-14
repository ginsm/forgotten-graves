package me.mgin.graves.commands;

import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClientSetCommand {
    static public <T> void execute(CommandContext<ServerCommandSource> context, String option, String subclass, T value) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            PacketByteBuf buf = PacketByteBufs.create();

            // Store the option and value in nbt and write to buf
            NbtCompound nbt = new NbtCompound();
            nbt.putString("option", option);
            nbt.putString("subclass", subclass);

            switch(value.getClass().getSimpleName()) {
                case "Boolean" -> nbt.putBoolean("value", (Boolean) value);
                case "Integer" -> nbt.putInt("value", (Integer) value);
                case "String" -> nbt.putString("value", (String) value);
            }

            buf.writeNbt(nbt);

            // Send the buf and relevant info to client
            ServerPlayNetworking.send(player, Constants.SET_CLIENTSIDE_CONFIG, buf);
            source.sendFeedback(Text.translatable("text.forgottengraves.command.set", option, value).formatted(Formatting.GRAY),
                    true);
        } else {
            source.sendError(Text.translatable("error.forgottengraves.command.notplayer"));
        }
    }
}
