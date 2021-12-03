package me.mgin.graves.util;

import java.util.Random;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Particles {
	static public void spawnAtBlock(DefaultParticleType type, int amount, int maxVelocity, World world,
			BlockPos blockPos) {
		Random rng = new Random();

		for (int i = 0; i <= amount; i++) {
			double blockX = blockPos.getX() + rng.nextDouble();
			double blockY = blockPos.getY() + rng.nextDouble();
			double blockZ = blockPos.getZ() + rng.nextDouble();

			double velocityX = rng.nextDouble() * (maxVelocity - 1);
			double velocityY = rng.nextDouble() * (maxVelocity - 1);
			double velocityZ = rng.nextDouble() * (maxVelocity - 1);
			System.out.println("Spawning particles at: " + blockX + " " + blockY + " " + blockZ);

			world.addParticle(type, blockX, blockY, blockZ, velocityX, velocityY, velocityZ);
		}

	}
}
