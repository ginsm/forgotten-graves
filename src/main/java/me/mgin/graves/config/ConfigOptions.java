package me.mgin.graves.config;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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
        // Get subclasses
        for (Class<?> clazz : GravesConfig.class.getClasses()) {
            String name = clazz.getSimpleName();
            List<String> fieldNames = new ArrayList<>();

            for (Field field : clazz.getDeclaredFields()) {
                // Check if field is an enum
                Type type = field.getGenericType();
                if (type instanceof Class && ((Class<?>)type).isEnum()) {
                    // Store enum name and values (in string format)
                    enums.put(field.getName(),
                        Arrays.stream(((Class<?>) type).getEnumConstants()).map(Object::toString).toList()
                    );
                }

                // Add field name to list
                fieldNames.add(field.getName());
            }

            // Add subclass name to list
            subclass.add(name);

            // Add the field names to options and all lists
            options.put(clazz.getSimpleName(), fieldNames);
            all.addAll(fieldNames);
        }
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
