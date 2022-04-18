package me.mgin.graves.block.api;

import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GraveDropType;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RetrieveGrave {
	static public boolean retrieve(PlayerEntity player, World world, BlockPos pos) {
		// Edge case checking & variable initialization
		if (world.isClient)
			return false;

		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (!(blockEntity instanceof GraveBlockEntity graveEntity))
			return false;

		graveEntity.sync(world, pos);

		if (graveEntity.getInventory("Items") == null)
			return false;
		if (graveEntity.getGraveOwner() == null)
			return false;

		if (!Permission.playerCanAttemptRetrieve(player, graveEntity))
			if (!Permission.playerCanOverride(player))
				return false;

		// Store old inventories as one big inventory
		DefaultedList<ItemStack> oldInventory = DefaultedList.of();

		for (InventoriesApi api : Graves.inventories) {
			DefaultedList<ItemStack> inventory = api.getInventory(player);

			if (inventory == null)
				continue;

			oldInventory.addAll(inventory);
		}

		// Resolve drop type
		GraveDropType dropType = GravesConfig.resolveConfig("dropType", player.getGameProfile()).main.dropType;

		if (dropType == GraveDropType.PUT_IN_INVENTORY) {
			DefaultedList<ItemStack> extraItems = DefaultedList.of();

			// Equip inventories
			for (InventoriesApi api : Graves.inventories) {
				DefaultedList<ItemStack> inventory = graveEntity.getInventory(api.getID());

				if (inventory == null)
					continue;

				if (api.getInventorySize(player) == inventory.size()) {
					DefaultedList<ItemStack> unequippedItems = api.setInventory(inventory, player);
					extraItems.addAll(unequippedItems);
				} else {
					extraItems.addAll(inventory);
				}
			}

			// Check for any potentionally unloaded inventories; store them if found
			for (String modID : Graves.unloadedInventories) {
				DefaultedList<ItemStack> inventory = graveEntity.getInventory(modID);
				if (inventory != null)
					extraItems.addAll(inventory);
			}

			// Preserve previous inventory
			extraItems.addAll(oldInventory);

			// Remove any empty or air slots from extraItems
			extraItems.removeIf(item -> item == ItemStack.EMPTY || item.getItem() == Items.AIR);

			// Move extra items to open slots
			DefaultedList<Integer> openSlots = Inventory.getInventoryOpenSlots(player.getInventory().main);

			for (int i = 0; i < openSlots.size(); i++) {
				if (extraItems.size() > 0) {
					player.getInventory().setStack(openSlots.get(i), extraItems.get(0));
					extraItems.remove(0);
				}
			}

			// Drop any excess items
			DefaultedList<ItemStack> dropItems = DefaultedList.of();
			dropItems.addAll(extraItems);
			ItemScatterer.spawn(world, pos, dropItems);
		} else if (dropType == GraveDropType.DROP_ITEMS) {
			DefaultedList<ItemStack> droppedItems = DefaultedList.of();

			// Add loaded inventories to droppedItems list
			for (InventoriesApi api : Graves.inventories) {
				DefaultedList<ItemStack> modInventory = graveEntity.getInventory(api.getID());

				if (modInventory != null)
					droppedItems.addAll(modInventory);
			}

			// Add any unloaded inventories to droppedItems list
			for (String modID : Graves.unloadedInventories) {
				DefaultedList<ItemStack> modInventory = graveEntity.getInventory(modID);

				if (modInventory != null)
					droppedItems.addAll(modInventory);
			}

			ItemScatterer.spawn(world, pos, droppedItems);
		}

		// Add player experience back
		player.addExperience((int) (1 * graveEntity.getXp()));

		// spawnBreakParticles(world, player, pos, defaultState);

		// Remove block
		world.removeBlock(pos, false);
		return true;
	}
}
