package me.mgin.graves.block.utility;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.ServerState;
import me.mgin.graves.util.Responder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PlaceGrave {
    private static int minY;

    /**
     * Attempts to spawn a grave at the given position; if the position is invalid or a sinkable block it will look
     * for a more suitable position
     *
     * @param world  world
     * @param vecPos Vec3d
     * @param player PlayerEntity
     */
    public static void place(World world, Vec3d vecPos, PlayerEntity player) {
        if (world.isClient()) return;

        BlockPos pos = enforceWorldBoundaries(world, new BlockPos(vecPos.x, vecPos.y, vecPos.z));
        Block block = world.getBlockState(pos).getBlock();

        // This is the position below the grave; used for sinking purposes
        BlockPos belowPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

        // Sink through functionality
        if (graveShouldSink(world, belowPos, player)) {
            pos = sinkDownwards(world, belowPos, pos.getY() - (minY + 7), player);
        }

        // Try and find a new valid position
        if (!canPlaceGrave(world, block, new BlockPos(vecPos.x, vecPos.y, vecPos.z))) {
            pos = searchOutwards(world, pos, player);
        }

        // Place the grave
        spawnGrave(world, pos, player);
    }

    /**
     * Ensures that graves only spawn inside the world boundaries.
     *
     * @param world World
     * @param pos   BlockPos
     * @return BlockPos
     */
    private static BlockPos enforceWorldBoundaries(World world, BlockPos pos) {
        int maxY = world.getTopY() - 1;
        minY = world.getDimension().minY();

        // Handle dying at or above the dimension's maximum Y height
        if (pos.getY() >= maxY) {
            pos = new BlockPos(pos.getX(), maxY - 1, pos.getZ());
        }

        // Handle dying below the dimension's minimum Y height
        if (minY > pos.getY()) {
            pos = new BlockPos(pos.getX(), minY + 7, pos.getZ());
        }

        return pos;
    }

    /**
     * Determines whether a grave should sink through a given block; based on configuration.
     *
     * @param world  World
     * @param pos    BlockPos
     * @param player PlayerEntity
     * @return boolean
     */
    public static boolean graveShouldSink(World world, BlockPos pos, PlayerEntity player) {
        GameProfile profile = player.getGameProfile();
        Block block = world.getBlockState(pos).getBlock();

        return switch (block.getName().getString()) {
            case "Air" -> (boolean) GravesConfig.resolve("sinkInAir", profile);
            case "Water" -> (boolean) GravesConfig.resolve("sinkInWater", profile);
            case "Lava" -> (boolean) GravesConfig.resolve("sinkInLava", profile);
            default -> false;
        };
    }

    /**
     * Iterates downwards until it finds a non-sinkable position to spawn the grave or reaches the minimum Y level.
     *
     * @param world  World
     * @param pos    BlockPos
     * @param depth  int
     * @param player PlayerEntity
     * @return BlockPos
     */
    private static BlockPos sinkDownwards(World world, BlockPos pos, int depth, PlayerEntity player) {
        for (BlockPos newPos : BlockPos.iterateOutwards(pos.add(new Vec3i(0, -1, 0)), 0, depth, 0)) {
            // Keep sinking until a suitable block is found
            if (graveShouldSink(world, newPos, player)) continue;

            // Move the position up one (where the grave will actually spawn)
            return new BlockPos(newPos.getX(), newPos.getY() + 1, newPos.getZ());
        }

        return pos;
    }

    /**
     * Determines whether the grave can spawn in the given position.
     *
     * @param world World
     * @param block Block
     * @param pos   BlockPos
     * @return boolean
     */
    private static boolean canPlaceGrave(World world, Block block, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        Set<Block> blackListedBlocks = new HashSet<>() {{
            add(Blocks.BEDROCK);
        }};

        if (blockEntity != null) return false;
        if (blackListedBlocks.contains(block)) return false;

        DimensionType dimension = world.getDimension();
        return !(pos.getY() < dimension.minY() || pos.getY() > world.getTopY());
    }

    /**
     * Iterates outwards until it finds a valid position to spawn the grave; ensuring the grave sinks when necessary.
     *
     * @param world  World
     * @param pos    BlockPos
     * @param player PlayerEntity
     * @return BlockPos
     */
    private static BlockPos searchOutwards(World world, BlockPos pos, PlayerEntity player) {
        for (BlockPos newPos : BlockPos.iterateOutwards(pos, 10, 10, 10)) {
            Block block = world.getBlockState(newPos).getBlock();

            if (canPlaceGrave(world, block, newPos)) {
                BlockPos belowPos = new BlockPos(newPos.getX(), newPos.getY() - 1, newPos.getZ());

                if (graveShouldSink(world, belowPos, player)) {
                    pos = sinkDownwards(world, belowPos, pos.getY() - (minY + 7), player);
                    break;
                }

                pos = newPos;
                break;
            }
        }

        return pos;
    }

    /**
     * Spawns a grave at the given BlockPos.
     *
     * @param world  World
     * @param pos    BlockPos
     * @param player PlayerEntity
     */
    public static void spawnGrave(World world, BlockPos pos, PlayerEntity player) {
        // Get block and state of location
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        world.setBlockState(pos, GraveBlocks.GRAVE.getDefaultState().with(Properties.HORIZONTAL_FACING,
            player.getHorizontalFacing()));

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
        System.out.printf("[%s] Grave spawned at: %dx %dy %dz for player %s.\n", Graves.MOD_ID, pos.getX(), pos.getY(),
            pos.getZ(), player.getName().getString());
    }

    /**
     * Resets the player's experience levels and progress to zero.
     *
     * @param player PlayerEntity
     */
    private static void resetPlayerExperience(PlayerEntity player) {
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.experienceLevel = 0;
    }
}
