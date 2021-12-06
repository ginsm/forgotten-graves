package me.mgin.graves.block;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Ageable<T extends Enum<T>> {
	int field_31056 = 4;

	Optional<BlockState> getDegradationResultState(BlockState state);

	float getDegradationChanceMultiplier();

	default void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		float f = 0.05688889F;
		if (random.nextFloat() < f) {
			this.tryDegrade(state, world, pos, random);
		}
	}

	T getDegradationLevel();

	default void tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int i = this.getDegradationLevel().ordinal();
		int j = 0;
		int k = 0;
		Iterator<BlockPos> var8 = BlockPos.iterateOutwards(pos, 4, 4, 4).iterator();

		while (var8.hasNext()) {
			BlockPos nextPos = (BlockPos) var8.next();
			int l = nextPos.getManhattanDistance(pos);
			if (l > 4) {
				break;
			}

			if (!nextPos.equals(pos)) {
				BlockState newState = world.getBlockState(pos);
				Block block = newState.getBlock();
				if (block instanceof Degradable) {
					Enum<?> enum_ = ((Degradable<?>) block).getDegradationLevel();
					if (this.getDegradationLevel().getClass() == enum_.getClass()) {
						int m = enum_.ordinal();
						if (m < i) {
							return;
						}

						if (m > i) {
							++k;
						} else {
							++j;
						}
					}
				}
			}
		}
		float f = (float) (k + 1) / (float) (k + j + 1);
		float g = f * f * this.getDegradationChanceMultiplier();
		if (random.nextFloat() < g) {
			this.getDegradationResultState(state).ifPresent((statex) -> setDegradationState(world, pos, statex));
		}
	}

	static void setDegradationState(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof GraveBlockEntity entity) {
			GameProfile owner = entity.getGraveOwner();
			String name = entity.getCustomName();
			DefaultedList<ItemStack> items = entity.getItems();
			int xp = entity.getXp();
			int noAge = entity.getNoAge();
			String graveSkull = entity.getGraveSkull();

			world.setBlockState(pos, state);

			GraveBlockEntity newGraveBlockEntity = new GraveBlockEntity(pos, state);
			newGraveBlockEntity.setGraveOwner(owner);
			newGraveBlockEntity.setItems(items);
			newGraveBlockEntity.setCustomName(name);
			newGraveBlockEntity.setXp(xp);
			newGraveBlockEntity.setNoAge(noAge);
			newGraveBlockEntity.setGraveSkull(graveSkull);

			world.addBlockEntity(newGraveBlockEntity);
		}
	}
}
