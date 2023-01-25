package me.mgin.graves.item;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
            Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + grave.getValue()),
                new BlockItem(grave.getKey(), new Item.Settings().group(ItemGroup.DECORATIONS)));
        }
    }
}
