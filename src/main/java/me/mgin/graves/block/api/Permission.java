package me.mgin.graves.block.api;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GraveRetrievalType;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.player.PlayerEntity;

public class Permission {
	/**
	 * Determines whether any of the following is true:
	 *
	 * <p>
	 * The grave has no owner, the player is the owner, grave robbing is enabled, or
	 * the player's operator permission level meets the requirements.
	 * </p>
	 *
	 * @param player
	 * @return boolean
	 */
	static public boolean playerCanAttemptRetrieve(PlayerEntity player, GraveBlockEntity graveEntity) {
		boolean graveRobbing = GravesConfig.getConfig().server.enableGraveRobbing;

		if (graveEntity.getGraveOwner() == null || graveEntity.isGraveOwner(player) || graveRobbing
				|| playerCanOverride(player)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines whether operator override is enabled, and if the player meets the
	 * necessary override level requirements.
	 *
	 * @param player
	 * @return boolean
	 */
	static public boolean playerCanOverride(PlayerEntity player) {
		int operatorOverrideLevel = GravesConfig.getConfig().server.minOperatorOverrideLevel;
		return (operatorOverrideLevel != -1 && player.hasPermissionLevel(operatorOverrideLevel));
	}

	/**
	 * Determines whether the player can break the block with left click
	 * (RetrievalType.ON_BREAK || RetrievalType.ON_BOTH).
	 *
	 * <p>
	 * In addition, it checks whether the player is the owner, if grave robbing is
	 * enabled, or if the player can override the protection with the proper
	 * operator level.
	 * <p>
	 *
	 * @param player
	 * @return boolean
	 */
	static public boolean playerCanBreakGrave(PlayerEntity player, GraveBlockEntity graveEntity) {
		GraveRetrievalType retrievalType = GravesConfig.resolveConfig("retrievalType",
				player.getGameProfile()).main.retrievalType;

		if (playerCanAttemptRetrieve(player, graveEntity))
			if (retrievalType == GraveRetrievalType.ON_BREAK || retrievalType == GraveRetrievalType.ON_BOTH)
				return true;

		return false;
	}

	/**
	 * Determines whether the player can use the block with right click
	 * (RetrievalType.ON_USE || RetrievalType.ON_BOTH).
	 *
	 * <p>
	 * In addition, it checks whether the player is the owner, if grave robbing is
	 * enabled, or if the player can override the protection with the proper
	 * operator level.
	 * </p>
	 *
	 * @param player
	 * @return
	 */
	static public boolean playerCanUseGrave(PlayerEntity player, GraveBlockEntity graveEntity) {
		GraveRetrievalType retrievalType = GravesConfig.resolveConfig("retrievalType",
				player.getGameProfile()).main.retrievalType;

		if (playerCanAttemptRetrieve(player, graveEntity))
			if (retrievalType == GraveRetrievalType.ON_USE || retrievalType == GraveRetrievalType.ON_BOTH)
				return true;

		return false;
	}
}
