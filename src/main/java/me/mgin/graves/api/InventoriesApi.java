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

	// Mostly just used when soulbound items should be removed, like during retrieval.
	void clearInventory(PlayerEntity player, boolean respectSoulbound);

	// Used when placing the grave so soulbound items stay on the player
	default void clearInventory(PlayerEntity player) {
		clearInventory(player, true);
	}
}
