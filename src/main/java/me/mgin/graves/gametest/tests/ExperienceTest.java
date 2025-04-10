package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.utility.Experience;
import me.mgin.graves.gametest.GraveTestHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class ExperienceTest {
    public static void storeAll(TestContext context, PlayerEntity player, BlockPos pos) {
        GraveTestHelper.runCommand(context, "graves server config reset");
        System.out.println("📗 Running STORE_ALL XP L0-16");
        player.addExperienceLevels(14);
        player.addExperience(5);
        runExperienceTest(context, player, pos, 14, 5);

        System.out.println("📗 Running STORE_ALL XP L17-31");
        player.addExperienceLevels(18);
        player.addExperience(10);
        runExperienceTest(context, player, pos, 18, 10);

        System.out.println("📗 Running STORE_ALL XP L32+");
        player.addExperienceLevels(34);
        player.addExperience(27);
        runExperienceTest(context, player, pos, 34, 27);

        System.out.println("📗 Running STORE_ALL XP (No Points)");
        player.addExperienceLevels(32);
        runExperienceTest(context, player, pos, 32, 0);
    }

    public static void storeVanilla(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running VANILLA XP");
        GraveTestHelper.runCommand(context, "graves server config set expStorageType VANILLA");
        player.addExperienceLevels(45);
        player.addExperience(8);
        runExperienceTest(context, player, pos, 15, 0);

        System.out.println("📗 Running VANILLA XP (Point Cap)");
        GraveTestHelper.runCommand(context, "graves server config set cap 100");
        GraveTestHelper.runCommand(context, "graves server config set capType POINTS");
        player.addExperienceLevels(45);
        player.addExperience(8);
        runExperienceTest(context, player, pos, 7, 9);
    }

    public static void storeNone(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running NONE XP");
        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.runCommand(context, "graves server config set expStorageType NONE");
        player.addExperienceLevels(45);
        player.addExperience(8);
        runExperienceTest(context, player, pos, 0, 0);
    }

    public static void percentage(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running Percentage XP (Points)");
        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.runCommand(context, "graves server config set percentage 70");
        player.addExperienceLevels(45);
        player.addExperience(8);
        runExperienceTest(context, player, pos, 39, 92);

        System.out.println("📗 Running Percentage XP (Levels)");
        GraveTestHelper.runCommand(context, "graves server config set percentageType LEVELS");
        player.addExperienceLevels(45);
        player.addExperience(8);
        runExperienceTest(context, player, pos, 31, 65);
    }

    public static void cap(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running Cap XP (Levels)");
        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.runCommand(context, "graves server config set cap 30");
        player.addExperienceLevels(100);
        player.addExperience(100);
        runExperienceTest(context, player, pos, 30, 0);
    }


    // NOTE - Helper functions
    private static void resetPlayerExperience(PlayerEntity player) {
        player.totalExperience = 0;
        player.experienceLevel = 0;
        player.experienceProgress = 0;
    }

    private static int getPlayerExperiencePoints(PlayerEntity player) {
        return (int) Math.floor(player.getNextLevelExperience() * player.experienceProgress);
    }

    private static void runExperienceTest(TestContext context, PlayerEntity player, BlockPos pos,
                                         int expectedLevel, int expectedPoints) {
        // Calculate experience using the Experience class
        int[] experience = Experience.calculatePlayerExperience(player);

        // Swap the player's experience over to what would've been in the grave
        resetPlayerExperience(player);
        player.addExperienceLevels(experience[0]);
        player.addExperience(experience[1]);

        // Get the player's current amount of experience points
        int playerExperiencePoints = getPlayerExperiencePoints(player);

        // Check if player has the expected level and points (based on config options)
        context.assertTrue(
            player.experienceLevel == expectedLevel && playerExperiencePoints == expectedPoints,
            String.format(
                "Player wasn't %dL %dP on retrieval, got: %dL %dP.",
                expectedLevel, expectedPoints, player.experienceLevel, playerExperiencePoints
            )
        );

        // Reset experience for next test
        resetPlayerExperience(player);
    }
}
