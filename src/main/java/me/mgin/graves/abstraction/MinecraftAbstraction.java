package me.mgin.graves.abstraction;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * This class contains method abstractions for Minecraft. This is to be used in conjunction
 * with stonecutter-kt to keep the rest of the codebase version agnostic.
 *
 * @see <a href="https://github.com/kikugie/stonecutter-kt">Stonecutter KT</a>
 */
public class MinecraftAbstraction {
    public static Text textFromJson(String json) {
        /*? if >1.20.2 {*/
        return Text.Serialization.fromJson(json);
        /*?} else {*//*
        return Text.Serializer.fromJson((json));
        *//*?}*/
    }

    public static String getIssuerName(ServerPlayerEntity issuer) {
        /*? if >1.20.2 {*/
        return issuer.getNameForScoreboard();
        /*?} else {*//*
        return issuer.getEntityName();
        *//*?}*/
    }

    public static Item.Settings getItemSettings() {
        /*? if >=1.20.5 {*/
        return new Item.Settings();
        /*?} else {*//*
        return FabricAbstraction.getFabricItemSettings();
        *//*?}*/
    }

}
