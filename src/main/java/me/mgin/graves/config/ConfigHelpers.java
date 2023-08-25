package me.mgin.graves.config;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.shedaniel.autoconfig.AutoConfig;

import java.lang.reflect.Field;
import java.util.Objects;

public class ConfigHelpers {
    /**
     * Converts the GravesConfig instance into a string.
     *
     * @return Serialized GravesConfig
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Convert a serialized config to a GravesConfig instance.
     *
     * @param config Serialized GravesConfig
     * @return GravesConfig instance
     */
    public static GravesConfig deserialize(String config) {
        Gson gson = new Gson();
        return gson.fromJson(config, GravesConfig.class);
    }

    /**
     * Set the config instance
     */
    public static void setConfig(GravesConfig config) {
        AutoConfig.getConfigHolder(GravesConfig.class).setConfig(config);
    }

    public static GravesConfig getConfig() {
        return AutoConfig.getConfigHolder(GravesConfig.class).getConfig();
    }

    /**
     * Save the config instance
     */
    public void save() {
        AutoConfig.getConfigHolder(GravesConfig.class).save();
    }

    /**
     * Reload the config instance
     */
    public void reload() {
        AutoConfig.getConfigHolder(GravesConfig.class).load();
    }

    /**
     * Resolves the value of the passed option; respects client option handling.
     *
     * @param option String
     * @param profile GameProfile
     * @return T
     */
    public static <T> T resolve(String option, GameProfile profile) {
        GravesConfig config = GravesConfig.getConfig();

        // Switches to the client config if applicable
        if (config.server.clientOptions.contains(option)) {
            config = Graves.clientConfigs.get(profile);
        }

        try {
            Field subclass = determineSubClass(option);
            if (subclass != null) {
                Field field = subclass.getType().getDeclaredField(option);
                Object result = field.get(subclass.get(config));
                return (T) result;
            }
        } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Worse case, throw an error.
        throw new RuntimeException("Something went wrong trying to access " + option);
    }

    /**
     * Dynamically set a field based on option name.
     *
     * @param option String
     * @param value  Object
     */
    public void setDynamicField(String option, Object value) {
        try {
            Field subclass = determineSubClass(option);
            Field field = Objects.requireNonNull(subclass).getType().getDeclaredField(option);
            field.set(subclass.get(this), value);
        } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static private Field determineSubClass(String option) throws NoSuchFieldException {
        for (String subclass : ConfigOptions.subclass) {
            if (ConfigOptions.options.get(subclass).contains(option))
                return GravesConfig.class.getDeclaredField(subclass);
        }
        return null;
    }
}
