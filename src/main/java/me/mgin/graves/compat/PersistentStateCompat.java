package me.mgin.graves.compat;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.PersistentState;

import java.lang.reflect.Method;

/**
 * Compatibility layer for PersistentState operations that have changed in Minecraft 1.20.5
 */
public class PersistentStateCompat {

    public static NbtCompound writeNbt(PersistentState state, NbtCompound nbt) {
        try {
            WrapperLookup wrapperLookup = getDummyWrapperLookup();

            try {
                Method writeNbt = state.getClass().getMethod("writeNbt", NbtCompound.class, WrapperLookup.class);
                return (NbtCompound) writeNbt.invoke(state, nbt, wrapperLookup);
            } catch (NoSuchMethodException e) {
                Method writeNbt = state.getClass().getMethod("writeNbt", NbtCompound.class);
                return (NbtCompound) writeNbt.invoke(state, nbt);
            }
        } catch (Exception e) {
            System.err.println("Error writing PersistentState: " + e.getMessage());
            return nbt;
        }
    }

    public static <T extends PersistentState> T createFromNbt(NbtCompound nbt, java.util.function.Function<NbtCompound, T> factory) {
        try {
            return factory.apply(nbt);
        } catch (Exception e) {
            System.err.println("Error creating PersistentState: " + e.getMessage());
            return null;
        }
    }

    public static <T extends PersistentState> java.util.function.Function<NbtCompound, T> createFactory(
            java.util.function.Function<NbtCompound, T> oldFactory) {
        return (nbt) -> {
            try {
                return oldFactory.apply(nbt);
            } catch (Exception e) {
                System.err.println("Error in PersistentState factory: " + e.getMessage());
                return null;
            }
        };
    }

    private static WrapperLookup getDummyWrapperLookup() {
        try {
            Class<?> dynamicRegistryManagerClass = Class.forName("net.minecraft.registry.DynamicRegistryManager");
            Object emptyManager = dynamicRegistryManagerClass.getField("EMPTY").get(null);
            Method getWrapperLookup = dynamicRegistryManagerClass.getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookup.invoke(emptyManager);
        } catch (Exception e) {
            System.err.println("Failed to create dummy WrapperLookup: " + e.getMessage());
            return null;
        }
    }
}
