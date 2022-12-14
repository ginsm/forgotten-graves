package me.mgin.graves.events;

import me.mgin.graves.block.api.Permission;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerBlockBreakHandler {
	public static boolean handleBeforeEvent(PlayerEntity player, BlockPos pos, BlockEntity entity) {
		if (entity instanceof GraveBlockEntity graveEntity) {
			if (!Permission.playerCanBreakGrave(player, graveEntity))
				return false;
		}
		return true;
	}
}
