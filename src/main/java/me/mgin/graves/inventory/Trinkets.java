package me.mgin.graves.inventory;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.tags.GraveEnchantTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Optional;

public class Trinkets implements InventoriesApi {
    /**
     * The mod ID string used for storing and retrieving the mod's inventory.
     */
    public String inventoryID = "trinkets";
    boolean respectSoulbound = true;

    public String getID() {
        return this.inventoryID;
    }

    public boolean getRespectSoulbound() {
        return respectSoulbound;
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

            if (rule.equals(DropRule.KEEP)) {
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
    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player, boolean removeBinding) {
        DefaultedList<ItemStack> unequipped = DefaultedList.of();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) continue;

            // Add item to be returned as unequipped
            if (removeBinding && EnchantmentHelper.hasBindingCurse(stack)) {
                unequipped.add(stack);
                continue;
            }

            // Attempt to equip item, otherwise add to unequipped inventory.
            boolean equipped = equipItem(stack, player, i);
            if (!equipped) {
                unequipped.add(stack);
            }
        }

        return unequipped;
    }

    /**
     * Remove all items from the trinket slots.
     *
     * @param player PlayerEntity
     */
    public void clearInventory(PlayerEntity player, boolean respectSoulbound) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

        component.ifPresent(trinketComponent -> trinketComponent.forEach((ref, stack) -> {
            DropRule rule = ref.inventory().getSlotType().getDropRule();
            boolean hasSoulbound = respectSoulbound
                    && GraveEnchantTags.hasSoulboundEnchantment(stack)
                    && !GraveEnchantTags.hasVanishingCurse(stack);
            if (rule.equals(DropRule.KEEP) || hasSoulbound) return;

            TrinketInventory inventory = ref.inventory();
            inventory.setStack(ref.index(), ItemStack.EMPTY);
        }));
    }

    public static void removeResoluteIvy(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        component.ifPresent(trinketComponent -> trinketComponent.forEach(
                (ref, stack) -> stack.removeSubNbt("Botania_keepIvy")
        ));
    }

    /**
     * Equips an item based on a given index; this is meant to be used with
     * setInventory. The index is based on each TrinketSlot -- not group.
     *
     * @param stack  ItemStack
     * @param player PlayerEntity
     * @param index  int
     */
    public static boolean equipItem(ItemStack stack, PlayerEntity player, int index) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        boolean equipped = false;

        if (component.isPresent()) {
            int currentSlot = 0;
            for (var group : component.get().getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    for (int i = 0; i < inventory.size(); i++) {
                        if (currentSlot == index) {
                            SlotReference ref = new SlotReference(inventory, index);
                            if (TrinketSlot.canInsert(stack, ref, player)) {
                                ItemStack newStack = stack.copy();
                                inventory.setStack(i, newStack);
                                SoundEvent soundEvent = stack.getItem() instanceof Equipment eq ? eq.getEquipSound() :
                                    null;

                                if (!stack.isEmpty() && soundEvent != null) {
                                    player.emitGameEvent(GameEvent.EQUIP);
                                    player.playSound(soundEvent, 1.0F, 1.0F);
                                }

                                stack.setCount(0);
                                equipped = true;
                            }
                        }
                        currentSlot += 1;
                    }
                }
            }
        }

        return equipped;
    }
}
