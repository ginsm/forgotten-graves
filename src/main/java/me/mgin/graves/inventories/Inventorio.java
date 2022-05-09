package me.mgin.graves.inventories;

import java.util.List;

import me.lizardofoz.inventorio.api.InventorioAPI;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.mgin.graves.api.InventoriesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class Inventorio implements InventoriesApi {
	public String inventoryID = "inventorio";

	public String getID() {
		return this.inventoryID;
	}

	@Override
	public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
		PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
		DefaultedList<ItemStack> items = DefaultedList.of();

		for (int i = 0; i < inventorioInv.size(); i++) {
			items.add(inventorioInv.getStack(i));
		}

		return items;
	}

	@Override
	public int getInventorySize(PlayerEntity player) {
		return InventorioAPI.getInventoryAddon(player).size();
	}

	@Override
	public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player) {
		PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);

		for (int i = 0; i < inventory.size(); i++) {
			inventorioInv.setStack(i, inventory.get(i));
		}

		// return an empty list
		return DefaultedList.of();
	}

	@Override
	public void clearInventory(PlayerEntity player) {
		PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);

		inventorioInv.clear();
	}
}
