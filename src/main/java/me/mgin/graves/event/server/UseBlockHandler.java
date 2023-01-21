package me.mgin.graves.event.server;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.event.item.Honeycomb;
import me.mgin.graves.event.item.Shovel;
import me.mgin.graves.event.item.Skull;
import me.mgin.graves.event.item.DecayItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UseBlockHandler {

    public static ActionResult handleEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            ItemStack itemStack = player.getStackInHand(hand);
            Item item = itemStack.getItem();

            // Prevent Decay
            if (Honeycomb.use(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            };

            // Remove Decay
            if (Shovel.use(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Skulls/Player Heads
            if (Skull.use(world, hand, pos, item, itemStack, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Vines, Mushrooms
            if (DecayItem.use(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Prevent block placement and restricted item usage if the player isn't sneaking
            if (!player.isSneaking()) {
                if (Item.BLOCK_ITEMS.containsValue(item) || restrictedItems.containsValue(item))
                    return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    /**
     * Items that should be prevented from activating. Mostly blocks.
     */
    private static final Map<Item, Item> restrictedItems = new HashMap<>() {
        {
            put(Items.LAVA_BUCKET, Items.LAVA_BUCKET.asItem());
            put(Items.FLINT_AND_STEEL, Items.FLINT_AND_STEEL.asItem());
        }
    };
}
