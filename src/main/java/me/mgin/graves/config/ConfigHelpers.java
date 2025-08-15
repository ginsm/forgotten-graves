package me.mgin.graves.config;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.shedaniel.autoconfig.AutoConfig;

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

    public GravesConfig resetConfig() {
        AutoConfig.getConfigHolder(GravesConfig.class).resetToDefault();
        return getConfig();
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

        // Switches to the client config if applicable.
        if (config.server.clientOptions.contains(option) && Graves.clientConfigs.containsKey(profile)) {
            GravesConfig clientConfig = Graves.clientConfigs.get(profile);
            if (clientConfig != null) config = clientConfig;
        }

        return ConfigOptions.getOptionValue(config, option);
    }
}
