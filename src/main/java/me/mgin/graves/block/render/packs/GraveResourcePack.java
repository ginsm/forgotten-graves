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
            case "grave_old" -> this.GRAVE_SHAPE_OLD;
            case "grave_weathered" -> this.GRAVE_SHAPE_WEATHERED;
            case "grave_forgotten" -> this.GRAVE_SHAPE_FORGOTTEN;
            case "grave_expired" -> this.GRAVE_SHAPE_EXPIRED;
            default -> this.GRAVE_SHAPE;
        };
    }

    public default float getSkullOffset() {
        return this.SKULL_OFFSET;
    }
}
