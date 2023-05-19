package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.command.utility.Interact;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.Responder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static me.mgin.graves.command.utility.ArgumentUtility.getIntegerArgument;
import static me.mgin.graves.command.utility.ArgumentUtility.getProfileArgument;
import static me.mgin.graves.util.DateFormatter.formatDate;
import static me.mgin.graves.util.NbtHelper.readCoordinates;

public class ListCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        ServerCommandSource source = context.getSource();
        Responder res = new Responder(context);

        // Get arguments
        GameProfile player = getProfileArgument(context, "player", 3);
        int page = getIntegerArgument(context, "page", 4); // -1 if no page selected
        GameProfile recipient = getProfileArgument(context, "recipient", 5);

        // Require the player argument when issued via console
        if (player == null && source.getPlayer() == null) {
            res.send(Text.translatable("command.generic.error.specify-player"), null);
            return Command.SINGLE_SUCCESS;
        }

        // Ensure source is issued by a player
        if (source.getPlayer() != null && player != null) {
            ServerPlayerEntity sourcePlayer = source.getPlayer();

            // Handle players with incorrect permissions trying to see other players graves.
            if (!sourcePlayer.hasPermissionLevel(4) && !sourcePlayer.getUuid().equals(player.getId())) {
                res.sendError(Text.translatable("command.generic.error.no-permission"), null);
                return Command.SINGLE_SUCCESS;
            }
        }

        // Determine which player's graves should be listed
        GameProfile target = player != null ? player : Objects.requireNonNull(source.getPlayer()).getGameProfile();

        executeWithoutCommand(res, target, recipient, page, server, source.getPlayer());

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Allows for issuing
     *
     * @param res Responder
     * @param target GameProfile
     * @param page int
     * @param server MinecraftServer
     * @param issuer ServerPlayerEntity
     */
    public static void executeWithoutCommand(Responder res, GameProfile target, GameProfile recipient, int page,
                                             MinecraftServer server, ServerPlayerEntity issuer) {
        // Get server config
        GravesConfig config = GravesConfig.getConfig();

        // Reassign page to 1 if the page was not given
        if (page == -1) page = 1;

        // Get the requested player's state
        PlayerState playerState = ServerState.getPlayerState(server, target.getId());

        // Get page bounds and amount of pages
        int endOfPage = page * 5;
        int startOfPage = endOfPage - 5;

        // Ensure that the page has graves on it before displaying
        if (startOfPage >= config.server.storedGravesAmount || startOfPage >= playerState.graves.size()) {
            res.sendInfo(page == 1 ?
                    Text.translatable("command.list.error.no-graves", res.highlight(target.getName())) :
                    Text.translatable("command.list.error.no-graves-on-page", page, res.highlight(target.getName())),
                null);
            return;
        }

        // Send the grave list to the target
        sendGraveList(res, target, issuer, recipient, page, startOfPage, endOfPage, playerState);
    }

    /**
     * @param res         Responder
     * @param target      GameProfile
     * @param issuer      ServerPlayerEntity
     * @param recipient
     * @param page        int
     * @param startOfPage int
     * @param endOfPage   int
     * @param playerState PlayerState
     */
    private static void sendGraveList(Responder res, GameProfile target, ServerPlayerEntity issuer, GameProfile recipient, int page,
                                      int startOfPage, int endOfPage, PlayerState playerState) {
        int amountOfPages = (int) Math.ceil((double) playerState.graves.size() / 5);

        // Page header with spacer and separator
        res.send(res.dim(Text.literal("")), null);
        res.sendInfo(Text.translatable("command.list.header", res.highlight(target.getName())), null);
        res.send(res.dim(Text.literal(" ")), null);

        // List graves for given page
        for (int i = startOfPage; i < playerState.graves.size(); i++) {
            // Exit prematurely if page limit has been reached
            if (i == endOfPage) break;

            // Graves are nbt compounds
            NbtCompound grave = (NbtCompound) playerState.graves.get(i);

            // Create the list item's message
            Text itemMessage = Text.literal("") // Helps prevent passing styles to other Text objects
//                .append(Text.literal(i + 1 + ". "))
                .append(genListCommandEntry(res, grave, issuer, recipient, target.getName(), i));

            res.sendInfo(itemMessage, null);
        }

        res.send(res.dim(Text.literal(" ")), null);

        // Send pagination (only applies to players issuing the command in game)
        String rec = recipient != null ? recipient.getName() : target.getName();
        if (issuer != null) {
            res.sendInfo(Interact.generatePagination(res, page, amountOfPages,
                String.format("/graves list %s", target.getName()) + " %d " + rec),
                null
            );
        }

        res.send(res.dim(Text.literal("")), null);
    }

    private static Text genListCommandEntry(Responder res, NbtCompound grave, ServerPlayerEntity issuer,
                                            GameProfile recipient,
                                            String target, int i) {
        // Get information on the grave
        String created = formatDate(grave.getLong("mstime"));
        String dimension = grave.getString("dimension");
        boolean retrieved = grave.getBoolean("retrieved");

        // Get the block position from the grave
        BlockPos pos = readCoordinates(grave);

        // Color the coordinates based on dimension if not retrieved, otherwise dim
        Text xText = retrieved ? res.dim(pos.getX()) : res.dimension(pos.getX(), dimension);
        Text yText = retrieved ? res.dim(pos.getY()) : res.dimension(pos.getY(), dimension);
        Text zText = retrieved ? res.dim(pos.getZ()) : res.dimension(pos.getZ(), dimension);

        // Generate the hover content
        Text hoverContent = res.info(Text.translatable(
            "command.list.entry.tooltip",
            res.highlight(dimension),
            res.highlight(created),
            res.highlight(retrieved ? "Yes" : "No")
        ));

        // Generate the message
        Text message = Text.translatable("grave.coordinates", xText, yText, zText);

        // Add op-only functionality
        if (player != null && player.hasPermissionLevel(4)) {
            String name = player.getName().getString();
            // Add a message about clicking to teleport if op
            if (!retrieved) {
                // Tell player they can teleport to the grave
                hoverContent = hoverContent.copy().append(
                    res.hint(Text.translatable("command.list.entry.tooltip.click-to-teleport"))
                );

                // Attach the teleport command to the coordinate message
                message = res.runOnClick(message,
                    String.format("/execute as %s in %s run tp %d %d %d", name, dimension, pos.getX(), pos.getY(), pos.getZ())
                );
            }

            // Attach the op-only command buttons
            String rec = recipient != null ? recipient.getName() : target;

            message = message.copy()
                .append(Text.literal(" "))
                .append(Interact.generateButton(res,
                    res.success(Text.translatable("command.list.entry.restore-button")),
                    res.hint(Text.translatable("command.list.entry.restore-button.tooltip", i + 1, rec)),
                    String.format("/graves restore %s %d %s true", target, i + 1, rec)
                ));

            message = message.copy()
                .append(Text.literal(" "))
                .append(Interact.generateButton(res,
                    res.error(Text.translatable("command.list.entry.delete-button")),
                    res.hint(Text.translatable("command.list.entry.delete-button.tooltip", i + 1)),
                    String.format("/graves delete %s %d true %s", target, i + 1, rec)
                ));
        }

        return res.hoverText(message, hoverContent);
    }
}
