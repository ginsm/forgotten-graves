package me.mgin.graves.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Compatibility layer for GameProfile and NbtHelper operations that have changed in Minecraft 1.20.5
 */
public class ProfileCompat {
    private static boolean loggedClassNotFound = false;
    private static boolean loggedConstructorError = false;
    private static boolean loggedGeneralError = false;

    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        try {
            try {
                Method writeGameProfile = NbtHelper.class.getMethod("writeGameProfile", NbtCompound.class, GameProfile.class);
                return (NbtCompound) writeGameProfile.invoke(null, nbt, profile);
            } catch (NoSuchMethodException e) {
                if (profile != null) {
                    if (profile.getId() != null) {
                        nbt.putUuid("Id", profile.getId());
                    }
                    if (profile.getName() != null) {
                        nbt.putString("Name", profile.getName());
                    }
                }
                return nbt;
            }
        } catch (Exception e) {
            System.err.println("Error writing GameProfile: " + e.getMessage());
            return nbt;
        }
    }

    public static GameProfile toGameProfile(NbtCompound nbt) {
        try {
            try {
                Method toGameProfile = NbtHelper.class.getMethod("toGameProfile", NbtCompound.class);
                return (GameProfile) toGameProfile.invoke(null, nbt);
            } catch (NoSuchMethodException e) {
                if (nbt.contains("Id") && nbt.contains("Name")) {
                    UUID uuid = nbt.getUuid("Id");
                    String name = nbt.getString("Name");
                    return new GameProfile(uuid, name);
                }
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error reading GameProfile: " + e.getMessage());
            return null;
        }
    }

    public static Object toProfileComponent(GameProfile profile) {
        if (profile == null || profile.getId() == null) {
            return null;
        }

        try {
            Class<?> profileComponentClass;
            try {
                profileComponentClass = Class.forName("net.minecraft.component.type.ProfileComponent");
            } catch (ClassNotFoundException e) {
                if (!loggedClassNotFound) {
                    System.err.println("ProfileComponent class not found: " + e.getMessage());
                    loggedClassNotFound = true;
                }
                return null;
            }

            try {
                Constructor<?> constructor = profileComponentClass.getConstructor(GameProfile.class);
                return constructor.newInstance(profile);
            } catch (Exception e) {
                if (!loggedConstructorError) {
                    System.err.println("Error creating ProfileComponent with GameProfile constructor: " + e.getMessage());
                    loggedConstructorError = true;
                }
            }

            return null;

        } catch (Exception e) {
            if (!loggedGeneralError) {
                System.err.println("Error creating ProfileComponent: " + e.getMessage());
                loggedGeneralError = true;
            }
            return null;
        }
    }
}
