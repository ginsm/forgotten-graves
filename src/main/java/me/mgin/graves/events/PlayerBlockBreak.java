package me.mgin.graves.events;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GraveRetrievalType;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerBlockBreak {
  public static boolean handleEvent(PlayerEntity player, BlockPos pos, BlockEntity entity) {
    if (entity instanceof GraveBlockEntity graveBlockEntity && graveBlockEntity.getGraveOwner() != null) {
      GraveRetrievalType retrievalType = GravesConfig.getConfig().mainSettings.retrievalType;

      // Max: 4, Min: -1
      int operatorOverrideLevel = Math.max(
        Math.min(GravesConfig.getConfig().mainSettings.operatorOverrideLevel, 4), 
        -1); 
      
      boolean graveRobbingEnabled = GravesConfig.getConfig().mainSettings.enableGraveRobbing;

      if (retrievalType != GraveRetrievalType.ON_BREAK && retrievalType != GraveRetrievalType.ON_BOTH)
        return false;

      if ((operatorOverrideLevel != -1 && player.hasPermissionLevel(operatorOverrideLevel)) || graveRobbingEnabled) {
        System.out.println("[Graves] Operator overrided grave protection at: " + pos);
        return true;
      }

      if (!graveBlockEntity.getGraveOwner().getId().equals(player.getGameProfile().getId()))
        return false;
    }

    return true;
  }
}

