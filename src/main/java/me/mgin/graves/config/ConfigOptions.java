package me.mgin.graves.config;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.config.enums.ExperienceType;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
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
            default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + value);
        };
    }

    public static SuggestionProvider<ServerCommandSource> suggest(List<String> suggestionList) {
        return (context, builder) -> {
            String option = context.getNodes().get(context.getNodes().size() - 1).getNode().getName();
            String[] input = context.getInput().split(" ");
            String original = null;
            String argument = null;

            // Necessary for secondary options (i.e. option:secondary)
            if (option.equals("add") || option.equals("remove")) {
                original = "clientOptions:" + option;
            }

            if (option.contains(":")) {
                original = option; // store original option
                option = option.split(":")[1];
            }

            // Do not suggest anything for improper options
            if (!all.contains(original != null ? original.split(":")[0] : option)) {
                return CompletableFuture.completedFuture(builder.build());
            }

            // Ensure that the last portion of the input is in fact an argument
            if (!input[input.length - 1].equals(option)) {
                argument = context.getArgument(original == null ? option : original, String.class);
            }

            for (String suggestion : suggestionList) {
                // If there is a partial match, or no argument, recommend option
                if (argument == null || suggestion.toLowerCase().contains(argument.toLowerCase())) {
                    builder.suggest(suggestion);
                }
            }

            return CompletableFuture.completedFuture(builder.build());
        };
    }
}
