package me.mgin.graves.event.server.useblock.item;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Particles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

/**
 * This event handler
 */
public class Skull {
    /**
     * This event handler determines whether a player is holding a skull and clicking
     * on an unowned grave. If that is the case, the grave will be set to display the
     * skull; the skull is not consumed in the process.
     *
     * @param world World
     * @param hand Hand
     * @param pos BlockPos
     * @param item Item
     * @param stack ItemStack
     * @param entity GraveBlockEntity
     * @return Boolean
     */
    public static Boolean handle(World world, Hand hand, BlockPos pos,
                                 Item item, ItemStack stack, GraveBlockEntity entity) {
        // Cases in which an early termination is necessary
        boolean hasOwner = entity.getGraveOwner() != null;
        boolean isValidSkull = validSkulls.contains(item.asItem().toString());
        boolean isMainHand = hand.equals(Hand.MAIN_HAND);

        if (hasOwner || !isValidSkull || !isMainHand) return false;

        NbtCompound initialItemNbt = stack.getNbt();
        BlockState state = entity.getState();
        String skull;

        // If the nbt isn't null, get the custom skull texture; otherwise set to item string
        if (initialItemNbt != null) {
            NbtCompound itemNbt = initialItemNbt.contains("tag") ? initialItemNbt.getCompound("tag") : initialItemNbt;
            skull = itemNbt.getCompound("SkullOwner")
                .getCompound("Properties")
                .getList("textures", 10)
                .getCompound(0)
                .getString("Value");

            if (skull.equals("") || skull.equals(entity.getGraveSkull())) return false;

            entity.setGraveSkull(skull);
        } else {
            entity.setGraveSkull(item.asItem().toString());
        }

        // Required for client sync
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);

        // Polish
        world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_HIT, SoundCategory.BLOCKS, 1f, 1f);
        Particles.spawnAtBlockBottom(world, pos, ParticleTypes.SOUL, 6, 0.025, 0.125);

        return true;
    }

    private static final HashSet<String> validSkulls = new HashSet<>() {
        {
            add("wither_skeleton_skull");
            add("skeleton_skull");
            add("player_head");
            add("zombie_head");
            add("creeper_head");
        }
    };
}
