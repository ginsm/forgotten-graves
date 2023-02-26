package me.mgin.graves.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import me.mgin.graves.api.InventoriesApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.event.GameEvent;

public class Trinkets implements InventoriesApi {
    /**
     * The mod ID string used for storing and retrieving the mod's inventory.
     */
    public String inventoryID = "trinkets";

    public String getID() {
        return this.inventoryID;
    }

    /**
     * Retrieve the amount of trinket slots available.
     *
     * @param player PlayerEntity
     * @return int
     */
    @Override
    public int getInventorySize(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        var slotWrapper = new Object() {
            int slots = 0;
        };

        component.ifPresent(trinketComponent -> trinketComponent.forEach((ref, itemStack) -> slotWrapper.slots++));

        return slotWrapper.slots;
    }

    /**
     * Retrieve a list containing items occupying the trinket slots.
     *
     * @param player PlayerEntity
     * @return List<ItemStack>
     */
    @Override
    public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        DefaultedList<ItemStack> itemStacks = DefaultedList.of();

        component.ifPresent(trinketComponent -> trinketComponent.forEach((ref, stack) -> {
            DropRule rule = ref.inventory().getSlotType().getDropRule();

            if (EnchantmentHelper.hasVanishingCurse(stack) || rule.equals(DropRule.KEEP)) {
                itemStacks.add(ItemStack.EMPTY);
            } else {
                itemStacks.add(stack);
            }
        }));

        return itemStacks;
    }

    /**
     * Equips all items within a list of ItemStacks into the trinket slots.
     *
     * @param inventory {@code List<ItemStack>}
     * @param player PlayerEntity
     * @return Items that could not be equipped
     */
    @Override
    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player) {
        DefaultedList<ItemStack> unequipped = DefaultedList.of();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) continue;

            // Do nothing & let the item be deleted
            if (EnchantmentHelper.hasVanishingCurse(stack)) continue;

            // Add item to be returned as unequipped
            if (EnchantmentHelper.hasBindingCurse(stack)) {
                unequipped.add(stack);
                continue;
            }

            equipItem(stack, player, i);
        }

        return unequipped;
    }

    /**
     * Remove all items from the trinket slots.
     *
     * @param player PlayerEntity
     */
    public void clearInventory(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

        component.ifPresent(trinketComponent -> trinketComponent.forEach((ref, stack) -> {
            DropRule rule = ref.inventory().getSlotType().getDropRule();
            if (rule.equals(DropRule.KEEP)) return;

            TrinketInventory inventory = ref.inventory();
            inventory.setStack(ref.index(), ItemStack.EMPTY);
        }));
    }

    /**
     * Equips an item based on a given index; this is meant to be used with
     * setInventory. The index is based on each TrinketSlot -- not group.
     *
     * @param item ItemStack
     * @param player PlayerEntity
     * @param index int
     */
    public static void equipItem(ItemStack item, PlayerEntity player, int index) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

        if (component.isPresent()) {
            int currentSlot = 0;
            for (var group : component.get().getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    for (int i = 0; i < inventory.size(); i++) {
                        if (currentSlot == index) {
                            SlotReference ref = new SlotReference(inventory, index);
                            if (TrinketSlot.canInsert(item, ref, player)) {
                                ItemStack newStack = item.copy();
                                inventory.setStack(i, newStack);
                                SoundEvent soundEvent = item.getEquipSound();
                                if (!item.isEmpty() && soundEvent != null) {
                                    player.emitGameEvent(GameEvent.EQUIP);
                                    player.playSound(soundEvent, 1.0F, 1.0F);
                                }
                                item.setCount(0);
                            }
                        }
                        currentSlot += 1;
                    }
                }
            }
        }
    }
}
