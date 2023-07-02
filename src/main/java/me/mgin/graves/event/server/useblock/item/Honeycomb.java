package me.mgin.graves.event.server.useblock.item;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Particles;
import me.mgin.graves.block.utility.Permission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Honeycomb {

    /**
     * This event handler is used to prevent decay from occurring. If the grave is in a decayable
     * state, and the player has a honeycomb item in hand, it will consume the honeycomb and set
     * the grave to no longer decay.
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
        boolean isHoneycombItem = item instanceof HoneycombItem;
        boolean canRetrieve = Permission.playerCanAttemptRetrieve(player, entity);
        boolean isMainHand = hand.equals(Hand.MAIN_HAND);
        boolean canDecay = entity.getNoDecay() == 0;

        if (!isHoneycombItem || !isMainHand || !canRetrieve || !canDecay) return false;

        // Deduct from stack and set no decay to true
        if (!player.isCreative()) player.getStackInHand(hand).decrement(1);
        entity.setNoDecay(1);

        // Polish
        Particles.spawnAtBlock(world, pos, ParticleTypes.WAX_ON, 8, 3);
        world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1f, 1f);

        return true;
    }
}


