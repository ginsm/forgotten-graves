package me.mgin.graves.gametest.tests;

import me.mgin.graves.gametest.GraveTestHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplosionTest {
    public static void resistsCreeper(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running resistsCreeper");
        String nbt = "{Fuse:1,ignited:1b}";
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:creeper %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }

    public static void resistsTNT(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running resistsTNT");
        String nbt = "{Fuse:1}";
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:tnt %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }

    public static void resistsEndCrystal(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running resistsEndCrystal");
        String nbt = "{ShowBottom: 0b}";
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:end_crystal %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:arrow %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }

    public static void resistsGhastFireball(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running resistsGhastFireball");
        String nbt = "{Motion:[0d,-5d,0d]}";
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:fireball %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }

    public static void resistsDragonFireball(TestContext context, PlayerEntity player, BlockPos pos) {
        System.out.println("📗 Running resistsDragonFireball");
        String nbt = "{Motion:[0d,-5d,0d]}";
        GraveTestHelper.runCommand(context,
            String.format("summon minecraft:dragon_fireball %d %d %d %s", pos.getX(), pos.getY() + 1, pos.getZ(), nbt)
        );
        GraveTestHelper.checkGraveExists(context, player, pos, World.OVERWORLD);
    }
}
