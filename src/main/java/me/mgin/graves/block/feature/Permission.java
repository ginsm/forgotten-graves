package me.mgin.graves.block.feature;

import me.mgin.graves.block.GraveBlockEntity;
import me.mgin.graves.config.enums.GraveRetrievalType;
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
        boolean graveRobbing = GravesConfig.getConfig().server.graveRobbing;

        return graveEntity.getGraveOwner() == null || graveEntity.isGraveOwner(player) || graveRobbing
                || playerCanOverride(player);
    }

    /**
     * Determines whether operator override is enabled, and if the player meets the
     * necessary override level requirements.
     *
     * @param player
     * @return boolean
     */
    static public boolean playerCanOverride(PlayerEntity player) {
        int OPOverrideLevel = GravesConfig.getConfig().server.OPOverrideLevel;
        return (OPOverrideLevel != -1 && player.hasPermissionLevel(OPOverrideLevel));
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
            return retrievalType == GraveRetrievalType.BREAK || retrievalType == GraveRetrievalType.BOTH;

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
            return retrievalType == GraveRetrievalType.USE || retrievalType == GraveRetrievalType.BOTH;

        return false;
    }
}
