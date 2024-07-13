package me.mgin.graves.event.server.useblock.item;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Permission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NameTag {
    public static Boolean handle(PlayerEntity player, World world, Hand hand, BlockPos pos,
                                 Item item, GraveBlockEntity entity) {
        ItemStack stack = player.getStackInHand(hand);
        boolean isNameTagItem = item instanceof NameTagItem;
        boolean isMainHand = hand.equals(Hand.MAIN_HAND);
        boolean unownedGrave = entity.getGraveOwner() == null;
        boolean hasCustomName = stack.hasCustomName();

        if (!isNameTagItem || !isMainHand || !unownedGrave || !hasCustomName) return false;

        entity.setCustomName(stack.getName().getString());

        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }

        world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_WORK_MASON, SoundCategory.BLOCKS, 1f, 1f);

        return true;
    }
}
