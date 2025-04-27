package me.mgin.graves.gametest;

import me.mgin.graves.gametest.tests.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GraveTest {
    @GameTest(templateName = "forgottengraves:placement_tests")
    public void graveGenerationTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();

        GraveTestHelper.printTestStarting("Generation");

        PlaceGraveTest.sinkInLava$false(context, player); // default is false
        PlaceGraveTest.sinkInWater$false(context, player);
        PlaceGraveTest.sinkThroughBlocks$false(context, player); // tested with a water column
        PlaceGraveTest.sinkInAir$false(context, player);
        PlaceGraveTest.replaceBlocks$false(context, player); // tested with tall grass in air chamber

        PlaceGraveTest.sinkInLava$true(context, player);
        PlaceGraveTest.sinkInWater$true(context, player);
        PlaceGraveTest.sinkThroughBlocks$true(context, player); // water column
        PlaceGraveTest.sinksInAir$true(context, player);
        PlaceGraveTest.replaceBlocks$true(context, player); // tall grass in air chamber

        PlaceGraveTest.spawnsInNether(context, player);
        PlaceGraveTest.spawnsInEnd(context, player);

        PlaceGraveTest.respectsBlacklist(context, player);
        PlaceGraveTest.respectsWorldBoundaries(context, player);

        PlaceGraveTest.graves$false(context, player);
        PlaceGraveTest.respectsDisableEffect(context, player);
        PlaceGraveTest.disableInPvP$true(context, player);

        // Complete test
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();

        GraveTestHelper.printTestEnding("Generation");
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void RetrieveGraveTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Retrieve");

        // Remove the grave in the center of the generic test platform
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

        RetrieveGraveTest.basicRetrieval(context, player, pos);
        RetrieveGraveTest.mergeRetrieval(context, player, pos);
        RetrieveGraveTest.overflowRetrieval(context, player, pos);
        RetrieveGraveTest.unloadedModRetrieval(context, player, pos);

        // Complete test
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();

        GraveTestHelper.printTestEnding("Retrieve");
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void decayTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Decay");

        DecayTest.decayEnabled$true(context, player, pos);
        DecayTest.decayEnabled$false(context, player, pos);
        DecayTest.honeycombPreventsDecay(context, player, pos);
        DecayTest.shovelRemovesHoneycomb(context, player, pos);
        DecayTest.decayItemsAddDecay(context, player, pos); // vines, mushrooms, etc
        DecayTest.shovelReducesDecayStage(context, player, pos);
        DecayTest.itemsDecay(context, player, pos);
        DecayTest.decayBreaksItems$true(context, player, pos);
        DecayTest.minStageTimeSeconds(context, player, pos);
        DecayTest.maxStageTimeSeconds(context, player, pos);

        // Complete test
        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        context.complete();

        GraveTestHelper.printTestEnding("Decay");
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void experienceTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Experience");

        // Remove the grave in the center of the generic test platform
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

        ExperienceTest.storeAll(context, player, pos);
        ExperienceTest.storeVanilla(context, player, pos);
        ExperienceTest.storeNone(context, player, pos);
        ExperienceTest.percentage(context, player, pos);
        ExperienceTest.cap(context, player, pos);

        // Complete test
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();

        GraveTestHelper.printTestEnding("Experience");
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void waterlogTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Waterlog");

        WaterlogTest.waterlogged(context, player, pos);
        WaterlogTest.notWaterlogged(context, player, pos);

        // Complete test
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        context.complete();

        GraveTestHelper.printTestEnding("Waterlog");
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void explosionTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Explosion");

        ExplosionTest.resistsCreeper(context, player, pos);
        ExplosionTest.resistsTNT(context, player, pos);
        ExplosionTest.resistsEndCrystal(context, player, pos);
        ExplosionTest.resistsGhastFireball(context, player, pos);
        ExplosionTest.resistsDragonFireball(context, player, pos);

        // Complete test
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();

        GraveTestHelper.printTestEnding("Explosion");
    }

    // NOTE - permission test
    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void permissionTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.printTestStarting("Permission");

        // Remove the grave in the center of the generic test platform
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

        PermissionTest.noAccess(context, player, pos);
        PermissionTest.graveRobbing(context, player, pos);
        PermissionTest.ownedGrave(context, player, pos);
        PermissionTest.decayRobbing(context, player, pos);

        // Complete test
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();

        GraveTestHelper.printTestEnding("Permission");
    }
}
