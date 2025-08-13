package me.mgin.graves.gametest.tests;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.decay.DecayStateManager;
import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.gametest.GraveTestHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

/**
 * There's no operator permission tests in this suite as the GameTest API doesn't allow for modifying the mock player's
 * permission levels (as far as I'm aware).
 */
public class PermissionTest {
    public static void noAccess(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        PlayerEntity player2 = context.createMockCreativePlayer();

        System.out.println("📗 Running noAccess");

        // Spawn a grave owned by another player
        GraveTestHelper.spawnEmptyGrave(player2, pos, world);

        // Attempt to retrieve as non-OP; should be unable to do so.
        RetrieveGrave.retrieveWithInteract(player, world, pos);
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }

    public static void graveRobbing(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running graveRobbing");

        // Enable grave robbing and attempt to claim the grave as OP; check if grave is gone
        config.server.graveRobbing = true;
        RetrieveGrave.retrieveWithInteract(player, world, pos);
        GraveTestHelper.checkGraveDoesntExist(context, player, pos, World.OVERWORLD);
        config.server.graveRobbing = false;
    }

    public static void ownedGrave(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);

        System.out.println("📗 Running ownedGrave");

        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player);
        RetrieveGrave.retrieveWithInteract(player, world, pos);
        GraveTestHelper.checkGraveDoesntExist(context, player, pos, World.OVERWORLD);
    }

    public static void decayRobbing(TestContext context, PlayerEntity player, BlockPos pos) {
        World world = GraveTestHelper.getWorld(player, World.OVERWORLD);
        PlayerEntity player2 = context.createMockCreativePlayer();
        GravesConfig config = GravesConfig.getConfig();

        System.out.println("📗 Running decayRobbing");

        // Configure the mod for decay robbing testing
        config.decay.decayEnabled = false;
        config.server.graveRobbing = true;
        config.decay.decayRobbing = DecayingGrave.BlockDecay.WEATHERED;

        // Place three graves in a row
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos), player2);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos.east()), player2);
        PlaceGrave.place(world, GraveTestHelper.posToVec3d(pos.west()), player2);

        // Set the decay state of the graves
        GraveTestHelper.setGraveDecay(world, pos, GraveBlocks.GRAVE_OLD);
        GraveTestHelper.setGraveDecay(world, pos.east(), GraveBlocks.GRAVE_WEATHERED);

        // Attempt retrieval of each one; only eastern one should be retrieved
        RetrieveGrave.retrieveWithInteract(player, world, pos.west());
        RetrieveGrave.retrieveWithInteract(player, world, pos);
        RetrieveGrave.retrieveWithInteract(player, world, pos.east());

        // Only the eastern one should be retrieved
        GraveTestHelper.checkGraveExists(context, player, pos.west(), World.OVERWORLD);
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
        GraveTestHelper.checkGraveDoesntExist(context, player, pos.east(), World.OVERWORLD);

        // Remove the remaining two graves
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos);
        GraveTestHelper.removeGrave(GraveTestHelper.getWorld(player, World.OVERWORLD), pos.west());
    }
}
