package me.mgin.graves.inventories;

import java.util.List;

import me.mgin.graves.api.InventoriesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BackSlot implements InventoriesApi {
  public String inventoryID = "backslot";

  public String getID() {
    return this.inventoryID;
  }

  public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
    DefaultedList<ItemStack> items = DefaultedList.of();

    items.add(player.getInventory().getStack(42));
    items.add(player.getInventory().getStack(43));

    return items;
  }

  public int getInventorySize(PlayerEntity player) {
    return 2;
  }

  public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player) {
    DefaultedList<ItemStack> unequipped = DefaultedList.of();

    player.getInventory().setStack(42, inventory.get(0));
    player.getInventory().setStack(43, inventory.get(1));

    return DefaultedList.of();
  }

  public void clearInventory(PlayerEntity player) {
    player.getInventory().clear();
  }

}
