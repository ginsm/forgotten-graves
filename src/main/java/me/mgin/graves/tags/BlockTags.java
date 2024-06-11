package me.mgin.graves.tags;

import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;

public class BlockTags {
    public static final TagKey<Block> REPLACEABLE =
        VersionedCode.createCustomTag("replaceable");
    public static final TagKey<Block> IRREPLACEABLE = 
        VersionedCode.createCustomTag("irreplaceable");

}
