package me.mgin.graves.block;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.decay.DecayingGrave.BlockDecay;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GraveBlocks {
    public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

    public static final GraveBlockBase GRAVE = createGrave(BlockDecay.FRESH);
    public static final GraveBlockBase GRAVE_OLD = createGrave(BlockDecay.OLD);
    public static final GraveBlockBase GRAVE_WEATHERED = createGrave(BlockDecay.WEATHERED);
    public static final GraveBlockBase GRAVE_FORGOTTEN = createGrave(BlockDecay.FORGOTTEN);

    public static final Map<GraveBlockBase, String> GRAVE_MAP = new HashMap<>() {
        {
            put(GraveBlocks.GRAVE, "");
            put(GraveBlocks.GRAVE_OLD, "_old");
            put(GraveBlocks.GRAVE_WEATHERED, "_weathered");
            put(GraveBlocks.GRAVE_FORGOTTEN, "_forgotten");
        }
    };

    private static GraveBlockBase createGrave(BlockDecay blockDecay) {
        return new GraveBlockBase(blockDecay, FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
    }

    /**
     * Register all grave blocks on the server.
     *
     * @param MOD_ID      String
     * @param BRAND_BLOCK String
     */
    public static void registerServerBlocks(String MOD_ID, String BRAND_BLOCK) {
        for (Map.Entry<GraveBlockBase, String> grave : GraveBlocks.GRAVE_MAP.entrySet()) {
            Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK + grave.getValue()), grave.getKey());
        }

        BlockEntityType<GraveBlockEntity> blockEntityType = FabricBlockEntityTypeBuilder.create(GraveBlockEntity::new,
                GraveBlocks.GRAVE, GraveBlocks.GRAVE_OLD, GraveBlocks.GRAVE_WEATHERED, GraveBlocks.GRAVE_FORGOTTEN)
            .build(null);

        GraveBlocks.GRAVE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":" + BRAND_BLOCK,
            blockEntityType);
    }
}
