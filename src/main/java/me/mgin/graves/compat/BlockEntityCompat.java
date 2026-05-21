package me.mgin.graves.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

/**
 * Compatibility layer for BlockEntity operations that have changed in Minecraft 1.20.5
 */
public class BlockEntityCompat {

    /**
     * Reads NBT data into a BlockEntity, handling API differences between versions
     */
    public static void readNbt(BlockEntity blockEntity, NbtCompound nbt) {
        try {
            WrapperLookup registryLookup = getRegistryLookup(blockEntity);

            try {
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class, WrapperLookup.class)
                    .invoke(blockEntity, nbt, registryLookup);
            } catch (NoSuchMethodException e) {
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            }
        } catch (Exception e) {
            System.err.println("Error reading NBT data: " + e.getMessage());
            try {
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            } catch (Exception ex) {
                System.err.println("Failed to read NBT data: " + ex.getMessage());
            }
        }
    }

    /**
     * Writes BlockEntity data to NBT, handling API differences between versions
     */
    public static NbtCompound writeNbt(BlockEntity blockEntity, NbtCompound nbt) {
        try {
            WrapperLookup registryLookup = getRegistryLookup(blockEntity);

            try {
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class, WrapperLookup.class)
                    .invoke(blockEntity, nbt, registryLookup);
            } catch (NoSuchMethodException e) {
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            }
            return nbt;
        } catch (Exception e) {
            System.err.println("Error writing NBT data: " + e.getMessage());
            try {
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            } catch (Exception ex) {
                System.err.println("Failed to write NBT data: " + ex.getMessage());
            }
            return nbt;
        }
    }

    /**
     * Creates an NBT compound from a BlockEntity, handling API differences between versions
     */
    public static NbtCompound toNbt(BlockEntity blockEntity) {
        NbtCompound nbt = new NbtCompound();
        return writeNbt(blockEntity, nbt);
    }

    private static WrapperLookup getRegistryLookup(BlockEntity blockEntity) {
        if (blockEntity.getWorld() == null) {
            try {
                return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
            } catch (Exception e) {
                return createDummyWrapperLookup();
            }
        }

        try {
            Object registryManager = blockEntity.getWorld().getClass().getMethod("getRegistryManager")
                .invoke(blockEntity.getWorld());
            return (WrapperLookup) registryManager.getClass().getMethod("getWrapperLookup")
                .invoke(registryManager);
        } catch (Exception e) {
            return createDummyWrapperLookup();
        }
    }

    private static WrapperLookup createDummyWrapperLookup() {
        try {
            return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
        } catch (Exception e) {
            System.err.println("Failed to create dummy WrapperLookup: " + e.getMessage());
            return null;
        }
    }
}
