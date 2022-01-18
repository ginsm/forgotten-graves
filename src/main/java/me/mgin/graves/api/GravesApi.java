package me.mgin.graves.api;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface GravesApi {
	List<ItemStack> getInventory(PlayerEntity entity);

	/**
	 * Set the inventory for a given mod.
	 * 
	 * @param inventory
	 * @param entity
	 * @return Items that could not be equipped.
	 */
	DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity entity);

	int getInventorySize(PlayerEntity entity);
}
