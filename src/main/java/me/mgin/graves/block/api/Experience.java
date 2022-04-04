package me.mgin.graves.block.api;

import com.mojang.authlib.GameProfile;

import me.mgin.graves.config.GraveExpStoreType;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.player.PlayerEntity;

public class Experience {
	public static int calculatePlayerExperience(PlayerEntity player) {
		int level = player.experienceLevel;
		float progress = player.experienceProgress;
		GameProfile profile = player.getGameProfile();

		GraveExpStoreType expStorageType = GravesConfig.resolveConfig("expStorageType", profile).main.expStorageType;
		int maxCustomXPLevel = GravesConfig.resolveConfig("maxCustomXPLevel", profile).main.maxCustomXPLevel;

		switch (expStorageType) {
			case STORE_ALL_XP :
				return calculateTotalExperience(level, progress);
			case STORE_DEFAULT_XP :
				return calculateDefaultExperience(level);
			case STORE_CUSTOM_XP :
				// Enforce a minimum threshold (0).
				int maxLevel = Math.max(maxCustomXPLevel, 0);
				return calculateCustomExperience(level, maxLevel);
			default :
				return calculateDefaultExperience(level);
		}
	}

	public static int calculateTotalExperience(int level, float progress) {
		int levelExperience = calculateLevelExperience(level);
		int progressExperience = calculateProgressExperience(level, progress);
		return levelExperience + progressExperience;
	}

	// This leverages the default death experience equation found here:
	// https://minecraft.fandom.com/wiki/Experience#Sources
	public static int calculateDefaultExperience(int level) {
		return Math.min(7 * level, 100);
	}

	// This function mimics the above one but allows for a custom maximum level,
	// i.e. 30.
	public static int calculateCustomExperience(int level, int maxLevel) {
		int maximumExperiencePoints = maxLevel > 0 ? calculateLevelExperience(maxLevel) : 0;
		return Math.min(7 * level, maximumExperiencePoints);
	}

	// This leverages the "total experience" equations found here:
	// https://minecraft.fandom.com/wiki/Experience#Leveling_up
	private static int calculateLevelExperience(int level) {
		int levelSquared = level * level;
		int levelExperience = 0;

		if (level <= 16)
			levelExperience = (int) (levelSquared + 6 * level);
		if (level >= 17 && level <= 31)
			levelExperience = (int) (2.5 * levelSquared - 40.5 * level + 360);
		if (level > 31)
			levelExperience = (int) (4.5 * levelSquared - 162.5 * level + 2220);

		return levelExperience;
	}

	// This leverages the "experience required" equation found here:
	// https://minecraft.fandom.com/wiki/Experience#Leveling_up
	private static int calculateProgressExperience(int level, float progress) {
		float progressExperience = 0;

		if (level <= 15)
			progressExperience = (int) ((2 * level + 7) * progress);
		if (level >= 16 && level <= 30)
			progressExperience = (int) ((5 * level - 38) * progress);
		if (level > 30)
			progressExperience = (int) ((9 * level - 158) * progress);

		/*
		 * The below conditional is in place to prevent an issue where Minecraft doesn't
		 * quite reach the level it should.. i.e. 17 might become 16.999 and so forth. I
		 * rather give 1 xp than have someone almost the level they were.
		 */
		int result = (int) Math.ceil(progressExperience);
		return (result > 0) ? result : (level > 0) ? 1 : 0;
	}
}
