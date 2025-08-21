package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.decay.DecayingGrave.BlockDecay;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.event.server.useblock.item.DecayItem;
import me.mgin.graves.event.server.useblock.item.Honeycomb;
import me.mgin.graves.event.server.useblock.item.Shovel;
import me.mgin.graves.gametest.GraveTest;
import me.mgin.graves.gametest.GraveTestHelper;
import me.mgin.graves.gametest.GraveTestNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DecayTest {
    private static final String testInventory = "[{Slot: 9b, id: \"minecraft:wooden_shovel\", Count: 1b, tag: {Damage: 40}}, {Slot: 10b, id: \"minecraft:stone_shovel\", Count: 1b, tag: {Damage: 129}}, {Slot: 11b, id: \"minecraft:golden_shovel\", Count: 1b, tag: {Damage: 7}}, {Slot: 12b, id: \"minecraft:iron_shovel\", Count: 1b, tag: {Damage: 232}}, {Slot: 13b, id: \"minecraft:diamond_shovel\", Count: 1b, tag: {Damage: 200}}, {Slot: 14b, id: \"minecraft:netherite_shovel\", Count: 1b, tag: {Damage: 1357}}, {Slot: 20b, id: \"minecraft:tnt\", Count: 1b}, {Slot: 21b, id: \"minecraft:lectern\", Count: 1b}, {Slot: 22b, id: \"minecraft:redstone_block\", Count: 1b}, {Slot: 23b, id: \"minecraft:redstone\", Count: 1b}, {Slot: 33b, id: \"minecraft:barrel\", Count: 1b}]";

    public static void decayEnabled$true(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        System.out.println("📗 Running decayEnabled$true");
        GravesConfig config = GravesConfig.getConfig();

        if (block instanceof GraveBlockBase graveBlock) {
            GraveTestHelper.resetGraveDecay(world, pos); // reset the decay
            config.decay.minStageTimeSeconds = 0;
            config.decay.freshGraveDecayChance = 100;
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());

            context.assertTrue(!GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.FRESH),
                "The grave didn't decay despite decayEnabled being true."
            );
        }
    }

    public static void decayEnabled$false(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running decayEnabled$false");
        if (block instanceof GraveBlockBase graveBlock) {
            config.decay.decayEnabled = false;
            GraveTestHelper.resetGraveDecay(world, pos); // reset the decay
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());

            context.assertTrue(GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.FRESH),
                "The grave decayed despite decayEnabled being false."
            );

            GravesConfig.getConfig().resetConfig();
        }
    }
    
    public static void honeycombPreventsDecay(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        System.out.println("📗 Running honeycombPreventsDecay");

        if (block instanceof GraveBlockBase graveBlock) {
            GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

            Honeycomb.handle(player, world, Hand.MAIN_HAND, pos, Items.HONEYCOMB, graveEntity);
            assert graveEntity != null;
            context.assertTrue(graveEntity.getNoDecay() == 1,
                "Honeycomb should was the grave causing noDecay to be set to 1."
            );

            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            context.assertTrue(GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.FRESH),
                "The grave decayed despite being waxed with honeycomb."
            );
        }
    }
    
    public static void shovelRemovesHoneycomb(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockEntity entity = world.getBlockEntity(pos);

        System.out.println("📗 Running shovelRemovesHoneycomb");

        if (entity instanceof GraveBlockEntity graveEntity) {
            Shovel.handle(player, world, Hand.MAIN_HAND, pos, Items.IRON_SHOVEL, graveEntity);
            context.assertTrue(graveEntity.getNoDecay() == 0,
                "Shovel should remove the wax from the last test, setting noDecay to 0."
            );
        }
    }

    public static void decayItemsAddDecay(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockEntity entity = world.getBlockEntity(pos);
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running decayItemsAddDecay");

        if (entity instanceof GraveBlockEntity graveEntity) {
            // Disable decay and reset it to prevent natural decay from skewing results
            // This will be useful for next test as well, since shovels can remove decay while its disabled too
            config.decay.decayEnabled = false;
            GraveTestHelper.resetGraveDecay(world, pos);

            DecayItem.handle(player, world, Hand.MAIN_HAND, pos, Items.WEEPING_VINES, graveEntity);
            DecayItem.handle(player, world, Hand.MAIN_HAND, pos, Items.RED_MUSHROOM, graveEntity);
            context.assertTrue(GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.WEATHERED),
                "Vines and mushrooms should cause decay stage progression."
            );
        }
    }

    public static void shovelReducesDecayStage(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockEntity entity = world.getBlockEntity(pos);

        System.out.println("📗 Running shovelReducesDecayStage");

        if (entity instanceof GraveBlockEntity graveEntity) {
            Shovel.handle(player, world, Hand.MAIN_HAND, pos, Items.IRON_SHOVEL, graveEntity);
            context.assertTrue(GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.OLD),
                "Shovel should be able to reduce amount of decay."
            );
        }
    }

    /**
     * This test also tests decayBreaksItems$false.
     */
    public static void itemsDecay(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        GravesConfig config = GravesConfig.getConfig().resetConfig();

        System.out.println("📗 Running itemsDecay");

        // Remove the grave in the center of the test area and clear player inventory
        GraveTestHelper.removeGrave(world, pos);
        GraveTestHelper.clearPlayerInventory(player);

        // Configure mod to allow for adding decay easily
        config.decay.minStageTimeSeconds = 0;
        config.decay.freshGraveDecayChance = 100;
        config.decay.oldGraveDecayChance = 100;
        config.decay.weatheredGraveDecayChance = 100;
        config.main.graveCoordinates = GraveTest.verbose;

        // Set the player inventory and place a grave based on said inventory
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, testInventory);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);

        if (block instanceof GraveBlockBase graveBlock) {
            // Age the grave several times
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            GraveTestHelper.resetGraveDecay(world, pos);
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
            graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());

            // Clear player inventory and retrieve the grave
            GraveTestHelper.clearPlayerInventory(player);
            RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

            // Compare the inventories, should be false as the items have changed.
            String playerInventoryAfter = GraveTestNBTHelper.getPlayerInventorySNBT(player);
            context.assertFalse(
                GraveTestNBTHelper.compareInventoriesSNBT(testInventory, playerInventoryAfter),
                "As items have decayed, the testInventory and player inventory should no longer match."
            );
        }
    }

    public static void decayBreaksItems$true(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        System.out.println("📗 Running decayBreaksItems$true");

        // Clear the player's inventory
        GraveTestHelper.clearPlayerInventory(player);

        // Set the player inventory and place a grave based on said inventory
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, testInventory);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);

        if (block instanceof GraveBlockBase graveBlock) {
            // Age the grave many times to give the items a chance to break
            for (int i = 0; i < 10; i++) {
                graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
                graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
                graveBlock.tickDecay(state, (ServerWorld) world, pos, world.getRandom());
                GraveTestHelper.resetGraveDecay(world, pos);
            }

            // Store the amount of items from before clearing/retrieving
            int amountOfItemsBefore = GraveTestHelper.getAmountOfItemsInInventory(player);

            // Clear player inventory and retrieve the grave
            GraveTestHelper.clearPlayerInventory(player);
            RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

            // Store the amount of items from afterwards
            int amountOfItemsAfter = GraveTestHelper.getAmountOfItemsInInventory(player);

            // Compare them, they should not match
            context.assertTrue(
                amountOfItemsBefore != amountOfItemsAfter,
                "The amount of items should have changed because decay should have destroyed some of them."
            );
        }
    }

    // use `randomTick` instead of `tickDecay` for testing this (as that's where it's used)
    // prob set decay chance to 100%, run randomTick, then check if it's decayed
    // then set the block's timer to the min time and then run randomTick again and check again
    public static void minStageTimeSeconds(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity entity = world.getBlockEntity(pos);
        GravesConfig config = GravesConfig.getConfig().resetConfig();

        System.out.println("📗 Running minStageTimeSeconds");

        // Reset config and configure it for this test
        config.decay.minStageTimeSeconds = 60;
        config.decay.freshGraveDecayChance = 100;

        if (block instanceof GraveBlockBase graveBlock) {
            // Run `randomTick`, as it contains the check for minStageTimeSeconds
            block.randomTick(state, (ServerWorld) world, pos, world.getRandom());

            // Ensure the grave hasn't decayed yet
            context.assertTrue(
                GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.FRESH),
                "The grave should still be FRESH as minStageTimeSeconds hasn't been reached."
            );

            // Set the timer to minStageTimeSeconds and tick it again, then assert it has decayed
            assert entity != null;
            ((GraveBlockEntity) entity).incrementTimer("decay", 60);
            block.randomTick(state, (ServerWorld) world, pos, world.getRandom());
            context.assertFalse(
                GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.FRESH),
                "The grave should be able to decay now, as min stage time has passed, and shouldn't be FRESH."
            );
        }
    }

    public static void maxStageTimeSeconds(TestContext context, PlayerEntity player, BlockPos pos) {
        // this is used in scheduledTick, not sure if there would be a problem calling it manually lol
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity entity = world.getBlockEntity(pos);

        System.out.println("📗 Running maxStageTimeSeconds");

        if (block instanceof GraveBlockBase graveBlock) {
            // Increment the timer to above default maxStageTimeSeconds (300)
            assert entity != null;
            ((GraveBlockEntity) entity).incrementTimer("decay", 240);

            // Run a scheduled tick, just to ensure the maxStageTimeSeconds code runs
            block.scheduledTick(state, (ServerWorld) world, pos, world.getRandom());

            // Ensure grave has decayed.
            context.assertTrue(
                GraveTestHelper.compareDecayLevel(world, pos, BlockDecay.WEATHERED),
                "Grave should now be decayed a stage further, as it reached the maxStageTimeSeconds."
            );
        }
    }
}
