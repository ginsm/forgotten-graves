package me.mgin.graves.util;

public class ExperienceCalculator {
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
		int maximumExperiencePoints = calculateLevelExperience(maxLevel);
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

		return levelExperience + 1; // without the extra point, lv 17 becomes 16.999999999 etc; this pushes it over
									// to 17
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

		return (int) Math.round(progressExperience);
	}
}
