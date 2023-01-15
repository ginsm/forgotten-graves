package me.mgin.graves.block.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class Inventory {

    public static DefaultedList<Integer> getInventoryOpenSlots(DefaultedList<ItemStack> inventory) {
        DefaultedList<Integer> openSlots = DefaultedList.of();

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == ItemStack.EMPTY)
                openSlots.add(i);
        }
        return openSlots;
    }
}
