package me.mgin.graves.block.render.packs;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class RedefinedPack implements ResourcePack {
    public String ID = "forgotten-graves-redefined";
    public List<String> files = new ArrayList<>();

    public RedefinedPack() {
        // These are files that the ResourcePackChecker will use to determine if
        // the pack is active or not;
        this.files.add("textures/block/dead_mossy_gravestone.png");
        this.files.add("textures/block/less_mossy_cobblestone.png");
        this.files.add("textures/block/less_mossy_gravestone.png");
        this.files.add("textures/block/mossy_gravestone.png");
        this.files.add("textures/block/mycelium_rooted_dirt.png");
        this.files.add("textures/block/podzol_old.png");
    }

    static float bit = 0.0625f; // useful for creating grave shapes
    public final VoxelShape GRAVE_SHAPE = VoxelShapes.union(
        // Tombstone
        VoxelShapes.cuboid(bit * 2, 0f, 0f, bit * 14, bit * 16, bit * 3),
        // Dirt
        VoxelShapes.cuboid(bit, 0f, bit, bit * 15, bit, bit * 14)
    );
    public final VoxelShape GRAVE_SHAPE_OLD = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_WEATHERED = GRAVE_SHAPE;
    public final VoxelShape GRAVE_SHAPE_FORGOTTEN = VoxelShapes.union(
        // Tombstone (top down)
        VoxelShapes.cuboid(bit * 2, bit * 14, 0f, bit * 11, bit * 16, bit * 3), // top of tombstone
        VoxelShapes.cuboid(bit * 2, bit * 13, 0f, bit * 12, bit * 14, bit * 3),
        VoxelShapes.cuboid(bit * 2, bit * 9, 0f, bit * 13, bit * 13, bit * 3),
        VoxelShapes.cuboid(bit * 2, 0f, 0f, bit * 14, bit * 9, bit * 3), // Bottom part of tombstone
        // Dirt
        VoxelShapes.cuboid(bit, 0f, bit, bit * 15, bit, bit * 14)
    );

    // Redefined has no expired grave model yet, so this is the shape of the default one.
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

    public float SKULL_OFFSET = -0.1f;

    public TextPositions getTextPositions() {
        return new TextPositions(70, 3, 52);
    }

    @Override
    public String getID() {
        return this.ID;
    };

    @Override
    public List<String> getFiles() {
        return this.files;
    };

    @Override
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
