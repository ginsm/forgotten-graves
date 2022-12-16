package me.mgin.graves.client.commands;

import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Field;
import java.util.List;

public class SetClientConfig {
    // TODO - Send server completion response to dynamically respond to player
    // Rather than just having "Option '<name>' has been changed." inside ClientConfigSetter
    public static void execute(PacketByteBuf buf) {
        GravesConfig config = GravesConfig.getConfig();
        NbtCompound nbt = buf.readNbt();
        if (nbt == null) return;

        // Passed by respective option handler
        String option = nbt.getString("option");
        String type = nbt.getString("type");
        Object value = extractNbtValue(nbt, option, type, config);
        if (value == null) return;

        // Handle clientOptions commands
        if (option.contains(":")) {
            // Do not add non-option values to the list
            if (!ConfigOptions.all.contains(value)) return;

            // Generate the value
            String[] options = option.split(":");
            value = updateClientOptions(config, options[1], (String) value);

            // Stop work if nothing was added or removed
            if (value.equals(config.server.clientOptions)) return;

            // Reassign option so determineSubClass can operate properly
            option = options[0];
        }

        // Sets the config field dynamically
        try {
            Field target = determineSubClass(option);
            Field field = target.getType().getDeclaredField(option);
            field.set(target.get(config), value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Save the config
        AutoConfig.getConfigHolder(GravesConfig.class).save();
    }

    static private List<String> updateClientOptions(GravesConfig config, String secondaryOption, String value) {
        List<String> clientOptions = config.server.clientOptions;

        if (secondaryOption.equals("add")) {
            if (clientOptions.contains(value)) return clientOptions;
            clientOptions.add((String) value);
        }

        if (secondaryOption.equals("remove")) {
            if (!clientOptions.contains(value)) return clientOptions;
            clientOptions.remove((String) value);
        }

        return clientOptions;
    }

    static private Field determineSubClass(String option) throws NoSuchFieldException {
        for (String subclass : ConfigOptions.subclass) {
            if (ConfigOptions.getSubclass(subclass).contains(option))
                return GravesConfig.class.getDeclaredField(subclass);
        }
        return null;
    }

    static private Object extractNbtValue(NbtCompound nbt, String option, String type, GravesConfig config) {
        return switch(type) {
            case "BoolArgumentType" -> nbt.getBoolean("value");
            case "integer" -> nbt.getInt("value");
            case "literal", "string" -> {
                if (ConfigOptions.enums.contains(option)) yield determineEnumValue(nbt, option, config);
                yield nbt.getString("value");
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    static private Enum<?> determineEnumValue(NbtCompound nbt, String option, GravesConfig config) {
        String value = nbt.getString("value");
        if (!ConfigOptions.validEnumValue(option, (String) value)) return null;
        return switch (option) {
            case "retrievalType" -> GraveRetrievalType.valueOf(value);
            case "dropType" -> GraveDropType.valueOf(value);
            case "expStorageType" ->  GraveExpStoreType.valueOf(value);
            default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + value);
        };
    }
}
