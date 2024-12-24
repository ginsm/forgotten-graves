package me.mgin.graves.block;

import me.mgin.graves.block.decay.DecayingGrave.BlockDecay;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class GraveBlocks {
    public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

    public static final GraveBlockBase GRAVE = createGrave(BlockDecay.FRESH, "grave");
    public static final GraveBlockBase GRAVE_OLD = createGrave(BlockDecay.OLD, "grave_old");
    public static final GraveBlockBase GRAVE_WEATHERED = createGrave(BlockDecay.WEATHERED, "grave_weathered");
    public static final GraveBlockBase GRAVE_FORGOTTEN = createGrave(BlockDecay.FORGOTTEN, "grave_forgotten");
    public static final GraveBlockBase GRAVE_EXPIRED = createGrave(BlockDecay.EXPIRED, "grave_expired");

    public static final Set<GraveBlockBase> GRAVE_SET = new HashSet<>();

    static {
        GRAVE_SET.add(GRAVE);
        GRAVE_SET.add(GRAVE_OLD);
        GRAVE_SET.add(GRAVE_WEATHERED);
        GRAVE_SET.add(GRAVE_FORGOTTEN);
        GRAVE_SET.add(GRAVE_EXPIRED);
    }

    private static GraveBlockBase createGrave(BlockDecay blockDecay, String blockID) {
        return new GraveBlockBase(
            blockDecay,
            FabricBlockSettings.create().strength(0.8f, 3600000.0F),
            blockID
        );
    }

    /**
     * Register all grave blocks on the server.
     *
     * @param MOD_ID      String
     * @param BRAND_BLOCK String
     */
    public static void registerServerBlocks(String MOD_ID, String BRAND_BLOCK) {
        for (GraveBlockBase grave : GraveBlocks.GRAVE_SET) {
            Registry.register(Registries.BLOCK, new Identifier(MOD_ID, grave.getBlockID()), grave);
        }

        BlockEntityType<GraveBlockEntity> blockEntityType = FabricBlockEntityTypeBuilder.create(GraveBlockEntity::new,
                GraveBlocks.GRAVE, GraveBlocks.GRAVE_OLD, GraveBlocks.GRAVE_WEATHERED, GraveBlocks.GRAVE_FORGOTTEN,
                GraveBlocks.GRAVE_EXPIRED)
            .build(null);

        GraveBlocks.GRAVE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, MOD_ID + ":" + BRAND_BLOCK,
            blockEntityType);
    }
}
