package me.mgin.graves.event.server;

import dev.emi.trinkets.api.TrinketEnums.DropRule;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TrinketDropHandler {
    /**
     * TrinketDropCallback handler; this method prevents Trinkets from handling item
     * drops unless graves is set to false.
     *
     * @param rule DropRule
     * @param entity LivingEntity
     * @return DropRule
     */
    public static DropRule handleTrinketDrop(DropRule rule, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            // Prevent Trinkets from handling a player's dropInventory
            if (GravesConfig.resolveConfig("graves", player.getGameProfile()).main.graves) {
                return DropRule.KEEP;
            }
        }
        return rule;
    }
}
