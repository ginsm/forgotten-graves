package me.mgin.graves.command.utility;

import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
import net.minecraft.nbt.NbtCompound;

import java.lang.reflect.Field;

public class ConfigCommandUtility {
    static public Field determineSubClass(String option) throws NoSuchFieldException {
        for (String subclass : ConfigOptions.subclass) {
            if (ConfigOptions.getSubclass(subclass).contains(option))
                return GravesConfig.class.getDeclaredField(subclass);
        }
        return null;
    }

    static public Object extractNbtValue(NbtCompound nbt, String option, String type) {
        return switch (type) {
            case "BoolArgumentType" -> nbt.getBoolean("value");
            case "integer" -> nbt.getInt("value");
            case "literal", "string" -> {
                if (ConfigOptions.enums.contains(option)) yield determineEnumValue(nbt, option);
                yield nbt.getString("value");
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    static public Enum<?> determineEnumValue(NbtCompound nbt, String option) {
        String value = nbt.getString("value");
        if (!ConfigOptions.validEnumValue(option, (String) value)) return null;
        return switch (option) {
            case "retrievalType" -> GraveRetrievalType.valueOf(value);
            case "dropType" -> GraveDropType.valueOf(value);
            case "expStorageType" -> GraveExpStoreType.valueOf(value);
            default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + value);
        };
    }
}
