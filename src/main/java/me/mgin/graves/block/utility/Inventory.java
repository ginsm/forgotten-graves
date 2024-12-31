package me.mgin.graves.block.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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

        for (int i = 0; i < source.size(); i++) {
            ItemStack sourceStack = source.get(i);
            if (sourceStack.isEmpty()) continue;

            String sourceKey = getStackKey(sourceStack);
            List<Integer> matchingSlots = targetItemMap.getOrDefault(sourceKey, new ArrayList<>());
            Iterator<Integer> slotIterator = matchingSlots.iterator();

            while (slotIterator.hasNext() && !sourceStack.isEmpty()) {
                int slot = slotIterator.next();
                ItemStack targetStack = target.get(slot);
                
                attemptStackConsolidation(sourceStack, targetStack); // This mutates the two stacks.

                if (targetStack.getCount() == targetStack.getMaxCount()) {
                    slotIterator.remove();
                }
            }

            // Try to store it in place, if possible
            if (target.get(i).isEmpty()) {
                target.set(i, sourceStack);
                // Set overflow index to air, otherwise sourceStack will exist in both and end up being dropped
                source.set(i, Items.AIR.getDefaultStack());
                continue;
            }

            // Try empty slots if no matching slots remain
            Iterator<Integer> emptySlotIterator = emptySlots.iterator();
            while (emptySlotIterator.hasNext() && !sourceStack.isEmpty()) {
                int slot = emptySlotIterator.next();
                ItemStack newStack = sourceStack.copy();
                newStack.setCount(0); // Start with an empty stack to be filled
                target.set(slot, newStack);

                consolidateStacks(sourceStack, newStack);

                emptySlotIterator.remove();
                String newStackKey = getStackKey(newStack);
                targetItemMap.computeIfAbsent(newStackKey, k -> new ArrayList<>()).add(slot);
            }
        }
    }

    public static void attemptStackConsolidation(ItemStack sourceStack, ItemStack targetStack) {
        String sourceStackKey = getStackKey(sourceStack);
        String targetStackKey = getStackKey(targetStack);

        if (sourceStackKey.equals(targetStackKey)) {
            consolidateStacks(sourceStack, targetStack);
        }
    }

    public static void consolidateStacks(ItemStack sourceStack, ItemStack targetStack) {
        int transferAmount = Math.min(sourceStack.getCount(), targetStack.getMaxCount() - targetStack.getCount());
        targetStack.increment(transferAmount);
        sourceStack.decrement(transferAmount);
    }

    /**
     * Generates a unique key for an ItemStack based on its item type and NBT data.
     */
    public static String getStackKey(ItemStack stack) {
        return stack.getItem().toString() + (stack.hasNbt() ? stack.getNbt().toString() : "");
    }


}
