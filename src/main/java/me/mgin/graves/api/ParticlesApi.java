package me.mgin.graves.api;

import java.util.Random;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticlesApi {
	private static Random rng = new Random();

	static public void spawnAtBlock(World world, BlockPos blockPos, DefaultParticleType type, int amount,
			double maxVelocity) {
		for (int i = 0; i <= amount; i++) {
			double blockX = blockPos.getX() + rng.nextDouble();
			double blockY = blockPos.getY() + rng.nextDouble();
			double blockZ = blockPos.getZ() + rng.nextDouble();

			double velocityX = rng.nextDouble() * (maxVelocity - 1);
			double velocityY = rng.nextDouble() * (maxVelocity - 1);
			double velocityZ = rng.nextDouble() * (maxVelocity - 1);

			world.addParticle(type, blockX, blockY, blockZ, velocityX, velocityY, velocityZ);
		}
	}

	/**
	 * Spawn particles at the bottom of a block
	 *
	 * @param world
	 * @param blockPos
	 * @param type
	 * @param amount
	 * @param maxVelocity
	 * @param maxStartHeight
	 */
	static public void spawnAtBlockBottom(World world, BlockPos blockPos, DefaultParticleType type, int amount,
			double maxVelocity, double maxStartHeight) {
		for (int i = 0; i <= amount; i++) {
			double blockX = blockPos.getX() + rng.nextDouble();
			double blockY = Math.min(blockPos.getY() + rng.nextDouble(), blockPos.getY() + maxStartHeight);
			double blockZ = blockPos.getZ() + rng.nextDouble();

			double velocityX = rng.nextDouble() * maxVelocity;
			double velocityY = rng.nextDouble() * maxVelocity;
			double velocityZ = rng.nextDouble() * maxVelocity;

			world.addParticle(type, blockX, blockY, blockZ, velocityX, velocityY, velocityZ);
		}
	}
}
