package me.mgin.graves.registry;

import java.util.Map;

import me.mgin.graves.block.GraveBase;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ServerItems {

	public static void register(String MOD_ID, String BRAND_BLOCK) {
		for (Map.Entry<GraveBase, String> grave : GraveBlocks.GRAVE_MAP.entrySet()) {
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + grave.getValue()),
					new BlockItem(grave.getKey(), new Item.Settings().group(ItemGroup.DECORATIONS)));
		}
	}

}
