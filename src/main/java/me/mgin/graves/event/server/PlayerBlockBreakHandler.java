package me.mgin.graves.event.server;

import me.mgin.graves.block.utility.Permission;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerBlockBreakHandler {
    /**
     * This event handler prevents players who do not own a particular grave
     * from breaking it unless they have the proper permissions to do so.
     *
     * @param player PlayerEntity
     * @param entity BlockEntity
     * @return boolean
     */
    public static boolean handleBeforeEvent(PlayerEntity player, BlockEntity entity) {
        if (entity instanceof GraveBlockEntity graveEntity) {
            return Permission.playerCanBreakGrave(player, graveEntity);
        }
        return true;
    }
}
