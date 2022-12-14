package me.mgin.graves.util;

import java.util.Optional;

import me.mgin.graves.block.degradation.Ageable;
import me.mgin.graves.block.degradation.AgingGrave;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DegradationStateManager {

	static public boolean decreaseDegradationState(World world, BlockPos pos) {
		if (world.isClient)
			return false;

		Optional<BlockState> potentialNewState = AgingGrave.getDecreasedOxidationState(world.getBlockState(pos));
		return setDegradationState(world, pos, potentialNewState, false);
	}

	static public boolean increaseDegradationState(World world, BlockPos pos) throws Exception {
		if (world.isClient)
			return false;

		Optional<BlockState> potentialNewState = AgingGrave.getIncreasedOxidationState(world.getBlockState(pos));
		return setDegradationState(world, pos, potentialNewState, true);
	}

	static public boolean setDegradationState(World world, BlockPos pos, Optional<BlockState> potentialNewState,
			boolean itemsDecay) {
		if (potentialNewState.isPresent()) {
			Ageable.setDegradationState(world, pos, potentialNewState.get(), itemsDecay);
			return true;
		}
		return false;
	}

}
