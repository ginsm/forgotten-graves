package me.mgin.graves.block.utility;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Inventory {
    public static void mergeInventories(List<ItemStack> source, List<ItemStack> target) {
        HashMap<String, List<Integer>> targetItemMap = new HashMap<>();

        // Map item stacks and track empty slots separately
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < target.size(); i++) {
            ItemStack targetStack = target.get(i);
            if (targetStack.isEmpty()) {
                emptySlots.add(i);
            } else {
                String key = getStackKey(targetStack);
                targetItemMap.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }
        }

        for (ItemStack sourceStack : source) {
            if (sourceStack.isEmpty()) continue;

            String sourceKey = getStackKey(sourceStack);
            List<Integer> matchingSlots = targetItemMap.getOrDefault(sourceKey, new ArrayList<>());
            Iterator<Integer> slotIterator = matchingSlots.iterator();

            while (slotIterator.hasNext() && !sourceStack.isEmpty()) {
                int slot = slotIterator.next();
                ItemStack targetStack = target.get(slot);

                int transferAmount = Math.min(sourceStack.getCount(), targetStack.getMaxCount() - targetStack.getCount());
                targetStack.increment(transferAmount);
                sourceStack.decrement(transferAmount);

                if (targetStack.getCount() == targetStack.getMaxCount()) {
                    slotIterator.remove();
                }
            }

            // Try empty slots if no matching slots remain
            Iterator<Integer> emptySlotIterator = emptySlots.iterator();
            while (emptySlotIterator.hasNext() && !sourceStack.isEmpty()) {
                int slot = emptySlotIterator.next();
                ItemStack newStack = sourceStack.copy();
                newStack.setCount(0); // Start with an empty stack to be filled
                target.set(slot, newStack);

                int transferAmount = Math.min(sourceStack.getCount(), newStack.getMaxCount());
                newStack.setCount(transferAmount);
                sourceStack.decrement(transferAmount);

                emptySlotIterator.remove();
                String newStackKey = getStackKey(newStack);
                targetItemMap.computeIfAbsent(newStackKey, k -> new ArrayList<>()).add(slot);
            }
        }

        // Clean up the source list
        source.removeIf(ItemStack::isEmpty);
    }

    /**
     * Generates a unique key for an ItemStack based on its item type and NBT data.
     */
    private static String getStackKey(ItemStack stack) {
        return stack.getItem().toString() + (stack.hasNbt() ? stack.getNbt().toString() : "");
    }


}
