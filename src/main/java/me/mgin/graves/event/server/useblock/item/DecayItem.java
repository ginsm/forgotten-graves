package me.mgin.graves.event.server.useblock.item;

import me.mgin.graves.block.decay.DecayStateManager;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DecayItem {
    /**
     * This event handler determines whether a decay item is in the main hand,
     * whether the grave is unowned or is owned by the player, and then increases
     * the amount of decay on the grave if all criteria is met.
     *
     * @param player PlayerEntity
     * @param world World
     * @param hand Hand
     * @param pos BlockPos
     * @param item Itm
     * @param entity GraveBlockEntity
     * @return Boolean
     */
    public static Boolean handle(PlayerEntity player, World world, Hand hand, BlockPos pos,
                                 Item item, GraveBlockEntity entity) {
        boolean isDecayItem = decayItems.contains(item);
        boolean hasOwner = entity.getGraveOwner() != null;

        if (!isDecayItem || hasOwner) return false;

        if (DecayStateManager.increaseDecayState(world, pos)) {
            if (!player.isCreative()) {
                player.getStackInHand(hand).decrement(1);
            }

            world.playSound(null, pos, SoundEvents.BLOCK_VINE_PLACE, SoundCategory.BLOCKS, 1f, 1f);
        }

        return true;
    }

    private static final List<Item> decayItems = new ArrayList<>(){{
        add(Items.VINE);
        add(Items.TWISTING_VINES);
        add(Items.WEEPING_VINES);
        add(Items.BROWN_MUSHROOM);
        add(Items.RED_MUSHROOM);
    }};
}
