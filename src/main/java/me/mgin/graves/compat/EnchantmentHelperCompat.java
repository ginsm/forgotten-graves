package me.mgin.graves.compat;

import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Compatibility layer for EnchantmentHelper API changes in Minecraft 1.20.5
 */
public class EnchantmentHelperCompat {

    @SuppressWarnings("unchecked")
    public static Map<?, Integer> getEnchantments(ItemStack stack) {
        try {
            try {
                return (Map<?, Integer>) net.minecraft.enchantment.EnchantmentHelper.class
                    .getMethod("get", ItemStack.class).invoke(null, stack);
            } catch (NoSuchMethodException e) {
                return (Map<?, Integer>) net.minecraft.enchantment.EnchantmentHelper.class
                    .getMethod("getEnchantmentLevels", ItemStack.class).invoke(null, stack);
            }
        } catch (Exception e) {
            return Map.of();
        }
    }
}
