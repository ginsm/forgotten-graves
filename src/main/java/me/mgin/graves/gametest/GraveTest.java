package me.mgin.graves.gametest;

import me.mgin.graves.gametest.tests.ExplosionTest;
import me.mgin.graves.gametest.tests.PlaceGraveTest;
import me.mgin.graves.gametest.tests.WaterlogTests;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GraveTest {
    @GameTest(templateName = "forgottengraves:placement_tests")
    public void gravePlacementTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();

        GraveTestHelper.runCommand(context, "graves server config set sinkInWater false");
        GraveTestHelper.runCommand(context, "graves server config set sinkThroughBlocks false");
        GraveTestHelper.runCommand(context, "graves server config set sinkInAir false");
        GraveTestHelper.runCommand(context, "graves server config set replaceBlocks false");
        PlaceGraveTest.sinkInLava$false(context, player);
        PlaceGraveTest.sinkInWater$false(context, player);
        PlaceGraveTest.sinkInAir$false(context, player);
        PlaceGraveTest.sinkThroughBlocks$false(context, player); // water column
        PlaceGraveTest.replaceBlocks$false(context, player); // tall grass in air chamber

        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.runCommand(context, "graves server config set sinkInLava true");
        PlaceGraveTest.sinkInLava$true(context, player);
        PlaceGraveTest.sinkInWater$true(context, player);
        PlaceGraveTest.sinkThroughBlocks$true(context, player); // water column
        PlaceGraveTest.sinksInAir$true(context, player);
        PlaceGraveTest.replaceBlocks$true(context, player); // tall grass in air chamber

        PlaceGraveTest.spawnsInNether(context, player);
        PlaceGraveTest.spawnsInEnd(context, player);

        PlaceGraveTest.respectsBlacklist(context, player);
        PlaceGraveTest.respectsWorldBoundaries(context, player);

        GraveTestHelper.runCommand(context, "graves server config set graves false");
        PlaceGraveTest.graves$false(context, player);
        GraveTestHelper.runCommand(context, "graves server config set graves true");
        PlaceGraveTest.respectsDisableEffect(context, player);
        GraveTestHelper.runCommand(context, "graves server config set disableInPvP true");
        PlaceGraveTest.disableInPvP$true(context, player);

        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();
    }

    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void waterlogTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        WaterlogTests.waterlogged(context, player, pos);
        WaterlogTests.notWaterlogged(context, player, pos);

        GraveTestHelper.runCommand(context, "graves server config reset");
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        context.complete();
    }

    // NOTE - explosion tests
    @GameTest(templateName = "forgottengraves:generic_tests")
    public static void explosionTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));

        GraveTestHelper.runCommand(context, "graves server config reset");
        ExplosionTest.resistsCreeper(context, player, pos);
        ExplosionTest.resistsTNT(context, player, pos);
        ExplosionTest.resistsEndCrystal(context, player, pos);
        ExplosionTest.resistsGhastFireball(context, player, pos);
        ExplosionTest.resistsDragonFireball(context, player, pos);

        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        GraveTestHelper.runCommand(context, "graves server config reset");
        context.complete();
    }
}
