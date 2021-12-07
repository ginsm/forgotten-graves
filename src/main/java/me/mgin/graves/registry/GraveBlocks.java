package me.mgin.graves.registry;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.GraveBase;
import me.mgin.graves.block.degradation.AgingGrave.BlockAge;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

public class GraveBlocks {

	public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

	public static final GraveBase GRAVE = createGrave(BlockAge.FRESH);
	public static final GraveBase GRAVE_OLD = createGrave(BlockAge.OLD);
	public static final GraveBase GRAVE_WEATHERED = createGrave(BlockAge.WEATHERED);
	public static final GraveBase GRAVE_FORGOTTEN = createGrave(BlockAge.FORGOTTEN);

	public static final Map<GraveBase, String> GRAVE_MAP = new HashMap<GraveBase, String>(){
		{
			put(GraveBlocks.GRAVE, "");
			put(GraveBlocks.GRAVE_OLD, "_old");
			put(GraveBlocks.GRAVE_WEATHERED, "_weathered");
			put(GraveBlocks.GRAVE_FORGOTTEN, "_forgotten");
		}
	};

	private static final GraveBase createGrave(BlockAge blockAge) {
		return new GraveBase(blockAge, FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
	}

}
