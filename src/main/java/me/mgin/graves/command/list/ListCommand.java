package me.mgin.graves.command.list;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.util.Responder;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
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
        GravesConfig config = GravesConfig.getConfig();
        Responder res = new Responder(context);

        // Get arguments
        GameProfile player = getProfileArgument(context, "player", 3);
        int page = getIntegerArgument(context, "page", 4); // -1 if no page selected

        // Require the player argument when issued via console
        if (player == null && source.getPlayer() == null) {
            res.send(Text.translatable("command.generic:error.specify-player"), null);
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
        GameProfile target = player != null ? player : Objects.requireNonNull(context.getSource().getPlayer()).getGameProfile();

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
                Text.translatable("command.list.no-graves", res.highlight(target.getName())) :
                Text.translatable("command.list" + ".no-graves-on-page", page, res.highlight(target.getName())),
            null);
            return Command.SINGLE_SUCCESS;
        }

        // Send the grave list to the target
        sendGraveList(res, target, source.getPlayer(), page, startOfPage, endOfPage, playerState);

        return Command.SINGLE_SUCCESS;
    }

    private static void sendGraveList(Responder res, GameProfile target, ServerPlayerEntity player, int page,
                                      int startOfPage, int endOfPage, PlayerState playerState) {
        int amountOfPages = (int) Math.ceil((double) playerState.graves.size() / 5);

        // Page header with spacer and separator
        res.send(Text.literal(""), null);
        res.sendInfo(Text.translatable("command.list.header", res.highlight(target.getName())), null);
        res.send(res.dim(Text.literal("──────────────")), null);

        // List graves for given page
        for (int i = startOfPage; i < playerState.graves.size(); i++) {
            // Exit prematurely if page limit has been reached
            if (i == endOfPage) break;

            // Graves are nbt compounds
            NbtCompound grave = (NbtCompound) playerState.graves.get(i);

            // Create the list item's message
            Text itemMessage = Text.literal("") // Helps prevent passing styles to other Text objects
                .append(Text.literal(i + 1 + ". "))
                .append(genListCommandEntry(res, grave, player, i));

            res.sendInfo(itemMessage, null);
        }

        // Separator
        res.send(res.dim(Text.literal("──────────────")), null);

        // Send pagination (only applies to players issuing the command in game)
        if (player != null) {
            res.sendInfo(genPagination(res, page, amountOfPages, player), null);
            res.send(Text.literal(""), null);
        }
    }

    private static Text genListCommandEntry(Responder res, NbtCompound grave, ServerPlayerEntity player, int i) {
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
            // Add a message about clicking to teleport if op
            if (!retrieved) {
                // Tell player they can teleport to the grave
                hoverContent = hoverContent.copy().append(
                    res.hint(Text.translatable("command.list.entry.tooltip.click-to-teleport"))
                );

                // Attach the teleport command to the coordinate message
                message = res.runOnClick(message,
                    String.format("/execute as %s in %s run tp %d %d %d", player.getName().getString(), dimension,
                        pos.getX(), pos.getY(), pos.getZ())
                );
            }

            // Attach the op-only command buttons
            message = message.copy().append(generateButton(res,
                res.success(Text.translatable("command.list.entry.restore-button")),
                res.hint(Text.translatable("command.list.entry.restore-button.tooltip", i + 1)),
                String.format("/graves restore grave %s %d", player.getName().getString(), i + 1)
            ));
        }

        return res.hoverText(message, hoverContent);
    }

    public static Text genPagination(Responder res, int page, int amountOfPages, ServerPlayerEntity player) {
        Text pagination = Text.literal("Page: ");

        // Player name
        String name = player.getName().getString();

        // List pages
        for (int i = 1; i <= amountOfPages; i++) {
            Text pageText = Text.literal(String.format("%d ", i));

            if (page == i) {
                pagination = pagination.copy().append(res.hoverText(
                    res.info(pageText),
                    Text.translatable("command.list.pagination.current-page.tooltip")
                ));
            } else {
                pagination = pagination.copy().append(generateButton(res,
                    res.highlight(pageText),
                    Text.translatable("command.list.pagination.page-id.tooltip", i),
                    String.format("/graves list %s %d", name, i)
                ));
            }
        }

        return pagination;
    }

    private static Text generateButton(Responder res, Text message, Text hoverContent, String command) {
        return res.runOnClick(res.hoverText(message, hoverContent), command);
    }
}
