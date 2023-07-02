package me.mgin.graves.block.utility;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.ExperienceType;
import net.minecraft.entity.player.PlayerEntity;

public class Experience {
    public static int calculatePlayerExperience(PlayerEntity player) {
        GameProfile profile = player.getGameProfile();
        float progress = player.experienceProgress;
        int level = player.experienceLevel;

        // Config settings
        GraveExpStoreType storageType = GravesConfig.resolve("expStorageType", profile);
        ExperienceType percentageType = GravesConfig.resolve("percentageType", profile);
        ExperienceType capType = GravesConfig.resolve("capType", profile);
        int cap = GravesConfig.resolve("cap", profile);
        int percentage = GravesConfig.resolve("percentage", profile);



        // Determine experience points based on configured type
        float percentageModifier = ((float) percentage / 100);
        int experience;

        if (percentageType == ExperienceType.LEVELS) {
            float levelAndProgress = (level + progress) * percentageModifier;
            level = (int) levelAndProgress;
            progress = levelAndProgress % 1;
        }

        switch (storageType) {
            case VANILLA -> {
                experience = calculateVanillaExperience(level);
            }
            case ALL -> {
                experience = calculateTotalExperience(level, progress);
            }
            default -> {
                experience = 0;
            }
        }

        // Adjust experience based on set percentage
        if (percentageType == ExperienceType.POINTS) experience = Math.round(experience * percentageModifier);

        // Return amount, enforcing level cap.
        int capValue = capType == ExperienceType.LEVELS ? calculateLevelExperience(cap) : cap;
        return cap > -1 ? Math.min(capValue, experience) : experience;
    }

    // This leverages the default death experience equation found here:
    // https://minecraft.fandom.com/wiki/Experience#Sources
    public static int calculateVanillaExperience(int level) {
        return 7 * level;
    }

    public static int calculateTotalExperience(int level, float progress) {
        int levelExperience = calculateLevelExperience(level);
        int progressExperience = calculateProgressExperience(level, progress);
        return levelExperience + progressExperience;
    }

    // This leverages the "total experience" equations found here:
    // https://minecraft.fandom.com/wiki/Experience#Leveling_up

    /**
     * Calculates how many points
     * @param level int
     * @return int
     */
    private static int calculateLevelExperience(int level) {
        int levelSquared = level * level;
        int levelExperience = 0;

        if (level <= 16)
            levelExperience = levelSquared + 6 * level;
        if (level >= 17 && level <= 31)
            levelExperience = (int) (2.5 * levelSquared - 40.5 * level + 360);
        if (level > 31)
            levelExperience = (int) (4.5 * levelSquared - 162.5 * level + 2220);

        return levelExperience;
    }

    /**
     * <p>
     *     Calculates how many points the given progress. This leverages the
     *     "experience required" equation found <a href="https://minecraft.fandom.com/wiki/Experience#Leveling_up">here</a>.
     * </p>
     *
     * @param level int
     * @param progress float
     * @return int
     */
    private static int calculateProgressExperience(int level, float progress) {
        float progressExperience = 0;

        if (level <= 15)
            progressExperience = (2 * level + 7) * progress;
        if (level >= 16 && level <= 30)
            progressExperience = (5 * level - 38) * progress;
        if (level > 30)
            progressExperience = (9 * level - 158) * progress;

        int result = Math.round(progressExperience);

        /*
         * The below conditional is in place to prevent an issue where Minecraft doesn't
         * quite reach the level it should.. i.e. 17 might become 16.999 and so forth. I
         * rather give 1 xp than have someone almost the level they were.
         */
        if (result == 0) result += 1;

        return level > 0 ? result : 0;
    }
}
