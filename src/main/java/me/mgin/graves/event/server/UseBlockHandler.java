package me.mgin.graves.event.server;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.event.server.useblock.item.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UseBlockHandler {

    /**
     * This event handler controls what items can be used on a grave
     * and what effects they have.
     *
     * @param player PlayerEntity
     * @param world World
     * @param hand Hand
     * @param hitResult BlockHitResult
     * @return ActionResult
     */
    public static ActionResult handleEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            ItemStack itemStack = player.getStackInHand(hand);
            Item item = itemStack.getItem();

            // Rename grave with name tags
            if (NameTag.handle(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Prevent Decay
            if (Honeycomb.handle(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Remove Decay
            if (Shovel.handle(player, world, hand, pos, item, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Skulls/Player Heads
            if (Skull.handle(world, hand, pos, item, itemStack, graveEntity)) {
                return ActionResult.SUCCESS;
            }

            // Vines, Mushrooms
            if (DecayItem.handle(player, world, hand, pos, item, graveEntity)) {
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
