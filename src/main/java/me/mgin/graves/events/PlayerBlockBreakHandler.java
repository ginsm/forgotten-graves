package me.mgin.graves.events;

import me.mgin.graves.block.api.Permission;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerBlockBreakHandler {
	public static boolean handleBeforeEvent(PlayerEntity player, BlockEntity entity) {
		if (entity instanceof GraveBlockEntity graveEntity) {
			return Permission.playerCanBreakGrave(player, graveEntity);
		}
		return true;
	}
}
