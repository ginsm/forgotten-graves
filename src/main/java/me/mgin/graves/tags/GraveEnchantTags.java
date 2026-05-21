package me.mgin.graves.tags;

//? if >=1.20.5 {
import me.mgin.graves.compat.EnchantmentHelperCompat;
import me.mgin.graves.compat.ItemStackCompat;
//?}
import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.enchantment.Enchantment;
//? if <1.20.5 {
import net.minecraft.enchantment.EnchantmentHelper;
//?}
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
        // Botania's Resolute Ivy is treated like Soulbound
        return hasTaggedEnchantment(stack, SOULBOUND_ENCHANTS) || hasResoluteIvy(stack);
    }

    public static boolean hasResoluteIvy(ItemStack stack) {
        //? if >=1.20.5 {
        return ItemStackCompat.hasNbt(stack) && ItemStackCompat.getOrCreateNbt(stack).contains("Botania_keepIvy");
        //?} else {
        return stack.hasNbt() && stack.getOrCreateNbt().contains("Botania_keepIvy");
        //?}
    }

    private static boolean hasTaggedEnchantment(ItemStack stack, TagKey<Enchantment> tag) {
        if (!stack.hasEnchantments()) return false;

        //? if >=1.20.5 {
        return hasTaggedEnchantment1205(stack, tag);
        //?} else {
        for (Enchantment enchant : EnchantmentHelper.get(stack).keySet()) {
            RegistryEntry<Enchantment> entry = Registries.ENCHANTMENT.getEntry(enchant);
            if (entry.isIn(tag)) return true;
        }
        return false;
        //?}
    }

    //? if >=1.20.5 {
    @SuppressWarnings("unchecked")
    private static boolean hasTaggedEnchantment1205(ItemStack stack, TagKey<Enchantment> tag) {
        for (Object key : EnchantmentHelperCompat.getEnchantments(stack).keySet()) {
            RegistryEntry<Enchantment> entry = key instanceof RegistryEntry
                ? (RegistryEntry<Enchantment>) key
                : Registries.ENCHANTMENT.getEntry((Enchantment) key);
            if (entry.isIn(tag)) return true;
        }
        return false;
    }
    //?}
}
