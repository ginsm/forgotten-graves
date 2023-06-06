package me.mgin.graves.block.decay;

import com.mojang.authlib.GameProfile;

import java.util.Optional;

import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface Decayable<T extends Enum<T>> {
    Optional<BlockState> getDecayResultState(BlockState state);

    float getDecayChanceMultiplier();

    default void tickDecay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float f = 0.05688889F;
        if (random.nextFloat() < f) {
            this.tryDecay(state, world, pos, random);
        }
    }

    T getDecayStage();

    default void tryDecay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.getDecayChanceMultiplier() > random.nextFloat()) {
            this.getDecayResultState(state).ifPresent((statex) -> setDecayState(world, pos, statex, true));
        }
    }

    static DefaultedList<ItemStack> decayItems(DefaultedList<ItemStack> items, GameProfile profile) {
        float decayModifier = GravesConfig.resolveConfig("decayModifier", profile).itemDecay.decayModifier / 100f;
        boolean decayBreaksItems = GravesConfig.resolveConfig("decayBreaksItems", profile).itemDecay.decayBreaksItems;

        if (decayModifier == 0.0f)
            return items;

        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int maxDamage = item.getMaxDamage();
            int damage = item.getDamage();

            // item has durability
            if (maxDamage > 0) {
                Random random = Random.create();
                float unbreaking = (float) EnchantmentHelper.getLevel(Enchantments.UNBREAKING, item);

                float currentItemDecay = (float) damage / (float) maxDamage;

                if (currentItemDecay == 0.0f) {
                    currentItemDecay = 1f / (float) maxDamage;
                }

                float decayPercent = decayModifier * currentItemDecay;
                float unbreakingModifier = ((100f / (unbreaking + 1f)) / 100f);
                float decayChance = 0.35f * unbreakingModifier;

                if (decayChance >= random.nextFloat()) {
                    int remainingDamage = maxDamage - damage;
                    int decay = (int) Math.ceil((float) remainingDamage * decayPercent);

                    if (remainingDamage - decay > 0) {
                        item.setDamage(damage + decay);
                    } else if (decayBreaksItems) {
                        items.set(i, ItemStack.EMPTY);
                    } else {
                        item.setDamage(maxDamage - 1);
                    }
                }
            }
        }
        return items;
    }

    static void setDecayState(World world, BlockPos pos, BlockState state, boolean itemsDecay) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            world.setBlockState(pos, state);

            GraveBlockEntity newGraveEntity = new GraveBlockEntity(pos, state);
            GameProfile owner = graveEntity.getGraveOwner();

            // Transfer old grave entity's data to the new one
            newGraveEntity.readNbt(graveEntity.toNbt());

            // Decay inventotries (if enabled) and store them
            for (InventoriesApi api : Graves.inventories) {
                String id = api.getID();
                DefaultedList<ItemStack> inventory = graveEntity.getInventory(id);

                if (inventory == null)
                    continue;

                newGraveEntity.setInventory(id, itemsDecay ? decayItems(inventory, owner) : inventory);
            }

            world.addBlockEntity(newGraveEntity);
        }
    }
}
