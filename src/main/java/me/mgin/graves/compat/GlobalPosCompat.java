package me.mgin.graves.compat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

/**
 * Compatibility layer for GlobalPos operations that have changed in Minecraft 1.20.5
 */
public class GlobalPosCompat {

    public static BlockPos getPos(GlobalPos globalPos) {
        try {
            try {
                return (BlockPos) globalPos.getClass().getMethod("getPos").invoke(globalPos);
            } catch (NoSuchMethodException e) {
                return (BlockPos) globalPos.getClass().getMethod("pos").invoke(globalPos);
            }
        } catch (Exception e) {
            return BlockPos.ORIGIN;
        }
    }

    public static Object getDimension(GlobalPos globalPos) {
        try {
            try {
                return globalPos.getClass().getMethod("getDimension").invoke(globalPos);
            } catch (NoSuchMethodException e) {
                return globalPos.getClass().getMethod("dimension").invoke(globalPos);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDimensionString(GlobalPos globalPos) {
        Object dim = getDimension(globalPos);
        if (dim == null) return "minecraft:overworld";
        try {
            Object value = dim.getClass().getMethod("getValue").invoke(dim);
            return value != null ? value.toString() : "minecraft:overworld";
        } catch (Exception e) {
            return "minecraft:overworld";
        }
    }
}
