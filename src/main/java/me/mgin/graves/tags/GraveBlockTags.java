package me.mgin.graves.tags;

import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;

public class GraveBlockTags {
    public static final TagKey<Block> REPLACEABLE =
        VersionedCode.Tags.createCustomBlockTag("replaceable");
    public static final TagKey<Block> DO_NOT_REPLACE =
        VersionedCode.Tags.createCustomBlockTag("do_not_replace");
    public static final TagKey<Block> SINK_THROUGH =
        VersionedCode.Tags.createCustomBlockTag(("sink_through"));

}
