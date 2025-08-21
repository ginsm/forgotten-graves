package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.effects.GraveEffects;
import me.mgin.graves.gametest.GraveTest;
import me.mgin.graves.gametest.GraveTestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class PlaceGraveTest {
    public static void sinkInLava$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();
        config.main.graveCoordinates = GraveTest.verbose;

        System.out.println("📗 Running sinkInLava$false");
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInWater$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running sinkInWater$false");
        config.spawning.sinkInWater = false;
        BlockPos pos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkThroughBlocks$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running sinkThroughBlocks$false");
        config.spawning.sinkThroughBlocks = false;
        BlockPos pos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInAir$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running sinkInAir$false");
        config.spawning.sinkInAir = false;
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    // sink tests
    public static void sinkInLava$true(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig().resetConfig();

        System.out.println("📗 Running sinkInLava$true");
        config.spawning.sinkInLava = true;
        config.main.graveCoordinates = GraveTest.verbose;
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(10, 2, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkInWater$true(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running sinkInWater$true");
        BlockPos pos = context.getAbsolutePos(new BlockPos(2, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(2, 2, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinkThroughBlocks$true(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running sinkThroughBlocks$true");
        BlockPos pos = context.getAbsolutePos(new BlockPos(6, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(6, 2, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void sinksInAir$true(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running sinkInAir$true");
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void replaceBlocks$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running replaceBlocks$false");
        config.spawning.replaceBlocks = false;
        BlockPos pos = context.getAbsolutePos(new BlockPos(19, 2, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void replaceBlocks$true(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running replaceBlocks$true");
        BlockPos pos = context.getAbsolutePos(new BlockPos(18, 2, 2));
        context.getWorld().setBlockState(pos, Blocks.TALL_GRASS.getDefaultState());
        checkPlaceGrave(context, player, pos, pos, World.OVERWORLD);
    }

    public static void respectsBlacklist(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running respectsBlackList");
        BlockPos pos = context.getAbsolutePos(new BlockPos(14, 7, 2));
        BlockPos endPos = context.getAbsolutePos(new BlockPos(14, 9, 2));
        checkPlaceGrave(context, player, pos, endPos, World.OVERWORLD);
    }

    public static void spawnsInNether(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running spawnsInNether");
        BlockPos pos = new BlockPos(45, 63, 102);
        checkPlaceGrave(context, player, pos, pos, World.NETHER);
    }

    public static void spawnsInEnd(TestContext context, PlayerEntity player) {
        System.out.println("📗 Running sinkInEnd");
        BlockPos pos = new BlockPos(57, 56, 88);
        checkPlaceGrave(context, player, pos, pos, World.END);
    }

    public static void respectsWorldBoundaries(TestContext context, PlayerEntity player) {
        BlockPos minYOverworldPos = context.getAbsolutePos(new BlockPos(18, -10, 2)); // min is -64
        BlockPos maxYOverworldPos = context.getAbsolutePos(new BlockPos(18, 400, 2)); // max is 319
        BlockPos overworldEndPos = context.getAbsolutePos(new BlockPos(18, 2, 2)); // sinks down for max, rises up for min
        System.out.println("📗 Running respectsWorldBoundaries$min");
        checkPlaceGrave(context, player, minYOverworldPos, overworldEndPos, World.OVERWORLD);
        System.out.println("📗 Running respectsWorldBoundaries$max");
        checkPlaceGrave(context, player, maxYOverworldPos, overworldEndPos, World.OVERWORLD);

        BlockPos minYEndPos = new BlockPos(100, -200, 100); // min is 0
        BlockPos maxYEndPos = new BlockPos(100, 320, 100); // max is 255
        BlockPos endFinalPos = new BlockPos(100, 1, 100); // sinks down for max, rises up for min
        System.out.println("📗 Running respectsWorldBoundaries$min-end");
        checkPlaceGrave(context, player, minYEndPos, endFinalPos, World.END);
        System.out.println("📗 Running respectsWorldBoundaries$max-end");
        checkPlaceGrave(context, player, maxYEndPos, endFinalPos, World.END);
    }

    // Grave shouldn't spawn scenarios
    public static void graves$false(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running graves$false");
        config.main.graves = false;
        BlockPos pos = new BlockPos(18, 2, 2);
        checkGravesDisabled(context, player, pos, World.OVERWORLD);
    }

    public static void respectsDisableEffect(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig().resetConfig();
        config.main.graveCoordinates = GraveTest.verbose;

        System.out.println("📗 Running respectsDisableEffect");
        BlockPos pos = new BlockPos(18, 2, 2);
        player.addStatusEffect(new StatusEffectInstance(GraveEffects.DISABLE_GRAVES_EFFECT, 300));
        checkGravesDisabled(context, player, pos, World.OVERWORLD);
    }

    public static void disableInPvP$true(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running disableInPvP$true");
        config.main.disableInPvP = true;
        BlockPos pos = new BlockPos(18, 2, 2);
        PlayerEntity player2 = context.createMockCreativePlayer();
        player.setPosition(0, -58, 0);
        player2.setPosition(0, -58, 0);
        player2.attack(player);
        checkGravesDisabled(context, player, pos, World.OVERWORLD);
    }

    // Helper functions
    /**
     * Places a grave at the given {@code pos}, checks the {@code endPos} with {@link TestContext#checkBlock}, and
     * then removes the grave from the world with {@link RetrieveGrave#retrieveWithInteract}.
     *
     * @param context TestContext instance
     * @param player A mock player; use {@link TestContext#createMockSurvivalPlayer}
     * @param pos An absolute BlockPos; use {@link TestContext#getAbsolutePos} with a relative BlockPos
     * @param endPos An absolute BlockPos; use {@link TestContext#getAbsolutePos} with a relative BlockPos
     * @param worldKey RegistryKey for the given world (OVERWORLD, NETHER, END).
     */
   private static void checkPlaceGrave(TestContext context, PlayerEntity player, BlockPos pos, BlockPos endPos, RegistryKey<World> worldKey) {
        World world = Objects.requireNonNull(player.getServer()).getWorld(worldKey);
        if (world != null) {
            // Place the grave
            PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);

            // Ensure grave placed
            Block block = world.getBlockState(endPos).getBlock();
            String errorMessage = "Expect block to be a grave block at " + endPos + " got " + block.getName()
                + " in " + worldKey.getValue();
            context.assertTrue(block instanceof GraveBlockBase, errorMessage);

            // Remove grave
            RetrieveGrave.retrieveWithInteract(player, world, endPos);
        }
    }

    /**
     * Teleports a player to a given location, gives them an item stack, kills them, ensures a grave didn't spawn,
     * and ensures the item stack is on the ground.
     *
     * @param context TestContext instance
     * @param player A mock player; use {@link TestContext#createMockSurvivalPlayer}
     * @param pos An absolute BlockPos; use {@link TestContext#getAbsolutePos} with a relative BlockPos
     * @param worldKey RegistryKey for the given world (OVERWORLD, NETHER, END).
     */
    private static void checkGravesDisabled(TestContext context, PlayerEntity player, BlockPos pos, RegistryKey<World> worldKey) {
        World world = Objects.requireNonNull(player.getServer()).getWorld(worldKey);
        if (world != null) {
            // Give player items to check for when player dies
            ItemStack stack = Items.DIAMOND_BLOCK.getDefaultStack();
            stack.setCount(5);
            player.giveItemStack(stack);

            // Teleport and kill player
            BlockPos abs = context.getAbsolutePos(pos);
            GraveTestHelper.teleportPlayer(player, abs);
            GraveTestHelper.dropMockPlayerInventory(player, world, abs); // Mock players don't drop their inventories upon death
            player.kill();

            // Ensure grave didn't spawn
            Block block = world.getBlockState(abs).getBlock();
            String errorMessage = "Expect block not to be a grave block at " + abs + " got " + block.getName()
                + " in " + worldKey.getValue();
            context.assertTrue(!(block instanceof GraveBlockBase), errorMessage);

            // Check for the item stack
            context.expectItemsAt(Items.DIAMOND_BLOCK, pos, 3, 5);
            context.killAllEntities(ItemEntity.class); // cleans up the diamond blocks
        }
    }
}
