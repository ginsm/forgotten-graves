package me.mgin.graves.config;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigOptions {
    static public Set<String> all = new HashSet<>();

    static public Set<String> subclass = new HashSet<>() {{
        add("main");
        add("itemDecay");
        add("server");
        add("floating");
    }};

    static public Set<String> main = new HashSet<>() {{
        // MainSettings
        add("graves");
        add("graveCoordinates");
        add("retrievalType");
        add("dropType");
        add("expStorageType");
        add("maxCustomXPLevel");
    }};

    static public Set<String> itemDecay = new HashSet<>() {{
        // ItemDecaySettings
        add("decayModifier");
        add("decayBreaksItems");
    }};

    static public Set<String> server = new HashSet<>() {{
        // ServerSettings
        add("graveRobbing");
        add("OPOverrideLevel");
        add("storedGravesAmount");
        add("destructiveDeleteCommand");
        add("clientOptions");
    }};

    static public Set<String> floating = new HashSet<>() {{
        // FloatingSettings
        add("floatInAir");
        add("floatInWater");
        add("floatInLava");
    }};

    // Create a HashSet of all valid options.
    static {
        all.addAll(main);
        all.addAll(itemDecay);
        all.addAll(floating);
        all.addAll(server);
    }

    static public Set<String> enums = new HashSet<>() {{
        add("dropType");
        add("retrievalType");
        add("expStorageType");
    }};

    // Enum options
    static public Set<String> dropType = Stream.of(GraveDropType.values()).map(Enum::name).collect(Collectors.toSet());
    static public Set<String> retrievalType = Stream.of(GraveRetrievalType.values()).map(Enum::name).collect(Collectors.toSet());
    static public Set<String> expStorageType = Stream.of(GraveExpStoreType.values()).map(Enum::name).collect(Collectors.toSet());

    public static Set<String> getSubclass(String subclass) {
        return switch (subclass) {
            case "main" -> main;
            case "itemDecay" -> itemDecay;
            case "server" -> server;
            case "floating" -> floating;
            default -> throw new IllegalStateException("Unexpected value: " + subclass);
        };
    }

    @SafeVarargs
    public static Set<String> buildSet(Set<String>... sets) {
        Set<String> result = new HashSet<>();
        for (Set<String> set : sets) {
            result.addAll(set);
        }
        return result;
    }

    public static boolean validEnumValue(String option, String value) {
        return switch (option) {
            case "dropType" -> dropType.contains(value);
            case "retrievalType" -> retrievalType.contains(value);
            case "expStorageType" -> expStorageType.contains(value);
            default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + value);
        };
    }

    public static SuggestionProvider<ServerCommandSource> suggest(Set<String> suggestionList) {
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
