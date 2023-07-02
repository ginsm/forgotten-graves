package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.Graves;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.NbtHelper;
import me.mgin.graves.util.Responder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static me.mgin.graves.command.utility.ArgumentUtility.getBooleanArgument;
import static me.mgin.graves.command.utility.ArgumentUtility.getProfileArgument;
import static me.mgin.graves.util.DateFormatter.formatDate;

public class RestoreCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Responder res = new Responder(source.getPlayer(), server);

        // Get command arguments
        // graves restore <graveid> <player> <recipient>
        GameProfile player = getProfileArgument(context, "player", 3);
        int graveId = context.getArgument("graveid", Integer.class); // Remove one for zero-indexing
        GameProfile recipient = getProfileArgument(context, "recipient", 5);
        boolean showList = getBooleanArgument(context, "showlist", 6);

        // Handle an invalid player
        if (player == null) {
            res.send(Text.translatable("command.generic.error.specify-player"), null);
            return Command.SINGLE_SUCCESS;
        }

        // Get player name and state
        String name = player.getName();
        PlayerState playerState = ServerState.getPlayerState(server, player.getId());

        // Ensure grave exists within player's PlayerState.graves
        if (graveId > playerState.graves.size()) {
            res.sendError(Text.translatable("command.generic.error.grave-doesnt-exist", name), null);
            return Command.SINGLE_SUCCESS;
        }

        // Get requested grave
        NbtCompound grave = (NbtCompound) playerState.graves.get(graveId - 1);

        // Get player entity and ensure they are online
        GameProfile entityProfile = recipient != null ? recipient : player;
        PlayerEntity entity = server.getPlayerManager().getPlayer(entityProfile.getId());

        if (entity == null) {
            res.sendError(Text.translatable("command.generic.error.player-not-online", entityProfile.getName()), null);
            return Command.SINGLE_SUCCESS;
        }

        // Restore grave
        RetrieveGrave.retrieveWithCommand(entity, grave);

        // Alert the source and receiver
        sendPlayerFeedback(context.getSource(), player, entity, res,
            generateHoverContent(res, grave, graveId, context.getSource().getName())
        );

        // Log it to console
        System.out.printf("[%s] %s has restored %s's grave to %s. (ID: %d)\n",
            Graves.MOD_ID,
            context.getSource().getName(),
            player.getName(),
            entity.getEntityName(),
            graveId
        );

        // This portion runs the list command after restoring a grave if set to true; mostly used when restoring
        // said grave from the list command itself.
        if (showList) {
            int page;

            // Resolve the page number
            if (graveId % 5 == 0) {
                page = graveId / 5;
            } else {
                page = (int) (Math.floor((double) graveId / 5) + 1);
            }

            // Run the List Command
            ListCommand.executeWithoutCommand(res, player, entity.getGameProfile(), page, server, context.getSource().getPlayer());
        }

        return Command.SINGLE_SUCCESS;
    }

    public static void sendPlayerFeedback(ServerCommandSource source, GameProfile player, PlayerEntity entity,
                                          Responder res, Text hoverContent) {
        // Get the entity profile for easy comparison against player
        GameProfile entityProfile = entity.getGameProfile();
        GameProfile sourceProfile = null;
        boolean sourceIsPlayerEntity = source.getPlayer() != null;

        // Get the source profile, if the source is a player.
        if (sourceIsPlayerEntity) {
            sourceProfile = source.getPlayer().getGameProfile();
        }

        // Handle the issuer restoring their own grave
        if (player.equals(sourceProfile) && player.equals(entityProfile)) {
            res.sendInfo(res.hoverText(
                Text.translatable("command.restore.grave-restored"),
                hoverContent
            ), null);
            return;
        }

        // Handle entity being given their grave by another source (server, player, etc)
        if (player.equals(entityProfile)) {
            res.sendInfo(res.hoverText(
                Text.translatable("command.restore.grave-restored-by", source.getName()),
                hoverContent
            ), entity);

            // Alert the issuer of success
            if (sourceIsPlayerEntity) {
                res.sendInfo(res.hoverText(
                    Text.translatable("command.restore.restored-players-grave", entityProfile.getName()),
                    hoverContent
                ), null);
            }
            return;
        }

        // Handle a player giving another player someone else's grave
        if (!player.equals(entityProfile) && !entityProfile.equals(sourceProfile)) {
            // Alert the player that they were given another player's grave
            res.sendInfo(res.hoverText(
                Text.translatable("command.restore.received-players-grave-by", player.getName(), source.getName()),
                hoverContent
            ), entity);

            // Alert the issuer of success
            if (sourceIsPlayerEntity) {
                res.sendInfo(res.hoverText(
                    Text.translatable("command.restore.restored-players-grave-to", player.getName(), entityProfile.getName()),
                    hoverContent
                ), null);
            }

            return;
        }

        // Handle giving yourself someone else's grave
        if (entityProfile.equals(sourceProfile) && !entityProfile.equals(player)) {
            res.sendInfo(res.hoverText(
                Text.translatable("command.restore.received-players-grave", player.getName()),
                hoverContent
            ), entity);
        }
    }

    public static Text generateHoverContent(Responder res, NbtCompound grave, int graveId, String source) {
        String created = formatDate(grave.getLong("mstime"));
        String dimension = grave.getString("dimension");
        BlockPos pos = NbtHelper.readCoordinates(grave);
        GameProfile profile = NbtHelper.toGameProfile(grave.getCompound("GraveOwner"));

        return res.info(Text.translatable(
            "command.restore.restored.tooltip",
            res.highlight(profile.getName()),
            res.highlight(dimension),
            res.highlight(pos.getX()),
            res.highlight(pos.getY()),
            res.highlight(pos.getZ()),
            res.highlight(created),
            res.highlight(graveId + 1),
            res.highlight(source)
        ));
    }
}
