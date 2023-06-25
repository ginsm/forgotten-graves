package me.mgin.graves.command.utility;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.util.ArrayUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mgin.graves.config.ConfigOptions.convertStringToEnum;

public class CommandContextData {
    public Object VAL;
    public String TYPE;
    public String OPTION;
    public String ACTION = null;
    public boolean IS_SERVER;

    public boolean SEND_COMMAND_FEEDBACK;

    /**
     * Parses the context to gather argument data (type, option, value) and
     * whether to send command feedback or not.
     *
     * @param context CommandContext.ServerCommandSource
     */
    public CommandContextData(CommandContext<ServerCommandSource> context) {
        // Get value, option, and type
        this.TYPE = determineArgumentType(context);
        this.OPTION = determineOptionName(context, this.TYPE.equals("literal"));
        this.VAL = getArgumentValue(context, this.TYPE, this.OPTION);

        // Determine whether command feedback should be sent or not
        this.SEND_COMMAND_FEEDBACK = context.getSource()
            .getWorld()
            .getGameRules()
            .getBoolean(GameRules.SEND_COMMAND_FEEDBACK);

        // Determine whether the command is meant to be run on the server or client
        String[] input = context.getInput().split(" ");
        this.IS_SERVER = input[ArrayUtil.indexOf(input, "graves") + 1].equals("server");

        // This is largely used by clientOptions currently.
        if (this.OPTION.contains(":")) {
            String[] options = this.OPTION.split(":");
            this.OPTION = options[0];
            this.ACTION = options[1];
        }
    }

    /**
     * Serializes this CommandContextData instance.
     *
     * @return String
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Deserializes a JSONed instance of CommandContextData
     *
     * @param instance String
     * @return CommandContextData
     */
    public static CommandContextData deserialize(String instance) {
        Gson gson = new Gson();
        CommandContextData result = gson.fromJson(instance, CommandContextData.class);
        if (result.VAL == null) return result;

        // Deserialize enum values properly
        if (ConfigOptions.enums.containsKey(result.OPTION)) {
            result.VAL = convertStringToEnum(result.OPTION, (String) result.VAL);
            return result;
        }

        // Deserialize numbers to int
        if (result.VAL.getClass().equals(Double.class)) {
            result.VAL = Double.valueOf((double) result.VAL).intValue();
        }

        return result;
    }

    /**
     * Parses the last node of context.getNodes() to determine argument type.
     *
     * @param context CommandContext.ServerCommandSource
     * @return String
     */
    private String determineArgumentType(CommandContext<ServerCommandSource> context) {
        String node = context.getNodes().get(context.getNodes().size() - 1).toString();
        Pattern pattern = Pattern.compile("(integer|BoolArgumentType|string|literal)");
        Matcher matcher = pattern.matcher(node);

        if (!matcher.find()) throw new IllegalStateException("Unknown Argument Type: " + node);

        return matcher.group();
    }

    /**
     * Retrieves the option name from the argument node.
     *
     * @param context CommandContext.ServerCommandSource
     * @param literal boolean
     * @return String
     */
    private String determineOptionName(CommandContext<ServerCommandSource> context, boolean literal) {
        // Commands with type "literal" need to look at the input in order to
        // derive the config option's name.
        if (literal) {
            String[] input = context.getInput().split(" ");
            return input[ArrayUtil.indexOf(input, "set") + 1];
        }

        // Gathers the option name from the argument name.
        List<ParsedCommandNode<ServerCommandSource>> nodes = context.getNodes();
        return nodes.get(nodes.size() - 1).getNode().getName();
    }

    /**
     * Gets the argument value based on type and option.
     *
     * @param context CommandContext.ServerCommandSource
     * @param type    String
     * @param option  String
     * @return Object
     */
    private Object getArgumentValue(CommandContext<ServerCommandSource> context, String type, String option) {
        String[] input = context.getInput().split(" ");
        return switch (type) {
            case "BoolArgumentType" -> context.getArgument(option, Boolean.class);
            case "integer" -> context.getArgument(option, Integer.class);
            case "literal" -> input[input.length - 1];
            case "string" -> {
                String value = context.getArgument(option, String.class);
                if (ConfigOptions.enums.containsKey(option)) yield convertStringToEnum(option, value);
                yield value;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
