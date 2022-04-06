package me.mgin.graves.block.degradation;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
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
			this.getDegradationResultState(state).ifPresent((statex) -> setDegradationState(world, pos, statex, true));
		}
	}

	static DefaultedList<ItemStack> decayItems(DefaultedList<ItemStack> items, GameProfile profile) {
		float maxDecayPercent = GravesConfig.resolveConfig("maxDecayPercent", profile).itemDecay.maxDecayPercent / 100f;
		boolean itemDecayBreaksItems = GravesConfig.resolveConfig("itemDecayBreaksItems",
				profile).itemDecay.itemDecayBreaksItems;

		if (maxDecayPercent == 0.0f)
			return items;

		for (int i = 0; i < items.size(); i++) {
			ItemStack item = items.get(i);
			int maxDamage = item.getMaxDamage();
			int damage = item.getDamage();

			// item has durability
			if (maxDamage > 0) {
				Random random = new Random();
				float unbreaking = (float) EnchantmentHelper.getLevel(Enchantments.UNBREAKING, item);

				float currentItemDecay = (float) damage / (float) maxDamage;

				if (currentItemDecay == 0.0f) {
					currentItemDecay = 1f / (float) maxDamage;
				}

				float decayPercent = maxDecayPercent * currentItemDecay;
				float unbreakingModifier = (((100f / (unbreaking + 1f)) / 100f));
				float decayChance = 0.35f * unbreakingModifier;

				if (decayChance >= random.nextFloat()) {
					int remainingDamage = maxDamage - damage;
					int decay = (int) Math.ceil((float) remainingDamage * decayPercent);

					if (remainingDamage - decay > 0) {
						item.setDamage((int) damage + decay);
					} else if (itemDecayBreaksItems) {
						items.set(i, ItemStack.EMPTY);
					} else {
						item.setDamage(maxDamage - 1);
					}
				}
			}
		}
		return items;
	}

	public static void setDegradationState(World world, BlockPos pos, BlockState state, boolean itemsDecay) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof GraveBlockEntity graveEntity) {
			world.setBlockState(pos, state);

			GraveBlockEntity newGraveEntity = new GraveBlockEntity(pos, state);
			GameProfile owner = graveEntity.getGraveOwner();

			// Decay inventotries (if enabled) and store them
			for (InventoriesApi api : Graves.inventories) {
				String id = api.getID();
				DefaultedList<ItemStack> inventory = graveEntity.getInventory(id);

				if (inventory == null)
					continue;

				newGraveEntity.setInventory(id, itemsDecay ? decayItems(inventory, owner) : inventory);
			}

			// Transfer previous data
			newGraveEntity.setGraveOwner(owner);
			newGraveEntity.setCustomName(graveEntity.getCustomName());
			newGraveEntity.setXp(graveEntity.getXp());
			newGraveEntity.setNoAge(graveEntity.getNoAge());
			newGraveEntity.setGraveSkull(graveEntity.getGraveSkull());

			world.addBlockEntity(newGraveEntity);
		}
	}
}
