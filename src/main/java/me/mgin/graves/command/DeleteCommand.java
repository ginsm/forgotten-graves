package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.NbtHelper;
import me.mgin.graves.util.Responder;
import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static me.mgin.graves.command.utility.ArgumentUtility.*;

public class DeleteCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Responder res = new Responder(source.getPlayer(), server);

        // Get command arguments
        GameProfile player = getProfileArgument(context, "player", 3);
        int graveId = getIntegerArgument(context, "graveid", 4);
        boolean showList = getBooleanArgument(context, "showlist", 5);
        GameProfile recipient = getProfileArgument(context, "recipient", 6);

        // Handle an invalid player
        if (player == null) {
            res.sendError(Text.translatable("command.generic.error.specify-player"), null);
            return Command.SINGLE_SUCCESS;
        }

        // Get player name and state
        String name = player.getName();
        PlayerState playerState = ServerState.getPlayerState(server, player.getId());

        // Ensure grave exists
        if (playerState.graves.size() >= graveId) {
            NbtCompound grave = playerState.graves.getCompound(graveId - 1);

            // Removing 1 since IDs are one-based indexed for interface.
            playerState.graves.remove(graveId - 1);

            // Mark server state dirty
            ServerState.getServerState(server).markDirty();
            res.sendInfo(Text.translatable("command.delete.deleted-grave", graveId, player.getName()), null);

            // Run if the grave has not been retrieved
            if (!grave.getBoolean("retrieved")) {
                GravesConfig config = GravesConfig.getConfig();
                boolean destructive = config.server.destructiveDeleteCommand;

                // Removes the grave from the world (if set to true)
                if (destructive) {
                    BlockPos gravePos = NbtHelper.readCoordinates(grave);
                    String storedDimension = grave.getString("dimension");

                    // Search for the world the grave is located
                    for (ServerWorld world : server.getWorlds()) {
                        String dimension = VersionedCode.Worlds.getDimension(world);

                        if (!dimension.equals(storedDimension)) continue;

                        // Check if block is a grave block
                        if (world.getBlockEntity(gravePos) instanceof GraveBlockEntity graveEntity) {
                            // Verify grave is identical to the stored grave
                            if (graveEntity.getMstime() == grave.getLong("mstime")) {
                                // This needs to be set in order to actually remove the grave from the world
                                GraveBlockBase graveBlock = (GraveBlockBase) world.getBlockState(gravePos).getBlock();
                                graveBlock.setBrokenByPlayer(true);

                                // Remove the grave
                                world.removeBlock(gravePos, false);
                            }
                        }
                    }
                }
            }
        } else {
            res.sendError(Text.translatable("command.generic.error.grave-doesnt-exist", name), null);
        }

        // This portion runs the list command after clearing a grave if set to true; mostly used when clearing
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
            ListCommand.executeWithoutCommand(res, player, recipient, page, server, source.getPlayer());
        }

        return Command.SINGLE_SUCCESS;
    }
}
