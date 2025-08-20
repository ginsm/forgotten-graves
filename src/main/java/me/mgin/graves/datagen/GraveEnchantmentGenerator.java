package me.mgin.graves.datagen;

import me.mgin.graves.tags.GraveEnchantTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class GraveEnchantmentGenerator extends FabricTagProvider.EnchantmentTagProvider {
    public GraveEnchantmentGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(GraveEnchantTags.BINDING_CURSES)
                .add(Enchantments.BINDING_CURSE);

        getOrCreateTagBuilder(GraveEnchantTags.VANISHING_CURSES)
                .add(Enchantments.VANISHING_CURSE);

        getOrCreateTagBuilder(GraveEnchantTags.SOULBOUND_ENCHANTS)
                // fabric mods with soulbound
                .addOptional(new Identifier("enderzoology", "soulbound"))
                .addOptional(new Identifier("soulbound", "soulbound"))
                // forge ones below, for people using Sinytra Connector; completely untested though
                .addOptional(new Identifier("apotheosis", "soulbound"))
                .addOptional(new Identifier("tetra", "soulbound"))
                .addOptional(new Identifier("mna", "soulbound"))
                .addOptional(new Identifier("ensorcellation", "soulbound"));
    }
}
