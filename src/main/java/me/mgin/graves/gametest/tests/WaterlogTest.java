package me.mgin.graves.gametest.tests;

import me.mgin.graves.gametest.GraveTestHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.Properties;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterlogTest {
    public static void waterlogged(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running waterlogged");
        RegistryKey<World> key = World.OVERWORLD;
        World world = GraveTestHelper.getWorld(player, key);
        if (world != null) {
            world.setBlockState(pos, world.getBlockState(pos).with(Properties.WATERLOGGED, true));
            BlockState state = world.getBlockState(pos);
            boolean waterlogged = state.get(Properties.WATERLOGGED);
            String errorMessage = "Expected block to be waterlogged at " + pos + " in " + key.getValue()
                + " but it was not waterlogged.";
            context.assertTrue(waterlogged, errorMessage);
        }
    }

    public static void notWaterlogged(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running notWaterlogged");
        RegistryKey<World> key = World.OVERWORLD;
        World world = GraveTestHelper.getWorld(player, key);
        if (world != null) {
            world.setBlockState(pos, world.getBlockState(pos).with(Properties.WATERLOGGED, false));
            BlockState state = world.getBlockState(pos);
            boolean waterlogged = state.get(Properties.WATERLOGGED);
            String errorMessage = "Expected block to not be waterlogged at " + pos + " in " + key.getValue()
                + " but it was waterlogged.";
            context.assertFalse(waterlogged, errorMessage);
        }
    }
}
