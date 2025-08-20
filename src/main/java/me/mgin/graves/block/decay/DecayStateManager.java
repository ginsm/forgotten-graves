package me.mgin.graves.block.decay;

import java.util.Optional;

import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DecayStateManager {

    static public boolean decreaseDecayState(World world, BlockPos pos) {
        if (world.isClient) return false;

        Optional<BlockState> potentialNewState = DecayingGrave.getDecreasedDecayState(world.getBlockState(pos));
        return setDecayState(world, pos, potentialNewState, false);
    }

    static public boolean increaseDecayState(World world, BlockPos pos) {
        if (world.isClient) return false;

        Optional<BlockState> potentialNewState = DecayingGrave.getIncreasedDecayState(world.getBlockState(pos));
        return setDecayState(world, pos, potentialNewState, true);
    }

    static public boolean setDecayState(World world, BlockPos pos, Optional<BlockState> potentialNewState,
                                        boolean itemsDecay) {
        if (potentialNewState.isPresent()) {
            GraveBlockEntity entity = (GraveBlockEntity) world.getBlockEntity(pos);
            if (entity != null) {
                entity.resetTimer("decay");
                Decayable.setDecayState(world, pos, potentialNewState.get(), itemsDecay);
                return true;
            }
        }
        return false;
    }

}
