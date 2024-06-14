package me.mgin.graves.block.utility;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

class Dimension {
    public DimensionType dimension = null;
    public World world = null;
    public int maxY = 0;
    public int minY = 0;

    public Dimension(World world) {
        this.world = world;
        this.dimension = world.getDimension();
        this.minY = this.dimension.minY();
        this.maxY = this.dimension.height() - Math.abs(this.minY);
    }

    /**
     * Checks whether the given pos is within the dimensional boundaries.
     */
    public boolean inBounds(BlockPos pos) {
        return this.maxY > pos.getY() && pos.getY() > this.minY;
    }

    /**
     * Moves an out of bounds BlockPos to be within the dimensional boundaries.
     */
    public BlockPos enforceBoundaries(BlockPos pos) {
        if (this.inBounds(pos)) return pos;

        // Handle dying at or above the dimension's maximum Y height
        if (pos.getY() >= this.maxY) {
            pos = new BlockPos(pos.getX(), this.maxY - 1, pos.getZ());
        }

        // Handle dying below the dimension's minimum Y height
        if (this.minY >= pos.getY()) {
            // Adds 6 to the minY as the overworld has 4-5 layers of bedrock
            pos = new BlockPos(pos.getX(), this.minY + 6, pos.getZ());
        }

        return pos;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public DimensionType getDimension() {
        return this.dimension;
    }
}