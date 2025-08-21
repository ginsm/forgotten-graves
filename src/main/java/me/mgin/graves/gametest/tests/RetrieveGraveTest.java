package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveMergeOrder;
import me.mgin.graves.gametest.GraveTest;
import me.mgin.graves.gametest.GraveTestHelper;
import me.mgin.graves.gametest.GraveTestNBTHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RetrieveGraveTest {
    /**
     * This tests retrieving full, partial, and empty graves.
     */
    public static void basicRetrieval(TestContext context, PlayerEntity player, BlockPos pos) {
        // The inventory will be set to the following SNBTs in each test
        String fullInventory = "[{Slot: 0b, id: \"minecraft:red_sand\", Count: 64b}, {Slot: 1b, id: \"minecraft:dark_oak_planks\", Count: 64b}, {Slot: 2b, id: \"minecraft:sand\", Count: 64b}, {Slot: 3b, id: \"minecraft:crimson_planks\", Count: 64b}, {Slot: 4b, id: \"minecraft:bedrock\", Count: 64b}, {Slot: 5b, id: \"minecraft:gravel\", Count: 64b}, {Slot: 6b, id: \"minecraft:warped_planks\", Count: 64b}, {Slot: 7b, id: \"minecraft:coal_ore\", Count: 64b}, {Slot: 8b, id: \"minecraft:mangrove_planks\", Count: 64b}, {Slot: 9b, id: \"minecraft:stone\", Count: 64b}, {Slot: 10b, id: \"minecraft:granite\", Count: 64b}, {Slot: 11b, id: \"minecraft:polished_granite\", Count: 64b}, {Slot: 12b, id: \"minecraft:diorite\", Count: 64b}, {Slot: 13b, id: \"minecraft:polished_diorite\", Count: 64b}, {Slot: 14b, id: \"minecraft:andesite\", Count: 64b}, {Slot: 15b, id: \"minecraft:polished_andesite\", Count: 64b}, {Slot: 16b, id: \"minecraft:deepslate\", Count: 64b}, {Slot: 17b, id: \"minecraft:cobbled_deepslate\", Count: 64b}, {Slot: 18b, id: \"minecraft:polished_deepslate\", Count: 64b}, {Slot: 19b, id: \"minecraft:calcite\", Count: 64b}, {Slot: 20b, id: \"minecraft:tuff\", Count: 64b}, {Slot: 21b, id: \"minecraft:dripstone_block\", Count: 64b}, {Slot: 22b, id: \"minecraft:grass_block\", Count: 64b}, {Slot: 23b, id: \"minecraft:dirt\", Count: 64b}, {Slot: 24b, id: \"minecraft:coarse_dirt\", Count: 64b}, {Slot: 25b, id: \"minecraft:podzol\", Count: 64b}, {Slot: 26b, id: \"minecraft:rooted_dirt\", Count: 64b}, {Slot: 27b, id: \"minecraft:mud\", Count: 64b}, {Slot: 28b, id: \"minecraft:crimson_nylium\", Count: 64b}, {Slot: 29b, id: \"minecraft:warped_nylium\", Count: 64b}, {Slot: 30b, id: \"minecraft:cobblestone\", Count: 64b}, {Slot: 31b, id: \"minecraft:oak_planks\", Count: 64b}, {Slot: 32b, id: \"minecraft:spruce_planks\", Count: 64b}, {Slot: 33b, id: \"minecraft:birch_planks\", Count: 64b}, {Slot: 34b, id: \"minecraft:jungle_planks\", Count: 64b}, {Slot: 35b, id: \"minecraft:acacia_planks\", Count: 64b}, {Slot: 100b, id: \"minecraft:diamond_boots\", Count: 1b, tag: {Damage: 0}}, {Slot: 101b, id: \"minecraft:diamond_leggings\", Count: 1b, tag: {Damage: 0}}, {Slot: 102b, id: \"minecraft:diamond_chestplate\", Count: 1b, tag: {Damage: 0}}, {Slot: 103b, id: \"minecraft:diamond_helmet\", Count: 1b, tag: {Damage: 0}}, {Slot: -106b, id: \"minecraft:shield\", Count: 1b, tag: {Damage: 0}}]";
        String partialInventory = "[{Count:64b,Slot:0b,id:\"minecraft:red_sand\"},{Count:64b,Slot:2b,id:\"minecraft:sand\"},{Count:64b,Slot:3b,id:\"minecraft:crimson_planks\"},{Count:64b,Slot:4b,id:\"minecraft:bedrock\"},{Count:64b,Slot:6b,id:\"minecraft:warped_planks\"},{Count:64b,Slot:8b,id:\"minecraft:mangrove_planks\"},{Count:64b,Slot:9b,id:\"minecraft:stone\"},{Count:32b,Slot:11b,id:\"minecraft:polished_granite\"},{Count:64b,Slot:12b,id:\"minecraft:diorite\"},{Count:64b,Slot:13b,id:\"minecraft:cobblestone\"},{Count:64b,Slot:14b,id:\"minecraft:andesite\"},{Count:32b,Slot:15b,id:\"minecraft:polished_andesite\"},{Count:64b,Slot:17b,id:\"minecraft:cobbled_deepslate\"},{Count:32b,Slot:18b,id:\"minecraft:polished_deepslate\"},{Count:64b,Slot:19b,id:\"minecraft:dirt\"},{Count:64b,Slot:20b,id:\"minecraft:tuff\"},{Count:64b,Slot:25b,id:\"minecraft:podzol\"},{Count:64b,Slot:26b,id:\"minecraft:rooted_dirt\"},{Count:64b,Slot:27b,id:\"minecraft:mud\"},{Count:64b,Slot:28b,id:\"minecraft:crimson_nylium\"},{Count:64b,Slot:29b,id:\"minecraft:warped_nylium\"},{Count:64b,Slot:31b,id:\"minecraft:oak_planks\"},{Count:32b,Slot:32b,id:\"minecraft:grass_block\"},{Count:64b,Slot:33b,id:\"minecraft:birch_planks\"},{Count:32b,Slot:35b,id:\"minecraft:acacia_planks\"},{Count:1b,Slot:100b,id:\"minecraft:diamond_boots\",tag:{Damage:0}},{Count:1b,Slot:102b,id:\"minecraft:diamond_chestplate\",tag:{Damage:0}}]";
        String emptyInventory = "[]";

        GravesConfig.getConfig().main.graveCoordinates = GraveTest.verbose;
        System.out.println("📗 Running basicRetrieval");

        context.assertTrue(
            checkBasicRetrieval(player, pos, fullInventory),
            "The inventory didn't match the full inventory SNBT."
        );
        context.assertTrue(
            checkBasicRetrieval(player, pos, partialInventory),
            "The inventory didn't match the partial inventory SNBT."
        );
        context.assertTrue(
            checkBasicRetrieval(player, pos, emptyInventory),
            "The inventory didn't match the empty inventory SNBT."
        );
    }

    /**
     * This tests multiple merge features:<br>
     * - Stack consolidation.<br>
     * - Merging worn equipment.<br>
     * - Non-stackables not consolidating (i.e. enchanting books).<br>
     * - NBT items not consolidating with their non-nbt counterparts.
     */
    public static void mergeRetrieval(TestContext context, PlayerEntity player, BlockPos pos) {
        String currentInv = "[{Count:39b,Slot:4b,id:\"minecraft:torch\"},{Count:1b,Slot:5b,id:\"minecraft:shield\",tag:{Damage:4}},{Count:1b,Slot:10b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{lvl:4s,id:\"minecraft:sharpness\"}]}},{Count:33b,Slot:21b,id:\"minecraft:stick\",tag:{HideFlag:1,Enchantments:[{lvl:1000,id:\"knockback\"}]}},{Count:9b,Slot:23b,id:\"minecraft:stick\"},{Count:1b,Slot:100b,id:\"minecraft:netherite_boots\",tag:{Damage:4}},{Count:1b,Slot:102b,id:\"minecraft:diamond_chestplate\",tag:{Damage:8}},{Count:1b,Slot:103b,id:\"minecraft:iron_helmet\",tag:{Damage:0}},{Count:7b,Slot:-106b,id:\"minecraft:netherite_block\"}]";
        String graveInv = "[{Count:9b,Slot:4b,id:\"minecraft:torch\"},{Count:7b,Slot:9b,id:\"minecraft:stick\"},{Count:1b,Slot:10b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{lvl:4s,id:\"minecraft:sharpness\"}]}},{Count:1b,Slot:12b,id:\"minecraft:stick\",tag:{HideFlag:1,Enchantments:[{lvl:1000,id:\"knockback\"}]}},{Count:1b,Slot:100b,id:\"minecraft:golden_boots\",tag:{Damage:0}},{Count:1b,Slot:101b,id:\"minecraft:leather_leggings\",tag:{Damage:0}},{Count:1b,Slot:103b,id:\"minecraft:chainmail_helmet\",tag:{Damage:2}},{Count:7b,Slot:-106b,id:\"minecraft:netherite_block\"}]";
        String graveInvMergeResult = "[{Count:1b,Slot:0b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:4s}]}},{Count:1b,Slot:1b,id:\"minecraft:netherite_boots\",tag:{Damage:4}},{Count:1b,Slot:2b,id:\"minecraft:iron_helmet\",tag:{Damage:0}},{Count:48b,Slot:4b,id:\"minecraft:torch\"},{Count:1b,Slot:5b,id:\"minecraft:shield\",tag:{Damage:4}},{Count:16b,Slot:9b,id:\"minecraft:stick\"},{Count:1b,Slot:10b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:4s}]}},{Count:34b,Slot:12b,id:\"minecraft:stick\",tag:{Enchantments:[{id:\"knockback\",lvl:1000}],HideFlag:1}},{Count:1b,Slot:100b,id:\"minecraft:golden_boots\",tag:{Damage:0}},{Count:1b,Slot:101b,id:\"minecraft:leather_leggings\",tag:{Damage:0}},{Count:1b,Slot:102b,id:\"minecraft:diamond_chestplate\",tag:{Damage:8}},{Count:1b,Slot:103b,id:\"minecraft:chainmail_helmet\",tag:{Damage:2}},{Count:14b,Slot:-106b,id:\"minecraft:netherite_block\"}]";
        String currentInvMergeResult = "[{Count:1b,Slot:0b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:4s}]}},{Count:1b,Slot:1b,id:\"minecraft:golden_boots\",tag:{Damage:0}},{Count:1b,Slot:2b,id:\"minecraft:chainmail_helmet\",tag:{Damage:2}},{Count:48b,Slot:4b,id:\"minecraft:torch\"},{Count:1b,Slot:5b,id:\"minecraft:shield\",tag:{Damage:4}},{Count:1b,Slot:10b,id:\"minecraft:enchanted_book\",tag:{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:4s}]}},{Count:34b,Slot:21b,id:\"minecraft:stick\",tag:{Enchantments:[{id:\"knockback\",lvl:1000}],HideFlag:1}},{Count:16b,Slot:23b,id:\"minecraft:stick\"},{Count:1b,Slot:100b,id:\"minecraft:netherite_boots\",tag:{Damage:4}},{Count:1b,Slot:101b,id:\"minecraft:leather_leggings\",tag:{Damage:0}},{Count:1b,Slot:102b,id:\"minecraft:diamond_chestplate\",tag:{Damage:8}},{Count:1b,Slot:103b,id:\"minecraft:iron_helmet\",tag:{Damage:0}},{Count:14b,Slot:-106b,id:\"minecraft:netherite_block\"}]";
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running mergeRetrieval");
        config.main.mergeOrder = GraveMergeOrder.GRAVE;
        context.assertTrue(
            checkMerge(player, pos, currentInv, graveInv, graveInvMergeResult),
            "The merge algorithm should have merged the current inventory into the grave inventory, but it failed."
        );

        config.main.mergeOrder = GraveMergeOrder.CURRENT;
        context.assertTrue(
            checkMerge(player, pos, currentInv, graveInv, currentInvMergeResult),
            "The merge algorithm should have merged the grave inventory into the current inventory, but it failed."
        );

        GravesConfig.getConfig().resetConfig();
    }

    /**
     * This tests several overflow scenarios:<br>
     * - Overflowing because the grave inventory is full when the merge order is set to GRAVE.<br>
     * - Overflowing because the inventory naturally fills up during the merge with the merge order set to GRAVE.<br>
     * - Overflowing because the inventory naturally fills up with the merge order set to CURRENT.
     * - Overflowing into the off-hand as a last resort (with an empty off-hand and partially full offhand).
     */
    public static void overflowRetrieval(TestContext context, PlayerEntity player, BlockPos pos) {
        String fullInventory = "[{Slot:0b,id:\"minecraft:stone\",Count:64b},{Slot:1b,id:\"minecraft:stone\",Count:64b},{Slot:2b,id:\"minecraft:stone\",Count:64b},{Slot:3b,id:\"minecraft:stone\",Count:64b},{Slot:4b,id:\"minecraft:stone\",Count:64b},{Slot:5b,id:\"minecraft:stone\",Count:64b},{Slot:6b,id:\"minecraft:stone\",Count:64b},{Slot:7b,id:\"minecraft:stone\",Count:64b},{Slot:8b,id:\"minecraft:stone\",Count:64b},{Slot:9b,id:\"minecraft:stone\",Count:64b},{Slot:10b,id:\"minecraft:stone\",Count:64b},{Slot:11b,id:\"minecraft:stone\",Count:64b},{Slot:12b,id:\"minecraft:stone\",Count:64b},{Slot:13b,id:\"minecraft:stone\",Count:64b},{Slot:14b,id:\"minecraft:stone\",Count:64b},{Slot:15b,id:\"minecraft:stone\",Count:64b},{Slot:16b,id:\"minecraft:stone\",Count:64b},{Slot:17b,id:\"minecraft:stone\",Count:64b},{Slot:18b,id:\"minecraft:stone\",Count:64b},{Slot:19b,id:\"minecraft:stone\",Count:64b},{Slot:20b,id:\"minecraft:stone\",Count:64b},{Slot:21b,id:\"minecraft:stone\",Count:64b},{Slot:22b,id:\"minecraft:stone\",Count:64b},{Slot:23b,id:\"minecraft:stone\",Count:64b},{Slot:24b,id:\"minecraft:stone\",Count:64b},{Slot:25b,id:\"minecraft:stone\",Count:64b},{Slot:26b,id:\"minecraft:stone\",Count:64b},{Slot:27b,id:\"minecraft:stone\",Count:64b},{Slot:28b,id:\"minecraft:stone\",Count:64b},{Slot:29b,id:\"minecraft:stone\",Count:64b},{Slot:30b,id:\"minecraft:stone\",Count:64b},{Slot:31b,id:\"minecraft:stone\",Count:64b},{Slot:32b,id:\"minecraft:stone\",Count:64b},{Slot:33b,id:\"minecraft:stone\",Count:64b},{Slot:34b,id:\"minecraft:stone\",Count:64b},{Slot:35b,id:\"minecraft:stone\",Count:64b},{Slot:-106b,id:\"minecraft:grass_block\",Count:48b}]";
        String partialInventory = "[{Slot:0b,id:\"minecraft:sandstone\",Count:64b},{Slot:1b,id:\"minecraft:sandstone\",Count:64b},{Slot:2b,id:\"minecraft:sandstone\",Count:64b},{Slot:3b,id:\"minecraft:sandstone\",Count:64b},{Slot:4b,id:\"minecraft:sandstone\",Count:64b},{Slot:5b,id:\"minecraft:sandstone\",Count:64b},{Slot:6b,id:\"minecraft:sandstone\",Count:64b},{Slot:9b,id:\"minecraft:sandstone\",Count:64b},{Slot:10b,id:\"minecraft:sandstone\",Count:64b},{Slot:11b,id:\"minecraft:sandstone\",Count:64b},{Slot:12b,id:\"minecraft:sandstone\",Count:64b},{Slot:13b,id:\"minecraft:sandstone\",Count:64b},{Slot:14b,id:\"minecraft:sandstone\",Count:64b},{Slot:15b,id:\"minecraft:sandstone\",Count:64b},{Slot:16b,id:\"minecraft:sandstone\",Count:64b},{Slot:17b,id:\"minecraft:sandstone\",Count:64b},{Slot:18b,id:\"minecraft:sandstone\",Count:64b},{Slot:19b,id:\"minecraft:sandstone\",Count:64b},{Slot:20b,id:\"minecraft:sandstone\",Count:64b},{Slot:21b,id:\"minecraft:sandstone\",Count:64b},{Slot:22b,id:\"minecraft:sandstone\",Count:64b},{Slot:23b,id:\"minecraft:sandstone\",Count:64b},{Slot:24b,id:\"minecraft:sandstone\",Count:64b},{Slot:25b,id:\"minecraft:sandstone\",Count:64b},{Slot:26b,id:\"minecraft:sandstone\",Count:64b},{Slot:27b,id:\"minecraft:sandstone\",Count:64b},{Slot:28b,id:\"minecraft:sandstone\",Count:64b},{Slot:29b,id:\"minecraft:sandstone\",Count:64b},{Slot:30b,id:\"minecraft:sandstone\",Count:64b},{Slot:31b,id:\"minecraft:sandstone\",Count:64b},{Slot:32b,id:\"minecraft:sandstone\",Count:64b},{Slot:33b,id:\"minecraft:sandstone\",Count:64b},{Slot:34b,id:\"minecraft:sandstone\",Count:64b},{Slot:35b,id:\"minecraft:sandstone\",Count:64b}]";
        String currentInventory = "[{Slot:0b,id:\"minecraft:grass_block\",Count:64b},{Slot:1b,id:\"minecraft:grass_block\",Count:64b},{Slot:2b,id:\"minecraft:grass_block\",Count:64b},{Slot:3b,id:\"minecraft:grass_block\",Count:64b}]";
        GravesConfig config = GravesConfig.getConfig();
        config.main.graveCoordinates = GraveTest.verbose;

        System.out.println("📗 Running overflowRetrieval");
        // Overflows because grave inventory is full (merge order GRAVE)
        // Also tests merging into existing offhand stack whilst overflowing (as a last resort)
        config.main.mergeOrder = GraveMergeOrder.GRAVE;
        checkOverflow(context, player, pos, currentInventory, fullInventory, Items.GRASS_BLOCK, 240);

        // Overflows because inventory fills up (merge order GRAVE)
        // Also tests merging into an empty offhand whilst overflowing (as a last resort)
        checkOverflow(context, player, pos, currentInventory, partialInventory, Items.GRASS_BLOCK, 64);

        // Overflows because inventory fills up (merge order CURRENT)
        config.main.mergeOrder = GraveMergeOrder.CURRENT;
        checkOverflow(context, player, pos, currentInventory, partialInventory, Items.SANDSTONE, 64);

        GravesConfig.getConfig().resetConfig();
    }

    /**
     * This test ensures unloaded mod inventories are still retrieved and end up in the player's main inventory.
     */
    public static void unloadedModRetrieval(TestContext context, PlayerEntity player, BlockPos pos) {
        // Test retrieving inventories for mods that aren't installed
        String backslotSNBT = "{timers:{},trinkets:{Items:[]},CustomName:'{\"text\":\"BackSlot-Full\"}',XP:0,mstime:1681418977105L,x:-52,ItemCount:{trinkets:0,Items:41},y:-30,Items:{Items:[{Slot:0b,id:\"minecraft:bow\",Count:1b,tag:{Damage:0}},{Slot:1b,id:\"minecraft:diamond_sword\",Count:1b,tag:{Damage:0}}]},z:-21,id:\"forgottengraves:grave\",noDecay:0,GraveOwner:{Id:[I;1306594965,790902035,-1235400077,422671094],Name:\"BackSlot-Full\"}}";
        String backslotResult = "[{Count:1b,Slot:0b,id:\"minecraft:bow\",tag:{Damage:0}},{Count:1b,Slot:1b,id:\"minecraft:diamond_sword\",tag:{Damage:0}}]";
        String inventorioSNBT = "{timers:{},trinkets:{Items:[]},inventorio:{Items:[{Slot:27b,id:\"minecraft:flint_and_steel\",Count:1b,tag:{Damage:0}},{Slot:28b,id:\"minecraft:shield\",Count:1b,tag:{Damage:0}},{Slot:29b,id:\"minecraft:bow\",Count:1b,tag:{Damage:0}},{Slot:30b,id:\"minecraft:crossbow\",Count:1b,tag:{Damage:0}},{Slot:35b,id:\"minecraft:diamond_pickaxe\",Count:1b,tag:{Damage:0}},{Slot:36b,id:\"minecraft:diamond_sword\",Count:1b,tag:{Damage:0}},{Slot:37b,id:\"minecraft:diamond_axe\",Count:1b,tag:{Damage:0}},{Slot:38b,id:\"minecraft:diamond_shovel\",Count:1b,tag:{Damage:0}},{Slot:39b,id:\"minecraft:diamond_hoe\",Count:1b,tag:{Damage:0}}]},CustomName:'{\"text\":\"Inventorio-Full\"}',XP:0,mstime:1681418977105L,x:-52,ItemCount:{inventorio:40,trinkets:0,Items:41},y:-30,Items:{Items:[{Slot:0b,id:\"minecraft:diamond_sword\",Count:1b,tag:{Damage:0}}]},z:-23,id:\"forgottengraves:grave\",noDecay:0,GraveOwner:{Id:[I;955138089,-476628734,-1921395004,-719679505],Name:\"Inventorio-Full\"}}";
        String inventorioResult = "[{Count:1b,Slot:0b,id:\"minecraft:diamond_sword\",tag:{Damage:0}},{Count:1b,Slot:1b,id:\"minecraft:flint_and_steel\",tag:{Damage:0}},{Count:1b,Slot:2b,id:\"minecraft:shield\",tag:{Damage:0}},{Count:1b,Slot:3b,id:\"minecraft:bow\",tag:{Damage:0}},{Count:1b,Slot:4b,id:\"minecraft:crossbow\",tag:{Damage:0}},{Count:1b,Slot:5b,id:\"minecraft:diamond_pickaxe\",tag:{Damage:0}},{Count:1b,Slot:6b,id:\"minecraft:diamond_sword\",tag:{Damage:0}},{Count:1b,Slot:7b,id:\"minecraft:diamond_axe\",tag:{Damage:0}},{Count:1b,Slot:8b,id:\"minecraft:diamond_shovel\",tag:{Damage:0}},{Count:1b,Slot:9b,id:\"minecraft:diamond_hoe\",tag:{Damage:0}}]";
        String trinketsSNBT = "{timers:{},trinkets:{Items:[{Slot:0b,id:\"minecraft:golden_helmet\",Count:1b,tag:{Damage:0}},{Slot:1b,id:\"minecraft:golden_boots\",Count:1b,tag:{Damage:0}},{Slot:2b,id:\"minecraft:golden_chestplate\",Count:1b,tag:{Damage:0}},{Slot:3b,id:\"minecraft:elytra\",Count:1b,tag:{Damage:0}},{Slot:4b,id:\"minecraft:golden_leggings\",Count:1b,tag:{Damage:0}}]},CustomName:'{\"text\":\"Trinkets-BothFull\"}',XP:0,mstime:1681418977105L,x:-52,ItemCount:{trinkets:5,Items:41},y:-30,Items:{Items:[]},z:-19,id:\"forgottengraves:grave\",noDecay:0,GraveOwner:{Id:[I;768727527,1471035076,-1953447371,-1312966385],Name:\"Trinkets-BothFull\"}}";
        String trinketsResult = "[{Count:1b,Slot:0b,id:\"minecraft:golden_helmet\",tag:{Damage:0}},{Count:1b,Slot:1b,id:\"minecraft:golden_boots\",tag:{Damage:0}},{Count:1b,Slot:2b,id:\"minecraft:golden_chestplate\",tag:{Damage:0}},{Count:1b,Slot:3b,id:\"minecraft:elytra\",tag:{Damage:0}},{Count:1b,Slot:4b,id:\"minecraft:golden_leggings\",tag:{Damage:0}}]";
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running unloadedModRetrieval");
        // Enable Grave Robbing as the graves being set are not owned by the mock player
        config.server.graveRobbing = true;
        config.main.graveCoordinates = GraveTest.verbose;

        context.assertTrue(
            checkUnloadedInventories(context, player, pos, backslotSNBT, backslotResult),
            "The player's main inventory should contain the BackSlot items after retrieving the grave."
        );

        context.assertTrue(
            checkUnloadedInventories(context, player, pos, inventorioSNBT, inventorioResult),
            "The player's main inventory should contain the Inventorio items after retrieving the grave."
        );

        context.assertTrue(
            checkUnloadedInventories(context, player, pos, trinketsSNBT, trinketsResult),
            "The player's main inventory should contain the Trinkets items after retrieving the grave."
        );
    }

    // NOTE - Helper functions
    private static boolean checkBasicRetrieval(PlayerEntity player, BlockPos pos, String inventorySNBT) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);

        // Set player inventory based on SNBT string (and get for comparison)
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, inventorySNBT);
        String playerInventoryBefore = GraveTestNBTHelper.getPlayerInventorySNBT(player);

        // Place the grave and clear the player's inventory for retrieval purposes
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        GraveTestHelper.clearPlayerInventory(player);

        // Retrieve it and ensure the before and after are the same
        RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        String playerInventoryAfter = GraveTestNBTHelper.getPlayerInventorySNBT(player);
        return GraveTestNBTHelper.compareInventoriesSNBT(playerInventoryBefore, playerInventoryAfter);
    }

    private static boolean checkMerge(PlayerEntity player, BlockPos pos, String currentInv, String graveInv,
                                String resultInv) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);

        // Generate the Grave with graveInv
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, graveInv);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        GraveTestHelper.clearPlayerInventory(player);

        // Set the player's inventory to currentInv and retrieve the grave
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, currentInv);
        RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

        // Retrieve it and ensure the before and after are the same
        String playerInventorySNBT = GraveTestNBTHelper.getPlayerInventorySNBT(player);
        return GraveTestNBTHelper.compareInventoriesSNBT(resultInv, playerInventorySNBT);
    }

    private static void checkOverflow(TestContext context, PlayerEntity player, BlockPos pos, String currentInv,
                                      String graveInv, Item item, int count) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);

        // Generate the Grave with graveInv
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, graveInv);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        GraveTestHelper.clearPlayerInventory(player);

        // Set the player's inventory to currentInv and retrieve the grave
        GraveTestNBTHelper.setPlayerInventoryFromSNBT(player, currentInv);
        RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);

        context.expectItemsAt(item, new BlockPos(3, 2, 3), 2, count);
        context.killAllEntities(ItemEntity.class); // cleans up dropped items
        GraveTestHelper.clearPlayerInventory(player);
    }

    private static boolean checkUnloadedInventories(TestContext context, PlayerEntity player, BlockPos pos,
                                                    String graveSNBT, String expectedResult) {
        GraveTestHelper.runCommand(context,
            String.format("/setblock %d %d %d forgottengraves:grave%s", pos.getX(), pos.getY(), pos.getZ(), graveSNBT)
        );
        RetrieveGrave.retrieveWithInteract(player, GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        String playerInventorySNBT = GraveTestNBTHelper.getPlayerInventorySNBT(player);
        GraveTestHelper.clearPlayerInventory(player);
        return GraveTestNBTHelper.compareInventoriesSNBT(playerInventorySNBT, expectedResult);
    }
}
