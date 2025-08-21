package me.mgin.graves.gametest;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.decay.DecayStateManager;
import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.block.utility.PlaceGrave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class GraveTestHelper {
    public static void runCommand(TestContext context, String command) {
        MinecraftServer server = context.getWorld().getServer();
        ServerCommandSource source = GraveTest.verbose ? server.getCommandSource() : server.getCommandSource().withSilent();
        server.getCommandManager().executeWithPrefix(source, command);
    }

    public static void checkGraveExists(TestContext context, PlayerEntity player, BlockPos pos, RegistryKey<World> key) {
        World world = getWorld(player, key);
        if (world != null) {
            Block block = world.getBlockState(pos).getBlock();
            String errorMessage = "Expect block to be a grave block at " + pos + " got " + block.getName()
                + " in " + key.getValue();
            context.assertTrue(block instanceof GraveBlockBase, errorMessage);
        }
    }

    public static void checkGraveDoesntExist(TestContext context, PlayerEntity player, BlockPos pos, RegistryKey<World> key) {
        World world = getWorld(player, key);
        if (world != null) {
            Block block = world.getBlockState(pos).getBlock();
            String errorMessage = "Expected block at " + pos + " in " + key.getValue() + " to not be a grave block.";
            context.assertFalse(block instanceof GraveBlockBase, errorMessage);
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

    public static void clearPlayerInventory(PlayerEntity player) {
        player.getInventory().clear();
    }

    public static Integer getAmountOfItemsInInventory(PlayerEntity player) {
        int amountOfItems = 0;
        for (ItemStack itemStack : player.getInventory().main) {
            if (!itemStack.isEmpty()) amountOfItems += 1;
        }
        return amountOfItems;
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

    public static void spawnEmptyGrave(PlayerEntity player, BlockPos pos, World world) {
        clearPlayerInventory(player);
        PlaceGrave.place(world, posToVec3d(pos), player);
    }

    public static void removeGrave(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof GraveBlockBase graveBlock) {
            graveBlock.setBrokenByPlayer(true);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public static void resetGraveDecay(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof GraveBlockBase graveBlock) {
            DecayStateManager.setDecayState(
                world, pos, Optional.ofNullable(GraveBlocks.GRAVE.getDefaultState()), false
            );
        }
    }

    public static void setGraveDecay(World world, BlockPos pos, GraveBlockBase base) {
        DecayStateManager.setDecayState(
            world, pos, Optional.ofNullable(base.getDefaultState()), false
        );
    }

    public static boolean compareDecayLevel(World world, BlockPos pos, DecayingGrave.BlockDecay expectedDecayLevel) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof GraveBlockBase graveBlock) {
            DecayingGrave.BlockDecay decay = graveBlock.getDecayStage();
            return decay == expectedDecayLevel;
        }

        return false;
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void printTestStarting(String name) {
        System.out.print("\n");
        System.out.println("======== Running " + name + " Tests ========");
    }

    public static void printTestEnding(String name) {
        System.out.println("======= Finished " + name + " Tests =======" + System.lineSeparator() + " ");
    }

    public static void setGamerule(PlayerEntity player, GameRules.Key<GameRules.BooleanRule> rule, boolean value) {
        MinecraftServer server = player.getServer();
        if (server != null) {
           GameRules.BooleanRule gamerule = server.getGameRules().get(rule);
           if (gamerule.get() != value) {
               gamerule.set(value, server);
           }
        }
    }
}
