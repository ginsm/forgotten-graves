package me.mgin.graves.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import me.mgin.graves.Graves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public interface AgingGrave extends Ageable<AgingGrave.BlockAge> {

	Supplier<ImmutableBiMap<Object, Object>> BLOCK_AGE_INCREASES = Suppliers.memoize(() -> {
		return ImmutableBiMap.builder().put(Graves.GRAVE, Graves.GRAVE_OLD)
				.put(Graves.GRAVE_OLD, Graves.GRAVE_WEATHERED).put(Graves.GRAVE_WEATHERED, Graves.GRAVE_FORGOTTEN)
				.build();
	});

	Supplier<BiMap<Object, Object>> BLOCK_AGE_DECREASES = Suppliers.memoize(() -> {
		return ((BiMap<Object, Object>) BLOCK_AGE_INCREASES.get()).inverse();
	});

	static Optional<Block> getDecreasedOxidationBlock(Block block) {
		return Optional.ofNullable((Block) ((BiMap<Object, Object>) BLOCK_AGE_DECREASES.get()).get(block));
	}

	static Block getUnaffectedOxidationBlock(Block block) {
		Block block2 = block;

		for (Block block3 = (Block) ((BiMap<Object, Object>) BLOCK_AGE_DECREASES.get())
				.get(block); block3 != null; block3 = (Block) ((BiMap<Object, Object>) BLOCK_AGE_DECREASES.get()).get(block3)) {
			block2 = block3;
		}

		return block2;
	}

	static Optional<BlockState> getDecreasedOxidationState(BlockState state) {
		return getDecreasedOxidationBlock(state.getBlock()).map((block) -> {
			return block.getStateWithProperties(state);
		});
	}

	static Optional<Block> getIncreasedOxidationBlock(Block block) {
		return Optional.ofNullable((Block) ((BiMap<Object, Object>) BLOCK_AGE_INCREASES.get()).get(block));
	}

	static BlockState getUnaffectedOxidationState(BlockState state) {
		return getUnaffectedOxidationBlock(state.getBlock()).getStateWithProperties(state);
	}

	/*
	 * default Optional<BlockEntity> getDegradationResultEntity(BlockEntity
	 * blockEntity) {
	 *
	 * return
	 * getIncreasedOxidationBlock(blockEntity.getWorld().getBlockState(blockEntity.
	 * getPos())).map((block) -> { return block; }); }
	 */

	default Optional<BlockState> getDegradationResultState(BlockState state) {
		return getIncreasedOxidationBlock(state.getBlock()).map((block) -> {
			return block.getStateWithProperties(state);
		});
	}

	default float getDegradationChanceMultiplier() {
		return this.getDegradationLevel() == AgingGrave.BlockAge.FRESH ? 0.75F : 1.0F;
	}

	public static enum BlockAge {
		FRESH, OLD, WEATHERED, FORGOTTEN;

		private BlockAge() {
		}
	}
}
