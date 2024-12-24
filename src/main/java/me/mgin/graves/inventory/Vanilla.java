package me.mgin.graves.inventory;

import java.util.List;

import me.mgin.graves.api.InventoriesApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.mob.MobEntity;
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
        DefaultedList<ItemStack> unequipped = DefaultedList.of();
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
                unequipped.add(armorItem);
                continue;
            }

            EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(armorItem);
            if (player.getEquippedStack(slot).isEmpty()) {
                player.equipStack(slot, armorItem);
            } else {
                unequipped.add(armorItem);
            }
        }

        // Equip offhand item
        ItemStack offHandItem = inventory.get(40);
        if (player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
            player.equipStack(EquipmentSlot.OFFHAND, offHandItem);
        } else {
            unequipped.add(offHandItem);
        }

        // Restore inventory in position
        List<ItemStack> mainInventory = inventory.subList(0, 36);

        for (int i = 0; i < mainInventory.size(); i++) {
            ItemStack stack = mainInventory.get(i);

            // Do nothing with items with curse of vanishing
            if (EnchantmentHelper.hasVanishingCurse(stack)) {
                continue;
            }

            if (playerInventory.main.get(i).isEmpty()) {
                player.getInventory().setStack(i, stack);
            } else {
                unequipped.add(stack);
            }
        }

        return unequipped;
    }

    public void clearInventory(PlayerEntity player) {
        player.getInventory().main.clear();
        player.getInventory().armor.clear();
        player.getInventory().offHand.clear();
    }
}
