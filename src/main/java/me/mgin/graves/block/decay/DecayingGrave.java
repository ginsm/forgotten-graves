package me.mgin.graves.block.decay;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Optional;
import java.util.function.Supplier;

import me.mgin.graves.block.GraveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public interface DecayingGrave extends Decayable<DecayingGrave.BlockDecay> {
    Supplier<ImmutableBiMap<Object, Object>> BLOCK_DECAY_INCREASES = Suppliers.memoize(() ->
        ImmutableBiMap.builder()
            .put(GraveBlocks.GRAVE, GraveBlocks.GRAVE_OLD)
            .put(GraveBlocks.GRAVE_OLD, GraveBlocks.GRAVE_WEATHERED)
            .put(GraveBlocks.GRAVE_WEATHERED, GraveBlocks.GRAVE_FORGOTTEN)
            .build()
    );

    Supplier<BiMap<Object, Object>> BLOCK_DECAY_DECREASES = Suppliers.memoize(() ->
        ((BiMap<Object, Object>) BLOCK_DECAY_INCREASES.get()).inverse()
    );

    static Optional<Block> getDecreasedDecayBlock(Block block) {
        return Optional.ofNullable((Block) BLOCK_DECAY_DECREASES.get().get(block));
    }

    static Optional<BlockState> getDecreasedDecayState(BlockState state) {
        return getDecreasedDecayBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
    }

    static Optional<Block> getIncreasedDecayBlock(Block block) {
        return Optional.ofNullable((Block) ((BiMap<Object, Object>) BLOCK_DECAY_INCREASES.get()).get(block));
    }

    static Optional<BlockState> getIncreasedDecayState(BlockState state) {
        return getIncreasedDecayBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
    }

    default Optional<BlockState> getDecayResultState(BlockState state) {
        return getIncreasedDecayBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
    }

    default float getDecayChanceMultiplier() {
        return this.getDecayStage() == BlockDecay.FRESH ? 0.75F : 1.0F;
    }

    enum BlockDecay {
        FRESH, OLD, WEATHERED, FORGOTTEN;

        BlockDecay() {
        }
    }
}
