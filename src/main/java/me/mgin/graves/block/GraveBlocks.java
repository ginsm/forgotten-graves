package me.mgin.graves.block;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.degradation.AgingGrave.BlockAge;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

public class GraveBlocks {

	public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

	public static final GraveBlock GRAVE = createGrave(BlockAge.FRESH);
	public static final GraveBlock GRAVE_OLD = createGrave(BlockAge.OLD);
	public static final GraveBlock GRAVE_WEATHERED = createGrave(BlockAge.WEATHERED);
	public static final GraveBlock GRAVE_FORGOTTEN = createGrave(BlockAge.FORGOTTEN);

	public static final Map<GraveBlock, String> GRAVE_MAP = new HashMap<>() {
		{
			put(GraveBlocks.GRAVE, "");
			put(GraveBlocks.GRAVE_OLD, "_old");
			put(GraveBlocks.GRAVE_WEATHERED, "_weathered");
			put(GraveBlocks.GRAVE_FORGOTTEN, "_forgotten");
		}
	};

	private static GraveBlock createGrave(BlockAge blockAge) {
		return new GraveBlock(blockAge, FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
	}

}
