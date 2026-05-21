package me.mgin.graves.block.render.packs;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface GraveResourcePack {
    public final VoxelShape GRAVE_SHAPE = VoxelShapes.fullCube();
    public final VoxelShape GRAVE_SHAPE_OLD = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_WEATHERED = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_FORGOTTEN = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_EXPIRED = GRAVE_SHAPE;
    public float SKULL_OFFSET = 0;

    public TextPositions getTextPositions();

    public default VoxelShape getGraveShape(String blockID) {
        return switch (blockID) {
            case "grave_old" -> GraveResourcePack.GRAVE_SHAPE_OLD;
            case "grave_weathered" -> GraveResourcePack.GRAVE_SHAPE_WEATHERED;
            case "grave_forgotten" -> GraveResourcePack.GRAVE_SHAPE_FORGOTTEN;
            case "grave_expired" -> GraveResourcePack.GRAVE_SHAPE_EXPIRED;
            default -> GraveResourcePack.GRAVE_SHAPE;
        };
    }

    public default float getSkullOffset() {
        return GraveResourcePack.SKULL_OFFSET;
    }
}
