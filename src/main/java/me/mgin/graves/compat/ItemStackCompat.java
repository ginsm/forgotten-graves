package me.mgin.graves.compat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

/**
 * Compatibility layer for ItemStack operations that have changed in Minecraft 1.20.5
 */
public class ItemStackCompat {

    public static boolean hasNbt(ItemStack stack) {
        try {
            return (boolean) stack.getClass().getMethod("hasNbt").invoke(stack);
        } catch (Exception e) {
            try {
                return stack.getClass().getMethod("getComponents").invoke(stack) != null;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static NbtCompound getOrCreateNbt(ItemStack stack) {
        try {
            return (NbtCompound) stack.getClass().getMethod("getOrCreateNbt").invoke(stack);
        } catch (Exception e) {
            NbtCompound nbt = getNbt(stack);
            if (nbt.isEmpty()) {
                nbt = new NbtCompound();
                try {
                    stack.getClass().getMethod("setNbt", NbtCompound.class).invoke(stack, nbt);
                } catch (Exception ignored) {}
            }
            return nbt;
        }
    }

    public static NbtCompound getOrCreateSubNbt(ItemStack stack, String key) {
        try {
            return (NbtCompound) stack.getClass().getMethod("getOrCreateSubNbt", String.class).invoke(stack, key);
        } catch (Exception e) {
            NbtCompound parent = getOrCreateNbt(stack);
            if (!parent.contains(key)) parent.put(key, new NbtCompound());
            return parent.getCompound(key);
        }
    }

    public static void setCustomName(ItemStack stack, net.minecraft.text.Text name) {
        try {
            stack.getClass().getMethod("setCustomName", net.minecraft.text.Text.class).invoke(stack, name);
        } catch (Exception e) {
            try {
                Object componentType = Class.forName("net.minecraft.component.DataComponentTypes").getField("CUSTOM_NAME").get(null);
                stack.getClass().getMethod("set", Class.forName("net.minecraft.component.DataComponentType"), Object.class).invoke(stack, componentType, name);
            } catch (Exception ex) {
                getOrCreateSubNbt(stack, "display").putString("Name", me.mgin.graves.versioned.VersionedCode.textToJson(name));
            }
        }
    }

    public static void removeSubNbt(ItemStack stack, String key) {
        try {
            stack.getClass().getMethod("removeSubNbt", String.class).invoke(stack, key);
        } catch (Exception e) {
            NbtCompound nbt = getNbt(stack);
            if (nbt != null && !nbt.isEmpty()) {
                nbt.remove(key);
            }
        }
    }

    public static boolean hasCustomName(ItemStack stack) {
        try {
            try {
                return (boolean) stack.getClass().getMethod("hasCustomName").invoke(stack);
            } catch (NoSuchMethodException e) {
                Text displayName = stack.getName();
                Text defaultName = (Text) stack.getItem().getClass().getMethod("getName", ItemStack.class).invoke(stack.getItem(), stack);
                return !displayName.equals(defaultName);
            }
        } catch (Exception e) {
            System.err.println("Error checking custom name: " + e.getMessage());
            return false;
        }
    }

    public static NbtCompound getNbt(ItemStack stack) {
        try {
            return (NbtCompound) stack.getClass().getMethod("getNbt").invoke(stack);
        } catch (NoSuchMethodException e) {
            var lookup = SerializationHelper.getWrapperLookup();
            if (lookup != null) {
                try {
                    Object encoded = stack.getClass().getMethod("encode", lookup.getClass()).invoke(stack, lookup);
                    if (encoded instanceof NbtCompound) return (NbtCompound) encoded;
                } catch (Exception ignored) {}
            }
            NbtCompound nbt = new NbtCompound();
            try {
                stack.getClass().getMethod("writeNbt", NbtCompound.class).invoke(stack, nbt);
            } catch (Exception ignored) {}
            return nbt;
        } catch (Exception e) {
            return new NbtCompound();
        }
    }

    public static void damage(ItemStack stack, int amount, LivingEntity entity, Hand hand) {
        try {
            try {
                stack.getClass().getMethod("damage", int.class, LivingEntity.class, java.util.function.Consumer.class)
                    .invoke(stack, amount, entity, (java.util.function.Consumer<LivingEntity>) (p) -> {
                        if (p instanceof PlayerEntity) {
                            try {
                                p.getClass().getMethod("sendToolBreakStatus", Hand.class).invoke(p, hand);
                            } catch (Exception ex) {
                                System.err.println("Error sending tool break status: " + ex.getMessage());
                            }
                        }
                    });
            } catch (NoSuchMethodException e) {
                try {
                    stack.getClass().getMethod("damage", int.class, LivingEntity.class).invoke(stack, amount, entity);
                } catch (NoSuchMethodException ex) {
                    int damage = (int) stack.getClass().getMethod("getDamage").invoke(stack);
                    stack.getClass().getMethod("setDamage", int.class).invoke(stack, damage + amount);
                }
            }
        } catch (Exception e) {
            System.err.println("Error damaging item: " + e.getMessage());
        }
    }
}
