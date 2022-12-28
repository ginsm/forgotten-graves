package me.mgin.graves.client.commands;

import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameRules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.mgin.graves.util.ConfigCommandUtil.determineSubClass;
import static me.mgin.graves.util.ConfigCommandUtil.extractNbtValue;

public class SetClientConfig {
    public static void execute(PacketByteBuf buf) {
        GravesConfig config = GravesConfig.getConfig();
        NbtCompound nbt = Objects.requireNonNull(buf.readNbt());

        // Passed by respective option handler
        String option = nbt.getString("option");
        String type = nbt.getString("type");
        Boolean sendCommandFeedback = nbt.getBoolean("sendCommandFeedback");
        Object value = extractNbtValue(nbt, option, type);
        String success = "success";

        // An improper enum value was given.
        if (value == null) {
            sendResponse("error.invalid-enum-value", nbt);
            return;
        }

        // Handle clientOptions commands
        if (option.contains(":")) {

            // Generate the value
            String[] options = option.split(":");
            List<String> oldClientOptions = new ArrayList<>(config.server.clientOptions);
            value = updateClientOptions(config, options[1], (String) value, nbt);

            if (value.equals(oldClientOptions)) {
                sendResponse("error.nothing-changed-client-options", nbt);
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
        AutoConfig.getConfigHolder(GravesConfig.class).save();

        if (sendCommandFeedback) sendResponse(success, nbt);
    }

    static private List<String> updateClientOptions(GravesConfig config, String secondaryOption, String value, NbtCompound nbt) {
        List<String> clientOptions = config.server.clientOptions;

        if (!ConfigOptions.all.contains(value))
            sendResponse("error.invalid-config-option", nbt);

        if (secondaryOption.equals("add") && !clientOptions.contains(value))
            clientOptions.add(value);

        if (secondaryOption.equals("remove") && clientOptions.contains(value))
            clientOptions.remove(value);

        return clientOptions;
    }

    static private void sendResponse(String text, NbtCompound nbt) {
        PacketByteBuf buf = PacketByteBufs.create();
        if (nbt == null) nbt = new NbtCompound();
        nbt.putString("text", text);
        buf.writeNbt(nbt);

        ClientPlayNetworking.send(Constants.SET_CLIENT_CONFIG_DONE, buf);
    }
}
