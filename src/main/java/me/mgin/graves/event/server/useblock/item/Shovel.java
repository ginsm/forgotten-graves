package me.mgin.graves.event.server.useblock.item;

import me.mgin.graves.block.decay.DecayStateManager;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Particles;
import me.mgin.graves.block.utility.Permission;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class Shovel {
    /**
     * This event handler determines whether a shovel is in the main hand,
     * whether the grave is unowned or is owned by the player, and then reduces
     * the amount of decay on the grave if all criteria is met.
     *
     * @param player PlayerEntity
     * @param world World
     * @param hand Hand
     * @param pos BlockPos
     * @param item Item
     * @param entity GraveBlockEntity
     * @return Boolean
     */
    public static Boolean handle(PlayerEntity player, World world, Hand hand, BlockPos pos,
                                 Item item, GraveBlockEntity entity) {
        // Cases in which an early termination is necessary
        boolean isShovelItem = item instanceof ShovelItem;
        boolean canRetrieve = Permission.playerCanAttemptRetrieve(player, entity);
        boolean isMainHand = hand.equals(Hand.MAIN_HAND);
        boolean canDecay = entity.getNoDecay() == 0;

        if (!isShovelItem || !canRetrieve || !isMainHand) return false;

        // This will cause the code to prioritize removing the Honeycomb effect if present.
        // Otherwise, it will decrease the decay state.
        if (!canDecay || DecayStateManager.decreaseDecayState(world, pos)) {
            Random random = new Random();

            // Remove honeycomb
            entity.setNoDecay(0);

            // Damage the item, respecting unbreaking enchant and creative
            float unbreaking = (float) EnchantmentHelper.getLevel(Enchantments.UNBREAKING, player.getActiveItem());
            float breakChance = ((100f / (unbreaking + 1f)) / 100f);

            if (!player.isCreative() && breakChance >= random.nextFloat()) {
                player.getStackInHand(hand).damage(1, player, (p) -> p.sendToolBreakStatus(hand));
            }

            // Spawn particles and sound in world
            Particles.spawnAtBlock(world, pos, ParticleTypes.WAX_OFF, 8, 3);
            world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundCategory.BLOCKS, 1f, 1f);
            return true;
        }

        return false;
    }
}
