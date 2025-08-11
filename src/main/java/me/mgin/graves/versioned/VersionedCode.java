package me.mgin.graves.versioned;

import me.mgin.graves.Graves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * This class contains abstractions that are used in conjunction with stonecutter-kt
 * to keep the rest of the codebase more version agnostic.
 *
 * @see <a href="https://github.com/kikugie/stonecutter-kt">Stonecutter KT</a>
 */
public class VersionedCode {
    public static Text textFromJson(String json) {
        // Handle cases where the json is in fact not json but a plain string
        if (!json.startsWith("{")) {
            return Text.literal(json.replace("\"", ""));
        }

        //? if >1.20.2 {
        /*return Text.Serialization.fromJson(json);
        *///?} else {
        return Text.Serializer.fromJson((json));
        //?}
    }

    public static String getIssuerName(ServerPlayerEntity issuer) {
        //? if >1.20.2 {
        /*return issuer.getNameForScoreboard();
        *///?} else {
        return issuer.getEntityName();
        //?}
    }

    public static class Tags {
        // Block Tag Methods
        public static TagKey<Block> createCustomBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(Graves.MOD_ID, name));
        }

        public static boolean blockTagContains(BlockState state, TagKey<Block> tag) {
            return state.isIn(tag);
        }

        // Item Tag Methods
        public static TagKey<Item> createCustomItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(Graves.MOD_ID, name));
        }

        public static boolean itemTagContains(ItemStack stack, TagKey<Item> tag) {
            return stack.isIn(tag);
        }

        public static TagKey<Enchantment> createCustomEnchantTag(String name) {
            return TagKey.of(RegistryKeys.ENCHANTMENT, new Identifier(Graves.MOD_ID, name));
        }


    }


}
