package me.mgin.graves.item;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Items {

    /**
     * Registers all server-side items.
     *
     * @param MOD_ID      String
     * @param BRAND_BLOCK String
     */
    public static void registerItems(String MOD_ID, String BRAND_BLOCK) {
        for (Map.Entry<GraveBlockBase, String> grave : GraveBlocks.GRAVE_MAP.entrySet()) {
            BlockItem item = new BlockItem(grave.getKey(), new FabricItemSettings());

            Registry.register(Registries.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + grave.getValue()), item);

            ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(item));
        }
    }
}
