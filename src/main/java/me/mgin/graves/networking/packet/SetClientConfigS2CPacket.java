package me.mgin.graves.networking.packet;

import me.mgin.graves.command.utility.CommandContextData;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SetClientConfigS2CPacket {
    private static MinecraftClient client;

    public static void receive(MinecraftClient playerClient, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        GravesConfig config = GravesConfig.getConfig();

        // Initialize class fields
        client = playerClient;
        String success = "success";

        // Extract the buf data
        CommandContextData data = CommandContextData.deserialize(_buf.readString());
        String option = data.OPTION;
        Object value = data.VAL;
        boolean sendCommandFeedback = data.SEND_COMMAND_FEEDBACK;

        // An improper enum value was given.
        if (value == null) {
            sendResponse("error.invalid-enum-value", data);
            return;
        }

        // Handle clientOptions commands
        if (option.equals("clientOptions")) {
            // Generate the value
            List<String> oldClientOptions = new ArrayList<>(config.server.clientOptions);

            if (!ConfigOptions.all.contains((String) value)) {
                sendResponse("error.invalid-config-option", data);
                return;
            }

            value = updateClientOptions(config, data.ACTION, (String) value);

            // Value will be null if the option given was invalid
            if (value == null) return;

            // Nothing changed
            if (value.equals(oldClientOptions)) {
                sendResponse("error.nothing-changed-client-options", data);
                return;
            }

            // Reassign option so determineSubClass can operate properly
            success = "success-client-options";
        }

        // Set the config and save it to file
        config.setDynamicField(option, value);
        config.save();

        if (sendCommandFeedback) sendResponse(success, data);
    }

    static private List<String> updateClientOptions(GravesConfig config, String action, String value) {
        List<String> clientOptions = config.server.clientOptions;

        if (action.equals("add") && !clientOptions.contains(value))
            clientOptions.add(value);

        if (action.equals("remove"))
            clientOptions.remove(value);

        return clientOptions;
    }

    private static void sendResponse(String slug, CommandContextData data) {
        if (client.player == null) return;

        String val = String.valueOf(data.VAL);
        String option = data.OPTION;
        String text = "command.config.set:" + slug;
        Text action = null;

        // Handle client option responses
        if (data.ACTION != null) {
            action = Text.translatable(text + ":" + data.ACTION);
        }

        // Pretty boolean values
        if (val.equals("0b")) val = "false";
        if (val.equals("1b")) val = "true";

        switch (Objects.requireNonNull(slug)) {
            case "success" -> client.player.sendMessage(
                Text.translatable(text, option, val).formatted(Formatting.GREEN)
            );
            case "success-client-options" -> client.player.sendMessage(
                Text.translatable(text, val, action).formatted(Formatting.GREEN)
            );
            // Error messages
            case "error.invalid-enum-value" -> client.player.sendMessage(
                Text.translatable(text, option).formatted(Formatting.RED)
            );
            case "error.invalid-config-option" -> client.player.sendMessage(
                Text.translatable(text, val).formatted(Formatting.RED)
            );
            case "error.nothing-changed-client-options" -> client.player.sendMessage(
                Text.translatable(text, val, action).formatted(Formatting.RED)
            );
        }

    }
}
