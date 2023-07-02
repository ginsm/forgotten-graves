package me.mgin.graves.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class Responder {
    PlayerEntity player;
    MinecraftServer server;
    boolean sendCommandFeedback;

    /**
     * Creates a new Responder class.
     *
     * @param player PlayerEntity
     * @param server MinecraftServer
     */
    public Responder(PlayerEntity player, MinecraftServer server) {
        this.player = player;
        this.server = server;
        this.sendCommandFeedback = sendCommandFeedback(server);
    }

    /**
     * Dispatches a message to the source; respecting the sendCommandFeedback gamerule
     * and applying a prefix.
     *
     * @param message Text
     * @param target PlayerEntity
     */
    public void send(Text message, PlayerEntity target) {

        // Do not send message if command feedback  is disabled
        if (!sendCommandFeedback) return;

        // Create prefix (with hover)
        Text prefix = ResponderTheme.style(Text.translatable("forgottengraves.small"), "prefix").copy()
            .styled(style -> style.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.translatable("forgottengraves.normal").formatted(Formatting.YELLOW)
            )));

        // Compose message with prefix (if message has any content)
        Text response = Text.literal(""); // This is ghetto but helps prevent transitive styling.

        if (message.getString().length() > 0) {
            response = response.copy()
                .append(prefix)
                .formatted(Formatting.RESET)
                .append(message);
        }

        // Resolve recipient of message
        PlayerEntity recipient = target != null ? target : player;

        if (recipient != null) recipient.sendMessage(response);
        else server.sendMessage(response);
    }

    /**
     * Send the message with the error style.
     *
     * @param message Text
     * @param target PlayerEntity
     */
    public void sendError(Text message, PlayerEntity target) {
        send(ResponderTheme.style(Text.translatable("error.prefix").append(message), "error"), target);
    }

    /**
     * Stylize the Text with the error style in ResponderTheme.
     *
     * @see ResponderTheme
     * @param value Object
     * @return Text
     */
    public Text error(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "error");
    }

    /**
     * Send the message with the success style.
     *
     * @param message Text
     * @param target PlayerEntity
     */
    public void sendSuccess(Text message, PlayerEntity target) {
        send(ResponderTheme.style(message, "success"), target);
    }

    /**
     * Stylize the Text with the success style in ResponderTheme.
     *
     * @see ResponderTheme
     * @param value Object
     * @return Text
     */
    public Text success(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "success");
    }

    /**
     * Send the message with the info style.
     *
     * @param message Text
     * @param target PlayerEntity
     */
    public void sendInfo(Text message, PlayerEntity target) {
        send(ResponderTheme.style(message, "info"), target);
    }

    /**
     * Stylize the Text with the info style in ResponderTheme.
     *
     * @see ResponderTheme
     * @param value Object
     * @return Text
     */
    public Text info(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "info");
    }

    /**
     * Converts the value into a stylized text literal to make it stick out
     * more from the rest of the message.
     *
     * @param value Object
     * @return Text
     */
    public Text highlight(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "highlight");
    }

    /**
     * Converts the value into a stylized text literal that should be used
     * when providing "hints" to the player. For example, when something is
     * clickable.
     *
     * @param value Object
     * @return Text
     */
    public Text hint(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "hint");
    }

    /**
     * Converts the value into a stylized text literal that is striked out.
     *
     * @param value Object
     * @return Text
     */
    public Text strike(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "strike");
    }

    /**
     * Converts the value into a stylized text literal to make it less noticeable; this
     * is useful for less important information.
     *
     * @param value Object
     * @return Text
     */
    public Text dim(Object value) {
        if (value == null) return null;
        return ResponderTheme.style(genValueText(value), "dim");
    }

    /**
     * Converts the value into a stylized text literal based on the given
     * dimension.
     *
     * @param value Object
     * @param dimension String
     * @return Text
     */
    public Text dimension(Object value, String dimension) {
        if (value == null) return null;

        if (!ResponderTheme.dimensions.containsKey(dimension))
            return genValueText(value);

        return ResponderTheme.styleBasedOnDim(genValueText(value), dimension);
    }

    /**
     * Adds a text hover event to the message which displays the passed content.
     *
     * @param message Text
     * @param content Text
     * @return Text
     */
    public Text hoverText(Text message, Text content) {
        return message.copy().styled(style -> style.withHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT, content
        )));
    }

    public Text runOnClick(Text message, String command) {
        return message.copy().styled(style -> style.withClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            command
        )));
    }

    // Helpers

    /**
     * Converts passed value into a Text.literal.
     *
     * @param value Object
     * @return Text
     */
    private Text genValueText(Object value) {
        return value instanceof Text ? (Text) value : Text.literal(String.valueOf(value));
    }

    /**
     * Determines whether commands should send feedback based on the
     * sendCommandFeedback gamerule.
     *
     * @param server MinecraftServer
     * @return boolean
     */
    public static boolean sendCommandFeedback(MinecraftServer server) {
        return server.getWorlds().iterator().next()
            .getGameRules()
            .getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
    }
}
