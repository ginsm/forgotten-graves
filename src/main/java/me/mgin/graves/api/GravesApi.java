package me.mgin.graves.api;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface GravesApi {
	List<ItemStack> getInventory(PlayerEntity entity);

	void setInventory(List<ItemStack> inventory, PlayerEntity entity);

	int getInventorySize(PlayerEntity entity);
}
