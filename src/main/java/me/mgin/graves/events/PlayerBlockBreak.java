package me.mgin.graves.events;

import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerBlockBreak {
  public static boolean handleEvent(PlayerEntity player, BlockPos pos, BlockEntity entity) {
    if (entity instanceof GraveBlockEntity graveBlockEntity) {
      if (!graveBlockEntity.playerCanBreak(player))
        return false;
    }

    return true;
  }
}

