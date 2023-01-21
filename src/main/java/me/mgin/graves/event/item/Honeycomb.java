package me.mgin.graves.event.item;

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
    public static Boolean use(PlayerEntity player, World world, Hand hand, BlockPos pos,
                              Item item, GraveBlockEntity entity) {
        // Cases in which an early termination is necessary
        boolean isHoneycombItem = item instanceof HoneycombItem;
        boolean canRetrieve = Permission.playerCanAttemptRetrieve(player, entity);
        boolean isMainHand = hand.equals(Hand.MAIN_HAND);
        boolean canDecay = entity.getNoDecay() == 0;

        if (!isHoneycombItem || !isMainHand || !canRetrieve || !canDecay) return false;

        // Deduct from stack and set no decay to true
        player.getStackInHand(hand).decrement(1);
        entity.setNoDecay(1);

        // Polish
        Particles.spawnAtBlock(world, pos, ParticleTypes.WAX_ON, 8, 3);
        world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1f, 1f);

        return true;
    }
}
