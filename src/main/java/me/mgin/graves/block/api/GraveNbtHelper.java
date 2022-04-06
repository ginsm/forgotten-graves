package me.mgin.graves.block.api;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class GraveNbtHelper {

	static public DefaultedList<ItemStack> readInventory(String key, NbtCompound nbt) {
		if (nbt.contains(key)) {
			int itemCount = nbt.getCompound("ItemCount").getInt(key);

			DefaultedList<ItemStack> stacks = DefaultedList.ofSize(itemCount, ItemStack.EMPTY);

			Inventories.readNbt(nbt.getCompound(key), stacks);

			return stacks;
		}

		return DefaultedList.ofSize(0);
	}

	public static NbtCompound writeInventory(String key, DefaultedList<ItemStack> stacks, NbtCompound nbt) {
		if (stacks == null)
			return nbt;

		// Write item count
		NbtCompound itemCount = new NbtCompound();

		if (nbt.contains("ItemCount"))
			itemCount = nbt.getCompound("ItemCount");

		itemCount.putInt(key, stacks.size());
		nbt.put("ItemCount", itemCount);

		// Store the inventory
		nbt.put(key, Inventories.writeNbt(new NbtCompound(), stacks, true));

		return nbt;
	}

	public static NbtCompound update(NbtCompound nbt) {
		// Retrieve the items like normal
		DefaultedList<ItemStack> oldItems = DefaultedList.ofSize(nbt.getInt("ItemCount"), ItemStack.EMPTY);
		Inventories.readNbt(nbt.getCompound("Items"), oldItems);

		// Separate the item lists
		DefaultedList<ItemStack> items = DefaultedList.ofSize(0);
		items.addAll(oldItems.subList(0, 41));

		DefaultedList<ItemStack> trinkets = DefaultedList.ofSize(0);
		if (oldItems.size() > 41) {
			trinkets.addAll(oldItems.subList(41, oldItems.size()));
		}

		// Create/store new ItemCount format
		NbtCompound itemCount = new NbtCompound();

		itemCount.putInt("Items", items.size());
		itemCount.putInt("trinkets", trinkets.size());

		nbt.put("ItemCount", itemCount);

		// Store the two inventories
		nbt.put("Items", Inventories.writeNbt(new NbtCompound(), items, true));

		nbt.put("trinkets", Inventories.writeNbt(new NbtCompound(), trinkets, true));

		return nbt;
	}
}
