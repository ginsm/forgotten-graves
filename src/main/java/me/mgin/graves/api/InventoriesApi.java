package me.mgin.graves.api;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface InventoriesApi {
	String inventoryID = null;

	String getID();

	DefaultedList<ItemStack> getInventory(PlayerEntity entity);

	int getInventorySize(PlayerEntity entity);

	DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity entity);

	void clearInventory(PlayerEntity player);
}
