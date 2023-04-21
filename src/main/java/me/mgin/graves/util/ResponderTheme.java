package me.mgin.graves.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponderTheme {
    public static List<Formatting> prefix = new ArrayList<>(){{
        add(Formatting.DARK_GRAY);
    }};

    public static List<Formatting> success = new ArrayList<>(){{
        add(Formatting.GREEN);
    }};

    public static List<Formatting> info = new ArrayList<>(){{
        add(Formatting.YELLOW);
    }};

    public static List<Formatting> highlight = new ArrayList<>(){{
       add(Formatting.WHITE);
    }};

    public static List<Formatting> dim = new ArrayList<>(){{
        add(Formatting.DARK_GRAY);
    }};

    public static List<Formatting> error = new ArrayList<>(){{
        add(Formatting.RED);
    }};

    public static List<Formatting> hint = new ArrayList<>(){{
        add(Formatting.LIGHT_PURPLE);
    }};

    public static List<Formatting> strike = new ArrayList<>(){{
        add(Formatting.STRIKETHROUGH);
    }};

    public static Map<String, List<Formatting>> dimensions = new HashMap<>(){{
        put("minecraft:overworld", new ArrayList<>(){{
            add(Formatting.WHITE);
        }});

        put("minecraft:the_nether", new ArrayList<>(){{
            add(Formatting.RED);
        }});

        put("minecraft:the_end", new ArrayList<>(){{
            add(Formatting.LIGHT_PURPLE);
        }});
    }};

    public static Text styleBasedOnDim(Text message, String dimension) {
        List<Formatting> style = dimensions.get(dimension);

        for (Formatting format : style) {
            message = message.copy().formatted(format);
        }

        return message;
    };

    /**
     * Styles the message based on the given type.
     *
     * @param message Text
     * @param type String
     * @return Text
     */
    public static Text style(Text message, String type) {
        List<Formatting> style = getStyle(type);

        for (Formatting format : style) {
            message = message.copy().formatted(format);
        }

        return message;
    }

    /**
     * Retrieves the style list for the given type.
     *
     * @param type String
     * @return {@code List<Formatting>}
     */
    public static List<Formatting> getStyle(String type) {
        return switch(type) {
            case "prefix" -> prefix;
            case "success" -> success;
            case "info" -> info;
            case "highlight" -> highlight;
            case "hint" -> hint;
            case "dim" -> dim;
            case "error" -> error;
            case "strike" -> strike;
            default -> new ArrayList<>();
        };
    }
}
