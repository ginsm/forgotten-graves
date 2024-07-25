package me.mgin.graves.datagen;

import me.mgin.graves.tags.GraveItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class GraveItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public GraveItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        FabricTagBuilder decayItem = getOrCreateTagBuilder(GraveItemTags.DECAY_ITEM);

        addBlock(Items.VINE, decayItem);
        addBlock(Items.TWISTING_VINES, decayItem);
        addBlock(Items.WEEPING_VINES, decayItem);
        addBlock(Items.BROWN_MUSHROOM, decayItem);
        addBlock(Items.RED_MUSHROOM, decayItem);
    }

    @SafeVarargs
    private void addBlock(Item item, FabricTagBuilder... builders) {
        for (FabricTagBuilder b : builders) {
            b.add(item);
        }
    }

    @SafeVarargs
    private void addItemTag(TagKey<Item> tag, FabricTagBuilder... builders) {
        for (FabricTagBuilder b : builders) {
            b.addOptionalTag(tag);
        }
    }
}
