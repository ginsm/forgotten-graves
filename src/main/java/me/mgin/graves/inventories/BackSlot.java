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

		items.add(player.getInventory().getStack(41));
		items.add(player.getInventory().getStack(42));

		return items;
	}

	public int getInventorySize(PlayerEntity player) {
		return 2;
	}

	public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player) {
		DefaultedList<ItemStack> unequipped = DefaultedList.of();

		unequipped.add(inventory.get(0));
		unequipped.add(inventory.get(1));

		return unequipped;
	}

	public void clearInventory(PlayerEntity player) {
		player.getInventory().setStack(41, ItemStack.EMPTY);
		player.getInventory().setStack(42, ItemStack.EMPTY);
	}

}
