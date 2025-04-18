package me.mgin.graves.inventory;

import de.rubixdev.inventorio.api.InventorioAPI;
import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import me.mgin.graves.api.InventoriesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

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
    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory,
                                                 PlayerEntity player) {
        PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
        DefaultedList<ItemStack> unequipped = DefaultedList.of();

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
    public void clearInventory(PlayerEntity player) {
        PlayerInventoryAddon inventorioInv = InventorioAPI.getInventoryAddon(player);
        inventorioInv.clear();
    }
}
