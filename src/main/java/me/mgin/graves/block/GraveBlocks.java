package me.mgin.graves.block;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.feature.decay.DecayingGrave.BlockDecay;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

public class GraveBlocks {
    public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

    public static final GraveBlock GRAVE = createGrave(BlockDecay.FRESH);
    public static final GraveBlock GRAVE_OLD = createGrave(BlockDecay.OLD);
    public static final GraveBlock GRAVE_WEATHERED = createGrave(BlockDecay.WEATHERED);
    public static final GraveBlock GRAVE_FORGOTTEN = createGrave(BlockDecay.FORGOTTEN);

    public static final Map<GraveBlock, String> GRAVE_MAP = new HashMap<>() {
        {
            put(GraveBlocks.GRAVE, "");
            put(GraveBlocks.GRAVE_OLD, "_old");
            put(GraveBlocks.GRAVE_WEATHERED, "_weathered");
            put(GraveBlocks.GRAVE_FORGOTTEN, "_forgotten");
        }
    };

    private static GraveBlock createGrave(BlockDecay blockDecay) {
        return new GraveBlock(blockDecay, FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
    }
}
