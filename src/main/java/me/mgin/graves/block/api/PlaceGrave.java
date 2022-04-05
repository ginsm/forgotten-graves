package me.mgin.graves.block.api;

import java.util.HashSet;
import java.util.Set;

import me.mgin.graves.Graves;
import me.mgin.graves.api.GravesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.registry.GraveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;


public class PlaceGrave {
  static public void place(World world, Vec3d vecPos, PlayerEntity player) {
    if (world.isClient)
      return;

    BlockPos pos = new BlockPos(vecPos.x, vecPos.y - 1, vecPos.z);

    // Handle dying below the dimension's minimum Y height
    int minY = world.getDimension().getMinimumY();
    if (minY > pos.getY()) {
      pos = new BlockPos(pos.getX(), minY + 5, pos.getZ());
    }

    // Handle dying at or above the dimension's maximum Y height
    int maxY = world.getTopY() - 1;
    if (pos.getY() >= maxY) {
      pos = new BlockPos(pos.getX(), maxY - 1, pos.getZ());
    }

    for (BlockPos gravePos : BlockPos.iterateOutwards(pos.add(new Vec3i(0, 1, 0)), 5, 5, 5)) {
      BlockState state = world.getBlockState(gravePos);
      Block block = state.getBlock();

      if (canPlaceGrave(world, block, gravePos)) {
        world.setBlockState(gravePos, GraveBlocks.GRAVE.getDefaultState().with(Properties.HORIZONTAL_FACING,
            player.getHorizontalFacing()));

        GraveBlockEntity graveEntity = new GraveBlockEntity(gravePos, world.getBlockState(gravePos));

        // Set inventories
        graveEntity.setInventory("Items", Inventory.getMainInventory(player));

        for (GravesApi mod : Graves.apiMods) {
          graveEntity.setInventory(mod.getModID(), mod.getInventory(player));
        }

        // Set owner
        graveEntity.setGraveOwner(player.getGameProfile());

        // Set experience & reset player's XP
        int experience = Experience.calculatePlayerExperience(player);
        graveEntity.setXp(experience);
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.experienceLevel = 0;

        // Add the block entity
        world.addBlockEntity(graveEntity);

        // Sync with the server
        if (world.isClient())
          graveEntity.sync(world, gravePos);

        block.onBreak(world, pos, state, player);

        GravesConfig config = GravesConfig.resolveConfig("sendGraveCoordinates", player.getGameProfile());

        if (config.main.sendGraveCoordinates) {
          player.sendMessage(new TranslatableText(
            "text.forgottengraves.mark_coords",
            gravePos.getX(),
            gravePos.getY(),
            gravePos.getZ()
          ), false);
        }

        System.out.println("[Graves] Grave spawned at: "
          + gravePos.getX() + ", "
          + gravePos.getY() + ", "
          + gravePos.getZ() + " for player "
          + player.getName().asString() + "."
        );

        break;
      }
    }
  }

  private static boolean canPlaceGrave(World world, Block block, BlockPos pos) {
    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (blockEntity != null)
      return false;

    Set<Block> blackListedBlocks = new HashSet<Block>() {
      {
        add(Blocks.BEDROCK);
      }
    };

    if (blackListedBlocks.contains(block))
      return false;

    DimensionType dimension = world.getDimension();
    return !(pos.getY() < dimension.getMinimumY() || pos.getY() > world.getTopY());
  }
}
