package me.mgin.graves.client.commands;

import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.mgin.graves.util.ConfigCommandUtil.determineSubClass;
import static me.mgin.graves.util.ConfigCommandUtil.extractNbtValue;

public class SetClientConfig {
    private static MinecraftClient client;
    private static NbtCompound nbt;

    public static void execute(MinecraftClient playerClient, PacketByteBuf buf) {
        GravesConfig config = GravesConfig.getConfig();

        // Initialize class fields
        nbt = Objects.requireNonNull(buf.readNbt());
        client = playerClient;
        String success = "success";

        // Extract nbt data
        Boolean sendCommandFeedback = nbt.getBoolean("sendCommandFeedback");
        String option = nbt.getString("option");
        String type = nbt.getString("type");
        Object value = extractNbtValue(nbt, option, type);

        // An improper enum value was given.
        if (value == null) {
            sendResponse("error.invalid-enum-value");
            return;
        }

        // Handle clientOptions commands
        if (option.contains(":")) {
            // Generate the value
            String[] options = option.split(":");
            List<String> oldClientOptions = new ArrayList<>(config.server.clientOptions);

            if (!ConfigOptions.all.contains(value)) {
                sendResponse("error.invalid-config-option");
                return;
            }

            value = updateClientOptions(config, options[1], (String) value);

            // Value will be null if the option given was invalid
            if (value == null) return;

            // Nothing changed
            if (value.equals(oldClientOptions)) {
                sendResponse("error.nothing-changed-client-options");
                return;
            }

            // Reassign option so determineSubClass can operate properly
            option = options[0];
            success = "success-client-options";
        }

        // Sets the config field dynamically
        try {
            Field target = determineSubClass(option);
            Field field = Objects.requireNonNull(target).getType().getDeclaredField(option);
            field.set(target.get(config), value);
        } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Save the config
        config.save();

        if (sendCommandFeedback) sendResponse(success);
    }

    static private List<String> updateClientOptions(GravesConfig config, String secondaryOption, String value) {
        List<String> clientOptions = config.server.clientOptions;

        if (secondaryOption.equals("add") && !clientOptions.contains(value))
            clientOptions.add(value);

        if (secondaryOption.equals("remove") && clientOptions.contains(value))
            clientOptions.remove(value);

        return clientOptions;
    }

    private static void sendResponse(String slug) {
        String val = String.valueOf(nbt.get("value"));
        String option = nbt.getString("option");
        String text = "command.config.set:" + slug;
        Text action = null;

        // Handle client option responses
        if (option.contains(":")) {
            action = Text.translatable(text + ":" + option.split(":")[1]);
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
