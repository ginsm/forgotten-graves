package me.mgin.graves.tags;

import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public class GraveEnchantTags {
    public static final TagKey<Enchantment> VANISHING_CURSES =
            VersionedCode.Tags.createCustomEnchantTag("vanishing_curses");
    public static final TagKey<Enchantment> BINDING_CURSES =
            VersionedCode.Tags.createCustomEnchantTag("binding_curses");
    public static final TagKey<Enchantment> SOULBOUND_ENCHANTS =
            VersionedCode.Tags.createCustomEnchantTag("soulbound_enchants");

    public static boolean hasBindingCurse(ItemStack stack) {
        return hasTaggedEnchantment(stack, BINDING_CURSES);
    }

    public static boolean hasVanishingCurse(ItemStack stack) {
        return hasTaggedEnchantment(stack, VANISHING_CURSES);
    }

    public static boolean hasSoulboundEnchantment(ItemStack stack) {
        return hasTaggedEnchantment(stack, SOULBOUND_ENCHANTS);
    }

    private static boolean hasTaggedEnchantment(ItemStack stack, TagKey<Enchantment> tag) {
        if (!stack.hasEnchantments()) return false;

        for (Enchantment enchant : EnchantmentHelper.get(stack).keySet()) {
            RegistryEntry<Enchantment> entry = Registries.ENCHANTMENT.getEntry(enchant);
            if (entry.isIn(tag)) return true;
        }

        return false;
    }
}
