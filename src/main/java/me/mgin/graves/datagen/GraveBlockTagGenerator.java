package me.mgin.graves.datagen;

import me.mgin.graves.tags.GraveBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class GraveBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public GraveBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        FabricTagBuilder replaceable = getOrCreateTagBuilder(GraveBlockTags.REPLACEABLE);
        FabricTagBuilder sinkThrough = getOrCreateTagBuilder(GraveBlockTags.SINK_THROUGH);
        FabricTagBuilder doNotReplace = getOrCreateTagBuilder(GraveBlockTags.DO_NOT_REPLACE);

        // doNotReplace
        addBlock(Blocks.BEDROCK, doNotReplace);

        // replaceable
        addBlock(Blocks.POWDER_SNOW, replaceable);

        // replaceable + sinkThrough
        addBlockTag(BlockTags.FLOWERS, replaceable, sinkThrough);
        addBlockTag(BlockTags.SAPLINGS, replaceable, sinkThrough);
        addBlockTag(BlockTags.CORAL_PLANTS, replaceable, sinkThrough);
        addBlockTag(BlockTags.CAVE_VINES, replaceable, sinkThrough);
        addBlockTag(BlockTags.CROPS, replaceable, sinkThrough);
        addBlock(Blocks.TALL_GRASS, replaceable, sinkThrough);
        addBlock(Blocks.SEAGRASS, replaceable, sinkThrough);
        addBlock(Blocks.TALL_SEAGRASS, replaceable, sinkThrough);
        addBlock(Blocks.FERN, replaceable, sinkThrough);
        addBlock(Blocks.LARGE_FERN, replaceable, sinkThrough);
        addBlock(Blocks.TORCH, replaceable, sinkThrough);
        addBlock(Blocks.BROWN_MUSHROOM, replaceable, sinkThrough);
        addBlock(Blocks.RED_MUSHROOM, replaceable, sinkThrough);
        addBlock(Blocks.CRIMSON_FUNGUS, replaceable, sinkThrough);
        addBlock(Blocks.CRIMSON_ROOTS, replaceable, sinkThrough);
        addBlock(Blocks.WARPED_FUNGUS, replaceable, sinkThrough);
        addBlock(Blocks.WARPED_ROOTS, replaceable, sinkThrough);
        addBlock(Blocks.HANGING_ROOTS, replaceable, sinkThrough);
        addBlock(Blocks.NETHER_SPROUTS, replaceable, sinkThrough);
        addBlock(Blocks.NETHER_WART, replaceable, sinkThrough);
        addBlock(Blocks.DEAD_BUSH, replaceable, sinkThrough);
        addBlock(Blocks.LILY_PAD, replaceable, sinkThrough);
        addBlock(Blocks.SCULK_VEIN, replaceable, sinkThrough);
        addBlock(Blocks.COBWEB, replaceable, sinkThrough);
        addBlock(Blocks.SUGAR_CANE, replaceable, sinkThrough);
        addBlock(Blocks.BAMBOO, replaceable, sinkThrough);
        addBlock(Blocks.KELP, replaceable, sinkThrough);
        addBlock(Blocks.KELP_PLANT, replaceable, sinkThrough);
        addBlock(Blocks.WEEPING_VINES, replaceable, sinkThrough);
        addBlock(Blocks.TWISTING_VINES, replaceable, sinkThrough);
        addBlock(Blocks.VINE, replaceable, sinkThrough);
        addBlock(Blocks.SNOW, replaceable, sinkThrough);
        addBlock(Blocks.FIRE, replaceable, sinkThrough);
        addBlock(Blocks.SOUL_FIRE, replaceable, sinkThrough);
        addBlock(Blocks.GLOW_LICHEN, replaceable, sinkThrough);
        addBlock(Blocks.SWEET_BERRY_BUSH, replaceable, sinkThrough);
        addBlock(Blocks.BIG_DRIPLEAF, replaceable, sinkThrough);
        addBlock(Blocks.BIG_DRIPLEAF_STEM, replaceable, sinkThrough);
        addBlock(Blocks.SMALL_DRIPLEAF, replaceable, sinkThrough);
        addBlock(Blocks.BUBBLE_COLUMN, replaceable, sinkThrough);
        //? if >1.20.2 {
        addBlock(Blocks.SHORT_GRASS, replaceable, sinkThrough);
        //?} else {
        /*addBlock(Blocks.GRASS, replaceable, sinkThrough);
        *///?}
    }

    @SafeVarargs
    private void addBlock(Block block, FabricTagBuilder... builders) {
        for (FabricTagBuilder b : builders) {
            b.add(block);
        }
    }

    @SafeVarargs
    private void addBlockTag(TagKey<Block> tag, FabricTagBuilder... builders) {
        for (FabricTagBuilder b : builders) {
            b.addOptionalTag(tag);
        }
    }
}
