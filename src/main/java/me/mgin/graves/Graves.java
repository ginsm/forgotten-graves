package me.mgin.graves;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;

import me.mgin.graves.api.InventoriesApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.inventories.BackSlot;
import me.mgin.graves.inventories.Inventorio;
import me.mgin.graves.inventories.Trinkets;
import me.mgin.graves.inventories.Vanilla;
import me.mgin.graves.registry.ServerBlocks;
import me.mgin.graves.registry.ServerCommands;
import me.mgin.graves.registry.ServerEvents;
import me.mgin.graves.registry.ServerItems;
import me.mgin.graves.registry.ServerReceivers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Graves implements ModInitializer {

	public static final ArrayList<InventoriesApi> inventories = new ArrayList<>();
	public static final ArrayList<String> unloadedInventories = new ArrayList<>();
	public static String MOD_ID = "forgottengraves";
	public static String BRAND_BLOCK = "grave";
	public static Map<GameProfile, GravesConfig> clientConfigs = new HashMap<>();

	@Override
	public void onInitialize() {
		// Graves Registry
		ServerBlocks.register(MOD_ID, BRAND_BLOCK);
		ServerItems.register(MOD_ID, BRAND_BLOCK);
		ServerEvents.register();
		ServerCommands.register();
		ServerReceivers.register();

		// Register inventory classes
		addInventory("vanilla", Vanilla.class);
		addInventory("backslot", BackSlot.class);
		addInventory("trinkets", Trinkets.class);
		addInventory("inventorio", Inventorio.class);

		inventories.addAll(FabricLoader.getInstance().getEntrypoints(MOD_ID, InventoriesApi.class));

		// Dependency Registry
		AutoConfig.register(GravesConfig.class, GsonConfigSerializer::new);
	}

	public void addInventory(String modID, Class<? extends InventoriesApi> modInventory) {
		try {
			if (modID == "vanilla" || FabricLoader.getInstance().isModLoaded(modID))
				inventories.add(modInventory.getDeclaredConstructor().newInstance());
			else
				unloadedInventories.add(modID);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
