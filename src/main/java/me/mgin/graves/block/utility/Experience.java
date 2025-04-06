package me.mgin.graves.block.utility;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.ExperienceType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;

public class Experience {
    public static int[] calculatePlayerExperience(PlayerEntity player) {
        GraveExpStoreType storageType = GravesConfig.resolve("expStorageType", player.getGameProfile());

        int[] experience = new int[]{0,0};
        switch(storageType) {
            case ALL -> experience = getCurrentExperience(player);
            case VANILLA -> experience = getVanillaExperience(player);
            case NONE -> {
                return new int[]{0,0};
            }
        }

        return applyConfigurationOptions(player, experience[0], experience[1]);
    }

    public static int[] getCurrentExperience(PlayerEntity player) {
        return new int[]{
            player.experienceLevel,
            getPlayerExperiencePoints(player)
        };
    }

    public static int[] getVanillaExperience(PlayerEntity player) {
        int vanillaExperience = 7 * player.experienceLevel;
        return calculateLevelAndPoints(vanillaExperience, player);
    }

    public static int[] applyConfigurationOptions(PlayerEntity player, int startingLevel, int startingPoints) {
        // This is used for configured client options
        GameProfile profile = player.getGameProfile();

        // Get percentage settings
        int percentage = GravesConfig.resolve("percentage", profile);
        ExperienceType percentageType = GravesConfig.resolve("percentageType", profile);
        float percentageModifier = percentage / 100f;

        // Get cap settings
        int cap = GravesConfig.resolve("cap", profile);
        ExperienceType capType = GravesConfig.resolve("capType", profile);

        // Initialize result
        int[] result = new int[]{startingLevel, startingPoints};

        // Apply percentage modifier
        if (100 > percentage) {
            if (percentageType == ExperienceType.LEVELS) {
                float adjustedLevel = startingLevel * percentageModifier;
                int level = (int) Math.floor(adjustedLevel);
                float progress = adjustedLevel - level;

                // Calculate points based on progress
                int originalLevel = player.experienceLevel;
                player.experienceLevel = level;
                int points = (int) Math.floor(player.getNextLevelExperience() * progress);
                player.experienceLevel = originalLevel;

                // Add proportional points from the original level
                if (startingPoints > 0) {
                    float pointPercentage = startingPoints / (float) getNextLevelExperience(level, player);
                    points += (int) Math.floor(pointPercentage * percentageModifier * getNextLevelExperience(level, player));
                }

                result = new int[]{level, points};
            } else {
                // POINTS type - convert to total XP, apply percentage, convert back
                long totalExperience = calculateTotalExperience(startingLevel, startingPoints, player);
                long adjustedExperience = (long) Math.floor(totalExperience * percentageModifier);
                result = calculateLevelAndPoints(adjustedExperience, player);
            }
        }


        // Apply cap if applicable
        if (cap > -1) {
            if (capType == ExperienceType.LEVELS) {
                if (result[0] > cap) {
                    return new int[]{cap, 0};
                }
            } else {
                long totalExperience = calculateTotalExperience(result[0], result[1], player);
                if (totalExperience > cap) {
                    return calculateLevelAndPoints(cap, player);
                }
            }
        }

        return result;
    }


    // NOTE - Helper functions
    /**
     * Gets the current experience points for the current level's progress.
     */
    private static int getPlayerExperiencePoints(PlayerEntity player) {
        return (int) Math.floor(player.getNextLevelExperience() * player.experienceProgress);
    }

    /**
     * Gets the XP required for the next level while respecting the player's current level.
     */
    private static int getNextLevelExperience(int level, PlayerEntity player) {
        int originalLevel = player.experienceLevel;
        player.experienceLevel = level;
        int experienceRequired = player.getNextLevelExperience();
        player.experienceLevel = originalLevel;
        return experienceRequired;
    }

    /**
     * Converts the level and points using points alone (respecting custom scaling mods).
     */
    private static int[] calculateLevelAndPoints(long totalExperience, PlayerEntity player) {
        int level = 0;
        long points = totalExperience;

        // Store original level to restore later
        int originalLevel = player.experienceLevel;

        // Find the highest complete level
        while (true) {
            player.experienceLevel = level;
            int experienceForNextLevel = player.getNextLevelExperience();

            if (points >= experienceForNextLevel) {
                points -= experienceForNextLevel;
                level++;
            } else {
                break; // Break the loop once the points are unable to increase the level
            }
        }

        // Restore original level
        player.experienceLevel = originalLevel;

        // Return int array containing level and points
        return new int[]{level, (int) points};
    }

    /**
     * Calculates the total experience points from a level and points value (respecting custom scaling mods).
     */
    private static long calculateTotalExperience(int level, int points, PlayerEntity player) {
        long totalExperience = 0;

        // Calculate XP for each full level
        int originalLevel = player.experienceLevel;
        for (int i = 0; i < level; i++) {
            player.experienceLevel = i;
            totalExperience += player.getNextLevelExperience();
        }
        player.experienceLevel = originalLevel; // Restore original level

        // Add remaining points
        totalExperience += points;

        return totalExperience;
    }
}