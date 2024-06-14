package me.mgin.graves.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.versioned.VersionedCode;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.mgin.graves.command.utility.ArgumentUtility.getIntegerArgument;
import static me.mgin.graves.command.utility.ArgumentUtility.getProfileArgument;
import static me.mgin.graves.util.DateFormatter.formatDate;
import static me.mgin.graves.util.NbtHelper.readCoordinates;

public class ListCommand {
    static public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Responder res = new Responder(source.getPlayer(), server);

        // Get arguments
        int page = getIntegerArgument(context, "page", 3); // -1 if no page selected
        GameProfile player = getProfileArgument(context, "player", 4);
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

        // Determine what graves to show the player
        Map<Integer, NbtCompound> graves = new HashMap<>();
        for (int i = 0; i < playerState.graves.size(); i++) {
            NbtCompound grave = playerState.graves.getCompound(i);

            // Do not show non-OP players their recovered graves.
            if (issuer != null && !issuer.hasPermissionLevel(4)) {
                if (grave.getBoolean("retrieved")) continue;
            }

            graves.put(i, grave);
        }

        // Ensure that the page has graves on it before displaying
        if (startOfPage >= config.server.storedGravesAmount || startOfPage >= graves.size()) {
            res.sendInfo(page == 1 ?
                    Text.translatable("command.list.error.no-graves", res.highlight(target.getName())) :
                    Text.translatable("command.list.error.no-graves-on-page", page, res.highlight(target.getName())),
                null);
            return;
        }

        // Send the grave list to the target
        sendGraveList(res, target, issuer, recipient, page, startOfPage, endOfPage, graves);
    }

    /**
     * @param res         Responder
     * @param target      GameProfile
     * @param issuer      ServerPlayerEntity
     * @param recipient   GameProfile
     * @param page        int
     * @param startOfPage int
     * @param endOfPage   int
     * @param graves {@code Map<Integer, NbtCompound>}
     */
    private static void sendGraveList(Responder res, GameProfile target, ServerPlayerEntity issuer, GameProfile recipient, int page,
                                      int startOfPage, int endOfPage, Map<Integer, NbtCompound> graves) {
        int amountOfPages = (int) Math.ceil((double) graves.size() / 5);

        // Seperator (no prefix)
        if (issuer != null) res.send(Text.literal(""), null);

        // Page header
        res.sendInfo(Text.translatable("command.list.header", res.highlight(target.getName())), null);

        // Separator (prefix)
        if (issuer != null) res.send(Text.literal(" "), null);

        // List graves for given the page
        int index = 0; // Needed to keep track of where program is inside the graves map (for pagination purposes).

        for (Map.Entry<Integer, NbtCompound> entry : graves.entrySet()) {
            // The index has to be incremented before the start of page check otherwise it'd never increment.
            // It could occur after the end of page check, but I figured keeping pagination together was better
            // visually.
            index++;

            // Handle pagination checks
            if (index - 1 == endOfPage) break;
            if (startOfPage > index - 1) continue;

            // Get the key (grave id) and grave NBT
            int i = entry.getKey();
            NbtCompound grave = entry.getValue();

            // Send the list entry to the issuer
            Text itemMessage = Text.literal("") // Helps prevent passing styles to other Text objects
                .append(genListCommandEntry(res, grave, issuer, recipient, target.getName(), i));

            res.sendInfo(itemMessage, null);
        }

        // Separator (prefix)
        if (issuer != null) res.send(Text.literal(" "), null);

        // Send pagination to issuer
        String recipientName = recipient != null ? recipient.getName() : target.getName();
        String paginationCommand = issuer != null && issuer.hasPermissionLevel(4) ?
            "/graves list %d " + String.format("%s %s", target.getName(), recipientName) :
            "/graves list %d";

        res.sendInfo(
            Interact.generatePagination(res, page, amountOfPages, paginationCommand),
            null
        );

        // Separator (no prefix)
        if (issuer != null) res.send(Text.literal(""), null);
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
            res.highlight(i + 1),
            res.highlight(created),
            res.highlight(retrieved ? "Yes" : "No")
        ));

        // Generate the message
        Text message;

        // Issuer is null when being issued by a non-player (server)
        if (issuer == null) {
            message = Text.literal("")
                .append(res.info(String.format("%d. ", i)))
                .append(Text.translatable("grave.coordinates", xText, yText, zText))
                .append(retrieved ? Text.literal(" (✓)") : Text.literal(""));
        } else {
            message = Text.translatable("grave.coordinates", xText, yText, zText);
        }

        // Add op-only functionality
        if (issuer != null && issuer.hasPermissionLevel(4)) {
            // Add a message about clicking to teleport if op
            if (!retrieved) {
                // Tell player they can teleport to the grave
                hoverContent = hoverContent.copy().append(
                    res.hint(Text.translatable("command.list.entry.tooltip.click-to-teleport"))
                );

                // Attach the teleport command to the coordinate message
                message = res.runOnClick(message,
                    String.format("/execute as %s in %s run tp %d %d %d", VersionedCode.getIssuerName(issuer),
                        dimension,
                        pos.getX(), pos.getY(), pos.getZ())
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
