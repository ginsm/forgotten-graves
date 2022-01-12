package me.mgin.graves.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import me.mgin.graves.api.GravesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TrinketsCompat implements GravesApi {
	@Override
	public List<ItemStack> getInventory(PlayerEntity player) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
		List<ItemStack> itemStacks = new ArrayList<>();

		if (component.isPresent()) {
			component.get().getAllEquipped().forEach((pair) -> {
				ItemStack itemStack = pair.getRight();
				itemStacks.add(itemStack);
			});
		}

		return itemStacks;
	}

	@Override
	public void setInventory(List<ItemStack> inventory, PlayerEntity player) {
		for (ItemStack itemStack : inventory) {
			TrinketItem.equipItem(player, itemStack);
		}
	}

	@Override
	public int getInventorySize(PlayerEntity player) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
		var slotWrapper = new Object() {
			int slots = 0;
		};

		if (component.isPresent())
			component.get().forEach((ref, itemStack) -> {
				slotWrapper.slots++;
			});

		return slotWrapper.slots;
	}

	public static void clearInventory(PlayerEntity player) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

		if (component.isPresent()) {
			component.get().forEach((ref, stack) -> {
				TrinketInventory inventory = ref.inventory();
				inventory.setStack(ref.index(), ItemStack.EMPTY);
			});
		}
	}
}
