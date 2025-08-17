package me.mgin.graves.inventory;

import de.rubixdev.inventorio.api.InventorioAPI;
import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.tags.GraveEnchantTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Objects;

public class Inventorio implements InventoriesApi {
    public String inventoryID = "inventorio";
    boolean respectSoulbound = false;

    public String getID() {
        return this.inventoryID;
    }

    public boolean getRespectSoulbound() {
        return respectSoulbound;
    }

    @Override
    public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
        PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
        DefaultedList<ItemStack> items = DefaultedList.of();

        if (inventorioInv == null) return items;

        for (int i = 0; i < inventorioInv.size(); i++) {
            items.add(inventorioInv.getStack(i));
        }

        return items;
    }

    @Override
    public int getInventorySize(PlayerEntity player) {
        return Objects.requireNonNull(InventorioAPI.getInventoryAddon(player)).size();
    }

    @Override
    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player, boolean removeBinding) {
        PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
        DefaultedList<ItemStack> unequipped = DefaultedList.of();

        if (inventorioInv == null) return unequipped;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventorioInv.getStack(i).isEmpty()) {
                inventorioInv.setStack(i, inventory.get(i));
            } else {
                unequipped.add(inventory.get(i));
            }
        }

        // return an empty list
        return unequipped;
    }

    @Override
    public void clearInventory(PlayerEntity player, boolean respectSoulbound) {
        PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
        if (inventorioInv != null) {
            if (respectSoulbound) {
                clearItemsRespectingEnchants(inventorioInv);
            } else {
                inventorioInv.clear();
            }
        }
    }

    private static void clearItemsRespectingEnchants(PlayerInventoryAddon inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!GraveEnchantTags.hasSoulboundEnchantment(stack) || GraveEnchantTags.hasVanishingCurse(stack)) {
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }
    }
}
