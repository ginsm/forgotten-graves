package me.mgin.graves.inventory;

import java.util.List;

import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.tags.GraveEnchantTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BackSlot implements InventoriesApi {
    public String inventoryID = "backslot";
    boolean respectSoulbound = true;

    public String getID() {
        return this.inventoryID;
    }

    public boolean getRespectSoulbound() {
        return respectSoulbound;
    }

    public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
        DefaultedList<ItemStack> items = DefaultedList.of();

        items.add(player.getInventory().getStack(41));
        items.add(player.getInventory().getStack(42));

        return items;
    }

    public int getInventorySize(PlayerEntity player) {
        return 2;
    }

    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player, boolean removeBinding) {
        DefaultedList<ItemStack> unequipped = DefaultedList.of();

        unequipped.add(inventory.get(0));
        unequipped.add(inventory.get(1));

        return unequipped;
    }

    public void clearInventory(PlayerEntity player, boolean respectSoulbound) {
        if (respectSoulbound) {
            clearItemRespectingEnchants(player.getInventory(), 41);
            clearItemRespectingEnchants(player.getInventory(), 42);
        } else {
            player.getInventory().setStack(41, ItemStack.EMPTY);
            player.getInventory().setStack(42, ItemStack.EMPTY);
        }
    }

    private static void clearItemRespectingEnchants(PlayerInventory inventory, int slot) {
        ItemStack stack = inventory.getStack(slot);

        if (!GraveEnchantTags.hasSoulboundEnchantment(stack) || GraveEnchantTags.hasSoulboundEnchantment(stack)) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
    }
}
