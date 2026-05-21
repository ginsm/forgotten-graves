package me.mgin.graves.compat;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.lang.reflect.Method;

/**
 * Compatibility layer for World operations that have changed in Minecraft 1.20.5
 */
public class WorldCompat {

    public static String getDimensionKey(World world) {
        try {
            try {
                Method getDimensionKey = world.getClass().getMethod("getDimensionKey");
                Object dimensionKey = getDimensionKey.invoke(world);
                Method getValue = dimensionKey.getClass().getMethod("getValue");
                Object identifier = getValue.invoke(dimensionKey);
                return identifier.toString();
            } catch (NoSuchMethodException e) {
                try {
                    Method getDimension = world.getClass().getMethod("getDimension");
                    Object dimension = getDimension.invoke(world);
                    Method getIdentifier = dimension.getClass().getMethod("getIdentifier");
                    Identifier identifier = (Identifier) getIdentifier.invoke(dimension);
                    return identifier.toString();
                } catch (NoSuchMethodException ex) {
                    Method getRegistryKey = world.getClass().getMethod("getRegistryKey");
                    Object registryKey = getRegistryKey.invoke(world);
                    Method getValue = registryKey.getClass().getMethod("getValue");
                    Identifier identifier = (Identifier) getValue.invoke(registryKey);
                    return identifier.toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting dimension key: " + e.getMessage());
            return "minecraft:overworld";
        }
    }
}
