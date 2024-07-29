package me.mgin.graves.block.utility;

import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
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
     * @param player PlayerEntity
     * @param graveEntity GraveBlockEntity
     * @return boolean
     */
    static public boolean playerCanAttemptRetrieve(PlayerEntity player, GraveBlockEntity graveEntity) {
        if (graveEntity.getGraveOwner() == null) return true;

        // Config settings
        GravesConfig config = GravesConfig.getConfig();
        int decayRobbing = GravesConfig.getConfig().decay.decayRobbing.ordinal();

        // Get the grave's current decay stage
        int graveStageOrdinal = ((GraveBlockBase) graveEntity.getState().getBlock()).getDecayStage().ordinal();

        // Conditionals
        boolean isGraveOwner = graveEntity.isGraveOwner(player);
        boolean canDecayRob = graveStageOrdinal >= decayRobbing;
        boolean graveRobbing = config.server.graveRobbing;
        boolean canOverride = playerCanOverride(player);

        return isGraveOwner || canDecayRob && graveRobbing || canOverride;
    }

    /**
     * Determines whether operator override is enabled, and if the player meets the
     * necessary override level requirements.
     *
     * @param player PlayerEntity
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
     * @param player PlayerEntity
     * @param graveEntity GraveBlockEntity
     * @return boolean
     */
    static public boolean playerCanBreakGrave(PlayerEntity player, GraveBlockEntity graveEntity) {
        // Players can always break unowned graves
        if (graveEntity.getGraveOwner() == null) return true;

        GraveRetrievalType retrievalType = GravesConfig.resolve("retrievalType", player.getGameProfile());

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
     * @param player PlayerEntity
     * @param graveEntity GraveBlockEntity
     * @return boolean
     */
    static public boolean playerCanUseGrave(PlayerEntity player, GraveBlockEntity graveEntity) {
        GraveRetrievalType retrievalType = GravesConfig.resolve("retrievalType", player.getGameProfile());

        if (playerCanAttemptRetrieve(player, graveEntity))
            return retrievalType == GraveRetrievalType.USE || retrievalType == GraveRetrievalType.BOTH;

        return false;
    }
}
