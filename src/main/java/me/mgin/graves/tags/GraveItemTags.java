package me.mgin.graves.tags;

import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public class GraveItemTags {
    public static final TagKey<Item> DECAY_ITEM =
        VersionedCode.Tags.createCustomItemTag("decay_item");
}
