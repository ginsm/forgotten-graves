package me.mgin.graves.versioned;

import me.mgin.graves.Graves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
        /*? if >1.20.2 {*//*
        return Text.Serialization.fromJson(json);
        *//*?} else {*/
        return Text.Serializer.fromJson((json));
        /*?}*/
    }

    public static String getIssuerName(ServerPlayerEntity issuer) {
        /*? if >1.20.2 {*//*
        return issuer.getNameForScoreboard();
        *//*?} else {*/
        return issuer.getEntityName();
        /*?}*/
    }

    public static TagKey<Block> createCustomTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(Graves.MOD_ID,name));
    }

    public static boolean TagContains(BlockState state, TagKey<Block> tag) {
        return state.isIn(tag);
    }

}
