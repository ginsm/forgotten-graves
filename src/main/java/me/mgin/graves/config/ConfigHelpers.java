package me.mgin.graves.config;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;

import me.mgin.graves.Graves;
import me.mgin.graves.networking.ConfigNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

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
     * Transmits JSON-formatted config data to the (dedicated) server. The data will
     * not be transmitted to integrated servers (singleplayer).
     */
    public void storeOnServer() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Do not send a packet whilst in the menus
        if (client.world == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(this.serialize());
        ClientPlayNetworking.send(ConfigNetworking.STORE_CONFIG_C2S, buf);
    }

    /**
     * Resolves whether the option is being handled by the client or server and
     * returns the appropriate GravesConfig instance.
     *
     * @param option String
     * @param profile GameProfile
     * @return GravesConfig
     */
    public static GravesConfig resolveConfig(String option, GameProfile profile) {
        GravesConfig config = GravesConfig.getConfig();

        if (config.server.clientOptions.contains(option)) {
            GravesConfig clientConfig = Graves.clientConfigs.get(profile);

            if (clientConfig != null) {
                return clientConfig;
            }
        }

        return config;
    }

    /**
     * Dynamically set a field based on option name.
     *
     * @param option String
     * @param value  Object
     */
    public void setDynamicField(String option, Object value) {
        try {
            Field target = determineSubClass(option);
            Field field = Objects.requireNonNull(target).getType().getDeclaredField(option);
            field.set(target.get(this), value);
        } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static private Field determineSubClass(String option) throws NoSuchFieldException {
        for (String subclass : ConfigOptions.subclass) {
            if (ConfigOptions.getSubclass(subclass).contains(option))
                return GravesConfig.class.getDeclaredField(subclass);
        }
        return null;
    }
}
