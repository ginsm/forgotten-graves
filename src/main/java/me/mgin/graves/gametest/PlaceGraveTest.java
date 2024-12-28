package me.mgin.graves.gametest;

import me.mgin.graves.effects.GraveEffects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceGraveTest {
    // no sink tests
    public static void sinkInLava$false(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInWater$false(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkThroughBlocks$false(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInAir$false(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    // sink tests
    public static void sinkInLava$true(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(10, 2, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInWater$true(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(2, 2, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkThroughBlocks$true(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(6, 2, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinksInAir$true(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void replaceBlocks$false(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(19, 2, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void replaceBlocks$true(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        context.getWorld().setBlockState(pos, Blocks.TALL_GRASS.getDefaultState());
        GraveTestHelper.placeGraveCheck(context, player, pos, pos, World.OVERWORLD);
    }

    // blacklist
    public static void respectsBlacklist(TestContext context, PlayerEntity player) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(14, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(14, 9, 2));
        GraveTestHelper.placeGraveCheck(context, player, pos, endPos, World.OVERWORLD);
    }

    // other dims
    public static void spawnsInNether(TestContext context, PlayerEntity player) {
        BlockPos pos = new BlockPos(45, 63, 102);
        GraveTestHelper.placeGraveCheck(context, player, pos, pos, World.NETHER);
    }

    public static void spawnsInEnd(TestContext context, PlayerEntity player) {
        BlockPos pos = new BlockPos(57, 56, 88);
        GraveTestHelper.placeGraveCheck(context, player, pos, pos, World.END);
    }

    // Runs min/max boundary enforcement for two dimensions with different boundaries
    public static void respectsWorldBoundaries(TestContext context, PlayerEntity player) {
        BlockPos minYOverworldPos = context.getAbsolutePos(new BlockPos(18, -10, 2)); // min is -64
        BlockPos maxYOverworldPos = context.getAbsolutePos(new BlockPos(18, 400, 2)); // max is 319
        BlockPos overworldEndPos = context.getAbsolutePos(new BlockPos(18, 2, 2)); // sinks down for max, rises up for min
        GraveTestHelper.placeGraveCheck(context, player, minYOverworldPos, overworldEndPos, World.OVERWORLD);
        GraveTestHelper.placeGraveCheck(context, player, maxYOverworldPos, overworldEndPos, World.OVERWORLD);

        BlockPos minYEndPos = new BlockPos(100, -200, 100); // min is 0
        BlockPos maxYEndPos = new BlockPos(100, 320, 100); // max is 255
        BlockPos endFinalPos = new BlockPos(100, 1, 100); // sinks down for max, rises up for min
        GraveTestHelper.placeGraveCheck(context, player, minYEndPos, endFinalPos, World.END);
        GraveTestHelper.placeGraveCheck(context, player, maxYEndPos, endFinalPos, World.END);
    }

    // grave shouldn't spawn
    public static void graves$false(TestContext context, PlayerEntity player) {
        BlockPos pos = new BlockPos(18, 2, 2);
        GraveTestHelper.gravesDisabledCheck(context, player, pos, World.OVERWORLD);
    }

    public static void respectsDisableEffect(TestContext context, PlayerEntity player) {
        BlockPos pos = new BlockPos(18, 2, 2);
        player.addStatusEffect(new StatusEffectInstance(GraveEffects.DISABLE_GRAVES_EFFECT, 300));
        GraveTestHelper.gravesDisabledCheck(context, player, pos, World.OVERWORLD);
    }

    public static void disableInPvP$true(TestContext context, PlayerEntity player) {
        BlockPos pos = new BlockPos(18, 2, 2);
        PlayerEntity player2 = context.createMockCreativePlayer();
        player.setPosition(0, -58, 0);
        player2.setPosition(0, -58, 0);
        player2.attack(player);
        GraveTestHelper.gravesDisabledCheck(context, player, pos, World.OVERWORLD);
    }
}
