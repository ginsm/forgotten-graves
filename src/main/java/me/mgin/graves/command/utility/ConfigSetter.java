package me.mgin.graves.command.utility;

import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Responder;
import me.mgin.graves.util.ResponderTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigSetter {
    private final MinecraftClient client;
    private final ServerCommandSource source;

    /**
     * The provided client or source will be used for alerting the issuer
     * about the result of the command.
     *
     * @param client MinecraftClient
     * @param source ServerCommandSource
     */
    public ConfigSetter(MinecraftClient client, ServerCommandSource source) {
        this.client = client;
        this.source = source;
    }

    /**
     * Sets the current GravesConfig instance based on the given
     * and alerts based on the provided client or source (whichever
     * present).
     *
     * @param data CommandContextData
     */
    public void setConfig(CommandContextData data) {
        GravesConfig config = GravesConfig.getConfig();
        String option = data.OPTION;
        Object value = data.VAL;

        // An improper enum value was given.
        if (value == null) {
            sendResponse("error.invalid-enum-value", data);
            return;
        }

        if (option.equals("clientOptions")) {
            if (!ConfigOptions.all.contains((String) value)) {
                sendResponse("error.invalid-config-option", data);
                return;
            }

            // Needed to compare after update
            List<String> oldClientOptions = new ArrayList<>(config.server.clientOptions);

            // Set value to updated client options
            value = updateClientOptions(config, data.ACTION, (String) value);

            // Value will be null if the option given was invalid
            if (value == null) return;

            // Nothing changed route
            if (value.equals(oldClientOptions)) {
                sendResponse("error.nothing-changed-client-options", data);
                return;
            }
        }

        // Set the config and save it to file
        config.setDynamicField(option, value);
        config.save();

        sendResponse(option.equals("clientOptions") ? "success-client-options" : "success", data);
    }

    /**
     * Add or remove values from the client options.
     *
     * @param config GravesConfig
     * @param action String
     * @param value  String
     * @return List.String
     */
    private List<String> updateClientOptions(GravesConfig config, String action, String value) {
        List<String> clientOptions = config.server.clientOptions;

        if (action.equals("add") && !clientOptions.contains(value))
            clientOptions.add(value);

        if (action.equals("remove"))
            clientOptions.remove(value);

        return clientOptions;
    }

    /**
     * Send a response to the source based on the given slug.
     *
     * @param slug String
     * @param data CommandContextData
     */
    private void sendResponse(String slug, CommandContextData data) {
        if (client == null && source == null) return;

        // Grab data values
        String val = String.valueOf(data.VAL);
        String option = data.OPTION;
        String text = "command.config.set:" + slug;
        Text action = Text.of(data.ACTION);
        Text onServer = Text.of("");

        // Handle client option responses
        if (data.ACTION != null) {
            action = Text.translatable(text + ":" + data.ACTION);
        }

        // Pretty boolean values
        if (val.equals("0b")) val = "false";
        if (val.equals("1b")) val = "true";

        if (data.IS_SERVER) {
            onServer = Text.translatable("command.config.set:success-server");
        }

        // Resolve response text
        Text response = switch (Objects.requireNonNull(slug)) {
            case "success" -> Text.translatable(text, option, val, onServer);
            case "success-client-options" -> Text.translatable(text, val, action, onServer);
            case "error.invalid-enum-value" -> Text.translatable(text, option);
            case "error.invalid-config-option" -> Text.translatable(text, val);
            case "error.nothing-changed-client-options" -> Text.translatable(text, val, action);
            default -> Text.literal(slug);
        };

        // This runs when modifying the server config
        if (source != null) {
            Responder res = new Responder(source.getPlayer(), source.getServer());
            res.send(slug.contains("error") ? res.error(response) : res.success(response), null);
        }

        // This runs when modifying the client config
        if (this.client != null && this.client.player != null) {
            // Hardcoding all of this for now; it's being a brat when trying to use Responder as-is.
            Text prefix = ResponderTheme.style(Text.translatable("forgottengraves.small"), "prefix").copy()
                .styled(style -> style.withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Text.translatable("forgottengraves.normal").formatted(Formatting.YELLOW)
                )));

            Text res = Text.literal("")
                .append(prefix)
                .formatted(Formatting.RESET)
                .append(response);

            this.client.player.sendMessage(
                ResponderTheme.style(res, slug.contains("error") ? "error" : "success")
            );
        }
    }
}
