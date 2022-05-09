package me.mgin.graves.block.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class Inventory {
	public static DefaultedList<ItemStack> getMainInventory(PlayerEntity player) {
		DefaultedList<ItemStack> items = DefaultedList.of();

		items.addAll(player.getInventory().main);
		items.addAll(player.getInventory().armor);
		items.addAll(player.getInventory().offHand);

		return items;
	}

	public static DefaultedList<Integer> getInventoryOpenSlots(DefaultedList<ItemStack> inventory) {
		DefaultedList<Integer> openSlots = DefaultedList.of();

		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i) == ItemStack.EMPTY)
				openSlots.add(i);
		}
		return openSlots;
	}
}
