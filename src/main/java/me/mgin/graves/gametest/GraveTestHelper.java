package me.mgin.graves.gametest;

import me.mgin.graves.block.GraveBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class GraveTestHelper {
    public static void runCommand(TestContext context, String command) {
        MinecraftServer server = context.getWorld().getServer();
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
    }

    public static ActionResult useStackOnBlock(PlayerEntity player, ItemStack stack, BlockPos pos) {
        teleportPlayer(player, pos);
        BlockHitResult blockHitResult = new BlockHitResult(Vec3d.ofCenter(pos), Direction.DOWN, pos, true);
        ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult);
        return stack.useOnBlock(itemUsageContext);
    }

    public static void checkGraveExists(TestContext context, PlayerEntity player, BlockPos pos, RegistryKey<World> key) {
        System.out.println(">> Running " + Thread.currentThread().getStackTrace()[2].getMethodName() + " <<");
        World world = getWorld(player, key);
        if (world != null) {
            Block block = world.getBlockState(pos).getBlock();
            String errorMessage = "Expect block to be a grave block at " + pos + " got " + block.getName()
                + " in " + key.getValue();
            context.assertTrue(block instanceof GraveBlockBase, errorMessage);
        }
    }

    public static World getWorld(PlayerEntity player, RegistryKey<World> key) {
        return Objects.requireNonNull(player.getServer()).getWorld(key);
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
