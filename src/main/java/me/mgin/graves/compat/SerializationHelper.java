package me.mgin.graves.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for serialization operations in Minecraft 1.20.5+
 */
public class SerializationHelper {

    private static volatile WrapperLookup CACHED_WRAPPER_LOOKUP = null;

    public static void setWrapperLookup(WrapperLookup lookup) {
        if (lookup != null) {
            CACHED_WRAPPER_LOOKUP = lookup;
        }
    }

    public static WrapperLookup getWrapperLookup() {
        WrapperLookup cached = CACHED_WRAPPER_LOOKUP;
        if (cached != null) return cached;

        try {
            try {
                Method getWrapperLookupMethod = DynamicRegistryManager.class.getMethod("getWrapperLookup");
                return (WrapperLookup) getWrapperLookupMethod.invoke(DynamicRegistryManager.EMPTY);
            } catch (Exception e) {
                // Ignore and try other approaches
            }

            try {
                Field field = DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP");
                return (WrapperLookup) field.get(null);
            } catch (Exception e) {
                // Ignore and try next approach
            }

            try {
                Field field = Registries.class.getField("WRAPPER_LOOKUP");
                return (WrapperLookup) field.get(null);
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        if (profile == null) {
            return nbt;
        }

        NbtCompound profileNbt = new NbtCompound();
        if (profile.getId() != null) {
            profileNbt.putUuid("Id", profile.getId());
        }
        if (profile.getName() != null) {
            profileNbt.putString("Name", profile.getName());
        }
        nbt.put("GraveOwner", profileNbt);
        return nbt;
    }

    public static GameProfile readGameProfile(NbtCompound nbt) {
        if (nbt == null || !nbt.contains("GraveOwner", NbtElement.COMPOUND_TYPE)) {
            return null;
        }

        NbtCompound profileNbt = nbt.getCompound("GraveOwner");
        UUID id = profileNbt.contains("Id") ? profileNbt.getUuid("Id") : null;
        String name = profileNbt.contains("Name") ? profileNbt.getString("Name") : null;
        return new GameProfile(id, name);
    }

    public static NbtCompound writeInventories(NbtCompound nbt, Map<String, DefaultedList<ItemStack>> inventories, WrapperLookup registryLookup) {
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        NbtCompound inventoriesNbt = new NbtCompound();
        NbtCompound itemCountNbt = new NbtCompound();

        for (Map.Entry<String, DefaultedList<ItemStack>> entry : inventories.entrySet()) {
            String id = entry.getKey();
            DefaultedList<ItemStack> inventory = entry.getValue();

            if (inventory == null || inventory.isEmpty()) {
                continue;
            }

            itemCountNbt.putInt(id, inventory.size());
            NbtList itemsNbt = new NbtList();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.get(i);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putInt("Slot", i);
                serializeItemStack(stack, itemNbt, registryLookup);
                itemsNbt.add(itemNbt);
            }

            inventoriesNbt.put(id, itemsNbt);
        }

        if (!inventoriesNbt.isEmpty()) {
            nbt.put("Inventories", inventoriesNbt);
        }
        if (!itemCountNbt.isEmpty()) {
            nbt.put("ItemCount", itemCountNbt);
        }

        return nbt;
    }

    public static void readInventories(NbtCompound nbt, Map<String, DefaultedList<ItemStack>> inventories, WrapperLookup registryLookup) {
        if (nbt == null || !nbt.contains("Inventories")) {
            return;
        }

        NbtCompound inventoriesNbt = nbt.getCompound("Inventories");
        NbtCompound itemCountNbt = nbt.contains("ItemCount") ? nbt.getCompound("ItemCount") : new NbtCompound();

        for (String id : inventoriesNbt.getKeys()) {
            try {
                NbtList itemsNbt = inventoriesNbt.getList(id, NbtElement.COMPOUND_TYPE);
                int size = itemCountNbt.contains(id) ? itemCountNbt.getInt(id) : 0;

                if (size == 0) {
                    for (int i = 0; i < itemsNbt.size(); i++) {
                        NbtCompound itemNbt = itemsNbt.getCompound(i);
                        if (itemNbt.contains("Slot")) {
                            int slot = itemNbt.getInt("Slot");
                            size = Math.max(size, slot + 1);
                        }
                    }
                }

                if (size > 0) {
                    DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);

                    for (int i = 0; i < itemsNbt.size(); i++) {
                        NbtCompound itemNbt = itemsNbt.getCompound(i);
                        if (itemNbt.contains("Slot")) {
                            int slot = itemNbt.getInt("Slot");
                            ItemStack stack = deserializeItemStack(itemNbt, registryLookup);
                            if (slot >= 0 && slot < inventory.size()) {
                                inventory.set(slot, stack);
                            }
                        }
                    }

                    inventories.put(id, inventory);
                }
            } catch (Exception e) {
                System.err.println("[SerializationHelper] Error reading inventory '" + id + "': " + e.getMessage());
            }
        }
    }

    private static ItemStack deserializeItemStack(NbtCompound itemNbt, WrapperLookup registryLookup) {
        try {
            try {
                Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                return (ItemStack) fromNbtMethod.invoke(null, itemNbt);
            } catch (Exception e) {
                // Continue
            }

            if (registryLookup != null) {
                try {
                    Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class, WrapperLookup.class);
                    return (ItemStack) fromNbtMethod.invoke(null, itemNbt, registryLookup);
                } catch (Exception e) {
                    // Continue
                }
            }

            return createItemStackManually(itemNbt);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    private static ItemStack createItemStackManually(NbtCompound itemNbt) {
        try {
            if (itemNbt.contains("id")) {
                String idString = itemNbt.getString("id");
                Identifier itemId = new Identifier(idString);
                int count = itemNbt.contains("Count") ?
                    (itemNbt.get("Count").getType() == NbtElement.BYTE_TYPE ? itemNbt.getByte("Count") : itemNbt.getInt("Count")) : 1;

                ItemStack stack = new ItemStack(Registries.ITEM.get(itemId), count);

                if (itemNbt.contains("Damage")) {
                    stack.setDamage(itemNbt.getInt("Damage"));
                }

                if (itemNbt.contains("tag")) {
                    try {
                        Method setNbtMethod = ItemStack.class.getMethod("setNbt", NbtCompound.class);
                        setNbtMethod.invoke(stack, itemNbt.getCompound("tag").copy());
                    } catch (Exception e) {
                        // Skip complex NBT
                    }
                }

                return stack;
            }
        } catch (Exception e) {
            // Fall through
        }

        return ItemStack.EMPTY;
    }

    public static NbtCompound serializeItemStack(ItemStack stack, NbtCompound nbt, WrapperLookup registryLookup) {
        if (stack.isEmpty()) {
            return nbt;
        }

        try {
            try {
                Method writeNbtDirectMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                writeNbtDirectMethod.invoke(stack, nbt);
                return nbt;
            } catch (Exception e) {
                // Continue
            }

            try {
                Method writeComponentsNbtMethod = ItemStack.class.getMethod("writeComponentsNbt", NbtCompound.class);
                writeComponentsNbtMethod.invoke(stack, nbt);
                return nbt;
            } catch (NoSuchMethodException e) {
                // Continue
            }

            if (registryLookup != null) {
                try {
                    Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class, WrapperLookup.class);
                    writeNbtMethod.invoke(stack, nbt, registryLookup);
                    return nbt;
                } catch (NoSuchMethodException e) {
                    // Continue
                }
            }

            try {
                Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                writeNbtMethod.invoke(stack, nbt);
                return nbt;
            } catch (Exception e) {
                manualItemSerialization(stack, nbt);
                return nbt;
            }
        } catch (Exception e) {
            manualItemSerialization(stack, nbt);
            return nbt;
        }
    }

    private static void manualItemSerialization(ItemStack stack, NbtCompound nbt) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        nbt.putString("id", id.toString());
        nbt.putInt("Count", stack.getCount());

        if (stack.isDamaged()) {
            nbt.putInt("Damage", stack.getDamage());
        }

        try {
            Method getNbtMethod = ItemStack.class.getMethod("getNbt");
            NbtCompound tag = (NbtCompound) getNbtMethod.invoke(stack);
            if (tag != null && !tag.isEmpty()) {
                nbt.put("tag", tag.copy());
            }
        } catch (Exception ignored) {
            // Skip
        }
    }
}
