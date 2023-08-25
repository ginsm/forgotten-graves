package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.Responder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PlayersCommand {
    /**
     *
     * @param context {@code CommandContext<ServerCommandSource>}
     * @return int
     */
    static public int execute(CommandContext<ServerCommandSource> context) {
        // Necessary context variables
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Responder res = new Responder(source.getPlayer(), server);
        ServerState serverState = ServerState.getServerState(server);

        // Ensure source has permission
        if (!source.hasPermissionLevel(4)) {
            res.sendError(Text.translatable("command.generic.error.no-permission"), null);
            return Command.SINGLE_SUCCESS;
        }

        // Used to create a list of players who are being tracked
        AtomicReference<Text> message = new AtomicReference<>(Text.literal("")
            .append(res.info(Text.translatable("command.players.beginning"))));

        // Iterate over players
        for (Map.Entry<UUID, PlayerState> entry : serverState.players.entrySet()) {
            UUID id = entry.getKey();
            PlayerState playerState = entry.getValue();

            if (playerState.graves.size() == 0) continue;

            // Check if the profile exists and if it does, add to message
            Optional<GameProfile> potentialProfile = server.getUserCache().getByUuid(id);
            potentialProfile.ifPresent(profile -> {
                message.set(message.get().copy().append(
                    Text.translatable("command.players.information",
                        res.highlight(profile.getName()),
                        playerState.graves.size()
                    )
                ));
            });
        }

        // Send the players list to the issuer
        res.sendInfo(message.get(), null);

        return Command.SINGLE_SUCCESS;
    }
}
