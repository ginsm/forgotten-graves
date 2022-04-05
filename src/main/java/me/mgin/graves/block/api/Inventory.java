package me.mgin.graves.block.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class Inventory {
  static DefaultedList<ItemStack> getMainInventory(PlayerEntity player) {
    DefaultedList<ItemStack> items = DefaultedList.of();

    items.addAll(player.getInventory().main);
    items.addAll(player.getInventory().armor);
    items.addAll(player.getInventory().offHand);

    return items;
  }
}
