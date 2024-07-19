package me.mgin.graves.block.decay;

import com.mojang.authlib.GameProfile;
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

import java.util.Optional;

public interface Decayable<T extends Enum<T>> {
    Optional<BlockState> getDecayResultState(BlockState state);

    float getDecayChanceMultiplier();

    default void tickDecay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        GravesConfig config = GravesConfig.getConfig();

        if (config.decay.decayEnabled) {
            float f = 0.05688889F;
            if (random.nextFloat() < f) {
                this.tryDecay(state, world, pos, random);
            }
        }
    }

    T getDecayStage();

    default void tryDecay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.getDecayChanceMultiplier() > random.nextFloat()) {
            this.getDecayResultState(state).ifPresent((statex) -> setDecayState(world, pos, statex, true));
        }
    }

    /**
     * Calculates the decay rate for an item based on its current health percentage, using an S-curve.
     *
     * @param healthPercent The percentage of health the item has remaining.
     * @param min The minimum decay percent.
     * @param max The maximum decay percent.
     * @return The amount the item should decay (percentage-wise). Multiply the item's durability to the result of this.
     */
    static float calculateItemDecayPercent(float healthPercent, float min, float max) {
        float steepness = 8.0f; // Control the steepness of the curve
        float midpoint = 0.5f; // Midpoint of the curve
        // Utilizes a logistic function for the curve
        float decayPercent = (float) (1 / (1 + Math.exp(-steepness * (healthPercent - midpoint))));
        return max - (min + (max - min) * decayPercent);
    }

    static DefaultedList<ItemStack> decayItems(DefaultedList<ItemStack> items) {
        GravesConfig config = GravesConfig.getConfig();
        float modifier = config.decay.decayModifier / 100f;
        boolean decayBreaksItems = config.decay.decayBreaksItems;

        // Do not decay items if the modifier is 0.
        if (modifier == 0.00f) return items;

        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int maxDamage = item.getMaxDamage();
            int damage = item.getDamage();
            Random random = Random.create();

            // Only decay items with a maxDamage.
            if (maxDamage > 0) {
                // Gets the decay percentage based on the item's remaining health.
                float itemHealthPercent = 1 - ((float) damage / (float) maxDamage);
                float decayPercent = calculateItemDecayPercent(itemHealthPercent, 0f, modifier);

                // Adds randomness, ranging between -0.02f and 0.02f.
                float randomness = (random.nextFloat() - 0.5f) * 0.04f;
                if (decayPercent + randomness >= 0.0f) { // Prevents going into the negatives and healing the item.
                    decayPercent += randomness;
                }

                // Unbreaking reduces the chance of an item decaying.
                float unbreaking = (float) EnchantmentHelper.getLevel(Enchantments.UNBREAKING, item);
                float unbreakingModifier = ((100f / (unbreaking + 1f)) / 100f);

                // Attempt to decay the item.
                float decayChance = 0.35f * unbreakingModifier;
                if (decayChance >= random.nextFloat()) {
                    int remainingDurability = maxDamage - damage;
                    float decay = (float) remainingDurability * decayPercent;

                    // Ensure the item hasn't broken, otherwise either remove the item (decayBreaksItems) or set it
                    // to one health.
                    if (remainingDurability - decay >= 1.0f) {
                        item.setDamage((int) Math.ceil(damage + decay));
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

            // Decay inventories (if enabled) and store them.
            for (InventoriesApi api : Graves.inventories) {
                String id = api.getID();
                DefaultedList<ItemStack> inventory = graveEntity.getInventory(id);
                if (inventory == null)  continue;
                graveEntity.setInventory(id, itemsDecay ? decayItems(inventory) : inventory);
            }

            world.addBlockEntity(graveEntity);
        }
    }
}
