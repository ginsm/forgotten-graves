package me.mgin.graves.block.utility;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.tags.GraveBlockTags;
import me.mgin.graves.util.Responder;
import me.mgin.graves.versioned.VersionedCode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Date;

public class PlaceGrave {
    /**
     * Attempts to spawn a grave at the given position; if the position is invalid or a sinkable block it will look
     * for a more suitable position.
     */
    public static void place(World world, Vec3d vecPos, PlayerEntity player) {
        if (world.isClient()) return;

        // Get dimension boundaries
        Dimension dimension = new Dimension(world);

        // Convert Vec to BlockPos and enforce dimension boundaries
        BlockPos pos = dimension.enforceBoundaries(new BlockPos(
            (int) Math.floor(vecPos.x),
            (int) Math.floor(vecPos.y),
            (int) Math.floor(vecPos.z)
        ));

        // Sink functionality
        if (graveShouldSink(world, pos, player)) {
            pos = findLowestSpawnPos(world, dimension, pos, player);
        }

        // Try and find a new valid, optimal position
        if (!canPlaceGrave(world, dimension, pos) || !isLiquidAirOrReplaceable(world, pos)) {
            pos = findOptimalSpawnPos(world, dimension, pos, player);
        }

        // Place the grave
        spawnGrave(world, pos, player);
    }

    /**
     * Iterates downwards until it finds a block that is not marked as sinkable or reaches the
     * minimum Y level.
     */
    private static BlockPos findLowestSpawnPos(World world, Dimension dimension, BlockPos pos, PlayerEntity player) {
        int depth = dimension.getMinY() + 1;
        int start = pos.getY() - 1; // Starts at the block below the potential grave pos
        BlockPos finalPos = pos;

        for (int i = start; i >= depth; i--) {
            BlockPos newPos = new BlockPos(pos.getX(), i, pos.getZ());

            if (!graveShouldSink(world, newPos, player)) {
                finalPos = newPos;
                break;
            }

            // If no spot is found when reaching the lowest depth, simply set the
            // spawn position to the max depth.
            if (i == depth) finalPos = newPos;
        }

        return finalPos;
    }

    /**
     * Iterates outwards until it finds an optimal position to spawn the grave; ensuring the grave sinks when necessary.
     */
    private static BlockPos findOptimalSpawnPos(World world, Dimension dimension, BlockPos pos, PlayerEntity player) {
        BlockPos suboptimalPos = pos;

        for (BlockPos newPos : BlockPos.iterateOutwards(pos, 8, 80, 8)) {
            if (canPlaceGrave(world, dimension, newPos)) {
                // Sink the new position if needed
                if (graveShouldSink(world, newPos, player)) {
                    newPos = findLowestSpawnPos(world, dimension, newPos, player);
                }

                // Checks for the optimal spot; an optimal spot is considered either liquid or air.
                if (isLiquidAirOrReplaceable(world, newPos)) {
                    return newPos;
                }

                // Store the first suboptimal position; this spot can still be placed but might end up breaking
                // or replacing another block. This is a fallback in case an optimal spot isn't found.
                boolean suboptimalUnchanged = suboptimalPos == pos;
                boolean cantPlaceInitial = !canPlaceGrave(world, dimension, pos);
                boolean canPlaceNewPos = canPlaceGrave(world, dimension, newPos);
                if (suboptimalUnchanged && cantPlaceInitial && canPlaceNewPos) {
                    suboptimalPos = newPos;
                }
            }
        }

        return suboptimalPos;
    }

    /**
     * Spawns a grave at the given BlockPos.
     */
    public static void spawnGrave(World world, BlockPos pos, PlayerEntity player) {
        // Get block and state
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Set block and rotational state
        world.setBlockState(pos, GraveBlocks.GRAVE.getDefaultState().with(
            Properties.HORIZONTAL_FACING,
            player.getHorizontalFacing().getOpposite()
        ));

        // Create new grave entity
        GraveBlockEntity graveEntity = new GraveBlockEntity(pos, world.getBlockState(pos));

        // Set the grave inventories and clear player's inventories
        for (InventoriesApi api : Graves.inventories) {
            DefaultedList<ItemStack> inventory = api.getInventory(player);

            if (inventory == null)
                continue;

            graveEntity.setInventory(api.getID(), inventory);
            api.clearInventory(player);
        }

        // Set grave owner
        graveEntity.setGraveOwner(player.getGameProfile());

        // Set experience & reset player's XP
        int experience = Experience.calculatePlayerExperience(player);
        graveEntity.setXp(experience);
        resetPlayerExperience(player);

        // Set grave spawn time
        graveEntity.setMstime((new Date()).getTime());

        // Spawn break particles
        block.onBreak(world, pos, state, player);

        // Add the block entity to the world
        world.addBlockEntity(graveEntity);

        // Store the grave data in persistent server state (used for restore command)
        ServerState.storePlayerGrave(player, graveEntity);

        // Alert user if graveCoordinates is enabled
        boolean graveCoordinates = GravesConfig.resolve("graveCoordinates", player.getGameProfile());

        if (graveCoordinates) {
            Responder res = new Responder(player, player.getServer());
            String dimension = String.valueOf(world.getDimensionKey().getValue());

            res.sendInfo(
                Text.translatable("event.death:send-player-coordinates",
                    res.dimension(pos.getX(), dimension),
                    res.dimension(pos.getY(), dimension),
                    res.dimension(pos.getZ(), dimension)
                ),
                null
            );
        }

        // For the logs :)
        System.out.printf("[%s] Grave spawned at %dx %dy %dz for player %s in %s.\n", Graves.MOD_ID, pos.getX(),
            pos.getY(), pos.getZ(), player.getName().getString(), world.getDimensionKey().getValue());
    }

    /**
     * Resets the player's experience levels and progress to zero.
     */
    private static void resetPlayerExperience(PlayerEntity player) {
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.experienceLevel = 0;
    }

    /**
     * Determines whether the grave can spawn in the given position.
     */
    private static boolean canPlaceGrave(World world, Dimension dimension, BlockPos pos) {
        // Do not replace existing block entities
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) return false;

        // Do not replace irreplaceable blocks
        BlockState state = world.getBlockState(pos);
        if (VersionedCode.Tags.blockTagContains(state, GraveBlockTags.DO_NOT_REPLACE)) return false;

        // Ensure pos is within boundaries
        return dimension.inBounds(pos);
    }

    /**
     * Determines whether a grave should sink through a given block; based on configuration.
     */
    public static boolean graveShouldSink(World world, BlockPos pos, PlayerEntity player) {
        pos = pos.down(); // Used to check the block below the potential grave spot
        GameProfile profile = player.getGameProfile();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Sink if the block is found in the SINK_THROUGH tag.
        boolean sinkThroughBlocks = GravesConfig.getConfig().sink.sinkThroughBlocks;
        if (sinkThroughBlocks && VersionedCode.Tags.blockTagContains(state, GraveBlockTags.SINK_THROUGH)) {
            return true;
        }

        // Stop sinking if the position is neither a liquid, air, or replaceable.
        if (!isLiquidAirOrReplaceable(world, pos)) return false;

        return switch (block.getName().getString()) {
            case "Air" -> (boolean) GravesConfig.resolve("sinkInAir", profile);
            case "Water" -> (boolean) GravesConfig.resolve("sinkInWater", profile);
            case "Lava" -> (boolean) GravesConfig.resolve("sinkInLava", profile);
            default -> false;
        };
    }

    /**
     *  Checks to see if the block is a liquid, air, or replaceable (tag).
     */
    private static boolean isLiquidAirOrReplaceable(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean replaceBlocks = GravesConfig.getConfig().sink.replaceBlocks;
        boolean canReplace = VersionedCode.Tags.blockTagContains(state, GraveBlockTags.REPLACEABLE);
        boolean doNotReplace = VersionedCode.Tags.blockTagContains(state, GraveBlockTags.DO_NOT_REPLACE);
        return state.isAir() || state.isLiquid() || replaceBlocks && !doNotReplace && canReplace;
    }
}