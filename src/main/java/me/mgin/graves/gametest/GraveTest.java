package me.mgin.graves.gametest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class GraveTest {
    @GameTest(templateName = "forgottengraves:grave_generation")
    public void gravePlacementTests(TestContext context) {
        PlayerEntity player = context.createMockSurvivalPlayer();

        GraveTestHelper.runCommand(context, "graves server config reset");
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
}
