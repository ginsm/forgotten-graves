package me.mgin.graves.events;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockHandler {
  public static ActionResult handleEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
    BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());

    if (blockEntity instanceof GraveBlockEntity) {
      Item itemInHand = player.getStackInHand(hand).getItem();

      if(!player.isSneaking()) {
        Map<Item, Item> restrictedItems = new HashMap<Item, Item>(){
          {
            put(Items.LAVA_BUCKET, Items.LAVA_BUCKET.asItem());
            // put(Items.WATER_BUCKET, Items.WATER_BUCKET.asItem());
          }
        };

        if (Item.BLOCK_ITEMS.containsValue(itemInHand) || restrictedItems.containsValue(itemInHand))
          return ActionResult.FAIL;
      }
    }


    return ActionResult.PASS;
  }
  
}
