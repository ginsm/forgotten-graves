package me.mgin.graves.item;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.versioned.VersionedCode;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class Items {
    /**
     * Registers all server-side items.
     *
     * @param MOD_ID      String
     * @param BRAND_BLOCK String
     */
    public static void registerItems(String MOD_ID, String BRAND_BLOCK) {
        ArrayList<Item> ITEMS = new ArrayList<>();

        // Create and register block items
        for (GraveBlockBase grave : GraveBlocks.GRAVE_SET) {
            BlockItem item = new BlockItem(grave, VersionedCode.getItemSettings());
            Registry.register(Registries.ITEM, new Identifier(MOD_ID, grave.getBlockID()), item);
            ITEMS.add(item);
        }

        // Create new ItemGroup containing ITEMS
        Identifier itemGroupID = new Identifier(MOD_ID, "grave-blocks");
        Registry.register(Registries.ITEM_GROUP, itemGroupID, FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.forgottengraves.graves"))
            .icon(() -> new ItemStack(GraveBlocks.GRAVE))
            .entries((displayContext, entries) -> entries.addAll(ITEMS.stream().map(Item::getDefaultStack).toList()))
            .build()
        );
    }
}
