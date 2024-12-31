package me.mgin.graves.inventory;

import java.util.List;

import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.utility.Inventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class Vanilla implements InventoriesApi {
    public String inventoryID = "Items";

    public String getID() {
        return this.inventoryID;
    }

    @Override
    public DefaultedList<ItemStack> getInventory(PlayerEntity player) {
        DefaultedList<ItemStack> items = DefaultedList.of();

        items.addAll(player.getInventory().main);
        items.addAll(player.getInventory().armor);
        items.addAll(player.getInventory().offHand);

        return items;
    }

    @Override
    public int getInventorySize(PlayerEntity player) {
        return 41;
    }

    @Override
    public DefaultedList<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity player) {
        DefaultedList<ItemStack> overflow = DefaultedList.of();
        PlayerInventory playerInventory = player.getInventory();

        // Equip armor pieces
        List<ItemStack> armor = inventory.subList(36, 40);

        for (ItemStack armorItem : armor) {
            // Do nothing with items with curse of vanishing
            if (EnchantmentHelper.hasVanishingCurse(armorItem)) {
                continue;
            }

            // Do not equip armor with curse of binding
            if (EnchantmentHelper.hasBindingCurse(armorItem)) {
                overflow.add(armorItem);
                continue;
            }

            EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(armorItem);
            if (player.getEquippedStack(slot).isEmpty()) {
                player.equipStack(slot, armorItem);
            } else {
                overflow.add(armorItem);
            }
        }

        // Equip offhand item
        ItemStack storedOffhandItem = inventory.get(40);
        ItemStack equippedOffhandItem = player.getEquippedStack(EquipmentSlot.OFFHAND);

        if (equippedOffhandItem.isEmpty()) {
            player.equipStack(EquipmentSlot.OFFHAND, storedOffhandItem);
        } else {
            // Consolidates if they match; mutates the above stacks
            Inventory.attemptStackConsolidation(storedOffhandItem, equippedOffhandItem);

            // Ensure some of the items are left before adding to overflow
            if (storedOffhandItem.getCount() > 0) {
                overflow.add(storedOffhandItem);
            }
        }

        // Restore inventory in position
        List<ItemStack> mainInventory = inventory.subList(0, 36);

        // Remove any curse of vanishing items
        for (int i = 0; i < mainInventory.size(); i++) {
            ItemStack stack = mainInventory.get(i);

            if (EnchantmentHelper.hasVanishingCurse(stack)) {
                mainInventory.set(i, Items.AIR.getDefaultStack());
            }
        }

        // Merge the inventories with mainInventory (the grave inventory) always being the source and the player
        // inventory being the target. This works because if it's GraveMergeOrder.CURRENT.. well, you want that
        // behavior. And if it's GraveMergeOrder.GRAVE the target inventory will be empty anyway due to L233 in
        // RetrieveGrave.
        Inventory.mergeInventories(mainInventory, player.getInventory().main);

        return overflow;
    }

    public void clearInventory(PlayerEntity player) {
        player.getInventory().main.clear();
        player.getInventory().armor.clear();
        player.getInventory().offHand.clear();
    }
}
