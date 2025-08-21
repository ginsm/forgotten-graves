package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.event.server.DeathCompass;
import me.mgin.graves.gametest.GraveTest;
import me.mgin.graves.gametest.GraveTestHelper;
import me.mgin.graves.inventory.Vanilla;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.test.TestContext;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;

public class DeathCompassTest {

    public static void giveDeathCompass$false(TestContext context, PlayerEntity player, BlockPos pos) {
        GravesConfig config = GravesConfig.getConfig();
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        config.spawning.giveDeathCompass = false;
        config.main.graveCoordinates = GraveTest.verbose;

        System.out.println("📗 Running giveDeathCompass$false");

        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        DeathCompass.give(player, player, true);

        context.assertFalse(
                player.getInventory().containsAny(
                        Set.of(Items.COMPASS)
                ),
                "Player was given a compass with giveDeathCompass disabled."
        );

        RetrieveGrave.retrieveWithInteract(player, world, pos);
    }

    public static void giveDeathCompass$true(TestContext context, PlayerEntity player, BlockPos pos) {
        GravesConfig config = GravesConfig.getConfig();
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        config.spawning.giveDeathCompass = true;

        System.out.println("📗 Running giveDeathCompass$true");

        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        DeathCompass.give(player, player, true);

        // check for compass
        NbtCompound lastGrave = getLastGrave(player);
        context.assertTrue(
                hasDeathCompass(player, lastGrave),
                "Player should have death compass for their last grave but doesn't have one."
        );
    }

    public static void removeCompassFromInventory(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);

        System.out.println("📗 Running removeCompassFromInventory");

        // Get last grave's data
        NbtCompound lastGrave = getLastGrave(player);

        // Give a second, different compass
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos.east(1)), player);
        DeathCompass.give(player, player, true);

        // Retrieve and make sure compass for last grave is gone
        RetrieveGrave.retrieveWithInteract(player, world, pos);

        context.assertFalse(
                hasDeathCompass(player, lastGrave),
                "Player should no longer have the previous grave's compass."
        );

        context.assertTrue(
                hasDeathCompass(player, getLastGrave(player)),
                "Player should have the second compass still"
        );

        // Make sure to clean up the second grave too
        RetrieveGrave.retrieveWithInteract(player, world, pos.east(1));
    }

    public static void pointsToDeathPosWhenGravesDisabled(TestContext context, PlayerEntity player) {
        GravesConfig config = GravesConfig.getConfig();
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        config.main.graves = false;

        System.out.println("📗 Running removeCompassFromInventory");

        DeathCompass.give(player, player, true);
        ItemStack stack = player.getInventory().main.get(0);

        if (stack.getItem() instanceof CompassItem && stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains("GraveMarker")) {
                BlockPos pos = NbtHelper.toBlockPos((NbtCompound) nbt.get("LodestonePos"));

                Optional<GlobalPos> lastDeath = player.getLastDeathPos();
                if (lastDeath.isPresent()) {
                    GlobalPos globalPos = lastDeath.get();
                    BlockPos lastDeathPos = globalPos.getPos();

                    context.assertTrue(
                            pos.equals(lastDeathPos),
                            "Compass should point to the last death's BlockPos."
                    );
                }
            }
        }

        player.getInventory().main.set(0, ItemStack.EMPTY);
    }

    public static NbtCompound getLastGrave(PlayerEntity player) {
        PlayerState state = ServerState.getPlayerState(player.getServer(), player.getUuid());
        return (NbtCompound) state.graves.get(state.graves.size() - 1);
    }

    public static boolean hasDeathCompass(PlayerEntity player, NbtCompound lastGrave) {
        long msTime = lastGrave.getLong("mstime");

        DefaultedList<ItemStack> items = new Vanilla().getInventory(player);

        for (ItemStack item : items) {
            if (item.hasNbt()) {
                NbtCompound nbt = item.getNbt();
                if (nbt != null && nbt.contains("GraveMarker")) {
                    if (nbt.getLong("GraveMarker") == msTime) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
