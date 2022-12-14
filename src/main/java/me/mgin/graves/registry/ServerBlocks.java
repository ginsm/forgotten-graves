package me.mgin.graves.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

import me.mgin.graves.block.GraveBase;
import me.mgin.graves.block.entity.GraveBlockEntity;

public class ServerBlocks {

	public static void register(String MOD_ID, String BRAND_BLOCK) {

		for (Map.Entry<GraveBase, String> grave : GraveBlocks.GRAVE_MAP.entrySet()) {
			Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK + grave.getValue()), grave.getKey());
		}

		BlockEntityType<GraveBlockEntity> blockEntityType = FabricBlockEntityTypeBuilder.create(GraveBlockEntity::new,
				GraveBlocks.GRAVE, GraveBlocks.GRAVE_OLD, GraveBlocks.GRAVE_WEATHERED, GraveBlocks.GRAVE_FORGOTTEN)
				.build(null);

		GraveBlocks.GRAVE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":" + BRAND_BLOCK,
				blockEntityType);

	}
}
