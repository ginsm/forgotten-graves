package me.mgin.graves;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.command.Commands;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.event.Events;
import me.mgin.graves.inventory.BackSlot;
import me.mgin.graves.inventory.Inventorio;
import me.mgin.graves.inventory.Trinkets;
import me.mgin.graves.inventory.Vanilla;
import me.mgin.graves.item.Items;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.event.ConfigNetworkingEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Graves implements ModInitializer {

    public static final ArrayList<InventoriesApi> inventories = new ArrayList<>();
    public static final ArrayList<String> unloadedInventories = new ArrayList<>();
    public static String MOD_ID = "forgottengraves";
    public static String BRAND_BLOCK = "grave";
    public static Map<GameProfile, GravesConfig> clientConfigs = new HashMap<>();

    @Override
    public void onInitialize() {
        // Register Config
        AutoConfig.register(GravesConfig.class, GsonConfigSerializer::new);
        ConfigOptions.generateConfigOptions();

        // Graves Registry
        GraveBlocks.registerServerBlocks(MOD_ID, BRAND_BLOCK);
        Items.registerItems(MOD_ID, BRAND_BLOCK);
        Commands.registerServerCommands();
        Events.registerServerEvents();
        ConfigNetworking.registerC2SPackets();
        ConfigNetworkingEvents.registerServerEvents();

        // Register inventory classes
        addInventory("vanilla", Vanilla.class);
        addInventory("backslot", BackSlot.class);
        addInventory("trinkets", Trinkets.class);
        addInventory("inventorio", Inventorio.class);

        inventories.addAll(FabricLoader.getInstance().getEntrypoints(MOD_ID, InventoriesApi.class));
    }

    public void addInventory(String modID, Class<? extends InventoriesApi> modInventory) {
        try {
            if (modID.equals("vanilla") || FabricLoader.getInstance().isModLoaded(modID))
                inventories.add(modInventory.getDeclaredConstructor().newInstance());
            else
                unloadedInventories.add(modID);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
