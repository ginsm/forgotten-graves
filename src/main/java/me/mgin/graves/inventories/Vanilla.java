package me.mgin.graves.inventories;

import java.util.List;

import me.mgin.graves.api.InventoriesApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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

		// Equip armor pieces
		List<ItemStack> armor = inventory.subList(36, 40);

		for (int i = 0; i < armor.size(); i++) {
			ItemStack armorItem = armor.get(i);

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
			player.equipStack(slot, armorItem);
		}

		// Equip off hand item
		ItemStack offHandItem = inventory.get(40);
		player.equipStack(EquipmentSlot.OFFHAND, offHandItem);

		// Restore inventory in position
		List<ItemStack> mainInventory = inventory.subList(0, 36);

		for (int i = 0; i < mainInventory.size(); i++) {
			ItemStack stack = mainInventory.get(i);

			// Do nothing with items with curse of vanishing
			if (EnchantmentHelper.hasVanishingCurse(stack)) {
				continue;
			}

			player.getInventory().setStack(i, stack);
		}

		return unequipped;
	}

	public void clearInventory(PlayerEntity player) {
		player.getInventory().main.clear();
		player.getInventory().armor.clear();
		player.getInventory().offHand.clear();
	}
}
