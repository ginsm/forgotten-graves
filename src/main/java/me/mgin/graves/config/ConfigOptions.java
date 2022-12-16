package me.mgin.graves.config;

import java.util.HashSet;
import java.util.Set;

public class ConfigOptions {
    final static public Set<String> all = new HashSet<>();
    final static public Set<String> getSubclass(String subclass) {
        return switch (subclass) {
            case "main" -> main;
            case "itemDecay" -> itemDecay;
            case "server" -> server;
            default -> throw new IllegalStateException("Unexpected value: " + subclass);
        };
    }

    final static public Set<String> subclass = new HashSet<>() {{
        add("main");
        add("itemDecay");
        add("server");
    }};

    final static public Set<String> main = new HashSet<>() {{
        // MainSettings
        add("graves");
        add("graveCoordinates");
        add("retrievalType");
        add("dropType");
        add("expStorageType");
        add("maxCustomXPLevel");
    }};

    final static public Set<String> itemDecay = new HashSet<>() {{
        // ItemDecaySettings
        add("decayModifier");
        add("decayBreaksItems");
    }};

    final static public Set<String> server = new HashSet<>() {{
        // ServerSettings
        add("graveRobbing");
        add("OPOverrideLevel");
        add("clientOptions");
    }};

    final static public Set<String> enums = new HashSet<>() {{
        add("dropType");
        add("retrievalType");
        add("expStorageType");
    }};

    static {
        all.addAll(main);
        all.addAll(itemDecay);
        all.addAll(server);
    }
}
