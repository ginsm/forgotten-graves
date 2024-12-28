package me.mgin.graves.gametest;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.block.utility.RetrieveGrave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class GraveTestHelper {
    public static void runCommand(TestContext context, String command) {
        MinecraftServer server = context.getWorld().getServer();
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
    }

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
    public static void placeGraveCheck(TestContext context, PlayerEntity player, BlockPos pos, BlockPos endPos,
                                       RegistryKey<World> worldKey) {
        System.out.println(">> Running " + Thread.currentThread().getStackTrace()[2].getMethodName() + " <<");
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
    public static void gravesDisabledCheck(TestContext context, PlayerEntity player, BlockPos pos, RegistryKey<World> worldKey) {
        System.out.println(">> Running " + Thread.currentThread().getStackTrace()[2].getMethodName() + " <<");
        World world = Objects.requireNonNull(player.getServer()).getWorld(worldKey);
        if (world != null) {
            // Give player items to check for when player dies
            ItemStack stack = Items.DIAMOND_BLOCK.getDefaultStack();
            stack.setCount(5);
            player.giveItemStack(stack);

            // Teleport and kill player
            BlockPos abs = context.getAbsolutePos(pos);
            teleportPlayer(player, abs);
            dropMockPlayerInventory(player, world, abs); // mock players don't drop their inventories upon death
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

    public static void teleportPlayer(PlayerEntity player, BlockPos pos) {
        if (player != null) {
            player.setPosition(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static void dropMockPlayerInventory(PlayerEntity player, World world, BlockPos pos) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    stack.copy());
                world.spawnEntity(itemEntity);
            }
        }
        player.getInventory().clear();
    }

    public static void removeGrave(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof GraveBlockBase graveBlock) {
            graveBlock.setBrokenByPlayer(true);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}
