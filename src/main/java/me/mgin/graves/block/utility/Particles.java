package me.mgin.graves.block.utility;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//? if >=1.20.5 {
import net.minecraft.particle.ParticleEffect;
//?} else {
/*import net.minecraft.particle.DefaultParticleType;
*///?}

public class Particles {
    private static final Random rng = new Random();

    //? if >=1.20.5 {
    static public void spawnAtBlock(World world, BlockPos pos, ParticleEffect type, int amount,
                                    double maxVelocity) {
    //?} else {
    /*static public void spawnAtBlock(World world, BlockPos pos, DefaultParticleType type, int amount,
                                    double maxVelocity) {*/
    //?}
        for (int i = 0; i <= amount; i++) {
            double blockX = pos.getX() + rng.nextDouble();
            double blockY = pos.getY() + rng.nextDouble();
            double blockZ = pos.getZ() + rng.nextDouble();

            double velocityX = rng.nextDouble() * (maxVelocity - 1);
            double velocityY = rng.nextDouble() * (maxVelocity - 1);
            double velocityZ = rng.nextDouble() * (maxVelocity - 1);

            world.addParticle(type, blockX, blockY, blockZ, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawn particles at the bottom of a block
     */
    //? if >=1.20.5 {
    static public void spawnAtBlockBottom(World world, BlockPos pos, ParticleEffect type, int amount,
                                          double maxVelocity, double maxStartHeight) {
    //?} else {
    /*static public void spawnAtBlockBottom(World world, BlockPos pos, DefaultParticleType type, int amount,
                                          double maxVelocity, double maxStartHeight) {*/
    //?}
        for (int i = 0; i <= amount; i++) {
            double blockX = pos.getX() + rng.nextDouble();
            double blockY = Math.min(pos.getY() + rng.nextDouble(), pos.getY() + maxStartHeight);
            double blockZ = pos.getZ() + rng.nextDouble();

            double velocityX = rng.nextDouble() * maxVelocity;
            double velocityY = rng.nextDouble() * maxVelocity;
            double velocityZ = rng.nextDouble() * maxVelocity;

            world.addParticle(type, blockX, blockY, blockZ, velocityX, velocityY, velocityZ);
        }
    }
}
