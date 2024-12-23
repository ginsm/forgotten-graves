package me.mgin.graves.block.render.packs;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class DefaultPack implements ResourcePack {
    public String ID = "forgotten-graves-default";
    public List<String> files = new ArrayList<>();

    static float bit = 0.0625f; // useful for creating grave shapes
    public final VoxelShape GRAVE_SHAPE = VoxelShapes.union(
        // Tombstone (top down)
        VoxelShapes.cuboid(0.1875f, 0.9375f, 0f, 0.8125f, 1f, 0.0625f), // Top part of tombstone
        VoxelShapes.cuboid(0.125f, 0.8125f, 0f, 0.875f, 0.9375f, 0.0625f), // Middle part of tombstone
        VoxelShapes.cuboid(0.0625f, 0f, 0f, 0.9375f, 0.8125f, 0.0625f), // Bottom part of tombstone
        // Dirt (top = closest section to tombstone)
        VoxelShapes.cuboid(0.0625f, 0f, 0.0625f, 0.9375f, 0.0625f, 0.75f), // nearest
        VoxelShapes.cuboid(0.125f, 0f, 0.75f, 0.875f, 0.0625f, 0.8125f),
        VoxelShapes.cuboid(0.1875f, 0f, 0.8125f, 0.8125f, 0.0625f, 0.875f),
        VoxelShapes.cuboid(0.3125f, 0f, 0.875f, 0.6875f, 0.0625f, 0.9375f) // furthest
    );
    public final VoxelShape GRAVE_SHAPE_OLD = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_WEATHERED = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_FORGOTTEN = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_EXPIRED = VoxelShapes.union(
        // Tombstone (top down)
        VoxelShapes.cuboid(bit * 3, bit * 13, 0f, bit * 4, bit * 13, bit),
        VoxelShapes.cuboid(bit * 3, bit * 12, 0f, bit * 4, bit * 12, bit),
        VoxelShapes.cuboid(bit * 2, 0f, 0f, bit * 15, bit * 10, bit),
        VoxelShapes.cuboid(bit, 0f, 0f, bit * 15, bit * 9, bit),
        // Dirt (top = closest section to tombstone)
        VoxelShapes.cuboid(bit, 0f, bit, bit * 15, bit, bit * 12),
        VoxelShapes.cuboid(bit * 2, 0f, bit * 12, bit * 14, bit, bit * 13),
        VoxelShapes.cuboid(bit * 3, 0f, bit * 13, bit * 13, bit, bit * 14),
        VoxelShapes.cuboid(bit * 5, 0f, bit * 14, bit * 11, bit, bit * 15)
    );

    public float SKULL_OFFSET = 0f;

    public TextPositions getTextPositions() {
        return new TextPositions();
    }

    public String getID() {
        return this.ID;
    };

    public List<String> getFiles() {
        return this.files;
    };

    public VoxelShape getGraveShape(String blockID) {
        return switch (blockID) {
            case "grave_old" -> this.GRAVE_SHAPE_OLD;
            case "grave_weathered" -> this.GRAVE_SHAPE_WEATHERED;
            case "grave_forgotten" -> this.GRAVE_SHAPE_FORGOTTEN;
            case "grave_expired" -> this.GRAVE_SHAPE_EXPIRED;
            default -> this.GRAVE_SHAPE;
        };
    }

    public float getSkullOffset() {
        return this.SKULL_OFFSET;
    }
}
