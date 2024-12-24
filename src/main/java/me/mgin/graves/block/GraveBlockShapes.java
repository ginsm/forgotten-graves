package me.mgin.graves.block;

import me.mgin.graves.block.render.GraveResourcePackManager;
import me.mgin.graves.block.render.packs.DefaultPack;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class GraveBlockShapes {
    public static VoxelShape getGraveShape(BlockState state, String blockID, boolean useDefault) {
        Direction facing = state.get(HorizontalFacingBlock.FACING);
        VoxelShape shape;

        if (useDefault) {
            shape = new DefaultPack().getGraveShape(blockID);
        } else {
            shape = GraveResourcePackManager.getActivePack().getGraveShape(blockID);
        }

        return switch (facing) {
            case DOWN, UP -> null; // It can't face down or up anyway.
            case NORTH -> rotateShape(Direction.NORTH, shape);
            case EAST -> rotateShape(Direction.EAST, shape);
            case SOUTH -> rotateShape(Direction.SOUTH, shape);
            case WEST -> rotateShape(Direction.WEST, shape);
        };
    }

    /**
     * Rotates the outline/collision shape based on direction
     */
    public static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontal() - Direction.NORTH.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(
                    1 - maxZ, minY, minX,
                    1 - minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }
}