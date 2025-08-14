package me.mgin.graves.config;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.config.enums.*;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ConfigOptions {
    // Houses all the config option information
    static public List<String> subclass = new ArrayList<>();
    static public List<String> all = new ArrayList<>();
    static public Map<String, List<String>> options = new HashMap<>();
    static public Map<String, List<String>> enums = new HashMap<>();

    /**
     * Generates lists containing the subclass names, field names for each subclass, and enum field names.
     */
    public static void generateConfigOptions() {
        // Get declared subclass names
        for (Field field : GravesConfig.class.getDeclaredFields()) {
            subclass.add(field.getName());

            // Get field's class
            Class<?> clazz = field.getType();

            // Store fields
            List<String> fieldNames = new ArrayList<>();

            for (Field subfield : clazz.getDeclaredFields()) {
                // Check if field is an enum
                if (subfield.getType().isEnum()) {
                    // Creates a list of constants (and converts them to strings)
                    List<String> constants = Arrays.stream(subfield.getType().getEnumConstants())
                            .map(Object::toString)
                            .toList();

                    // Store enum name and constants
                    enums.put(subfield.getName(), constants);
                }

                // Add field name to list
                fieldNames.add(subfield.getName());
            }

            // Add the field names to options and all lists
            options.put(field.getName(), fieldNames);
            all.addAll(fieldNames);
        }
    }

    /**
     * Converts string-based values into enum values (if contained in option enum).
     *
     * @param value  String
     * @param option String
     * @return {@code Enum<?>}
     */
    static public Enum<?> convertStringToEnum(String option, String value) {
        if (!ConfigOptions.enums.get(option).contains(value)) return null;

        return switch (option) {
            case "retrievalType" -> GraveRetrievalType.valueOf(value);
            case "dropType" -> GraveDropType.valueOf(value);
            case "expStorageType" -> GraveExpStoreType.valueOf(value);
            case "capType", "percentageType" -> ExperienceType.valueOf(value);
            case "decayRobbing" -> DecayingGrave.BlockDecay.valueOf(value);
            case "mergeOrder" -> GraveMergeOrder.valueOf(value);
            default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + value);
        };
    }
}
