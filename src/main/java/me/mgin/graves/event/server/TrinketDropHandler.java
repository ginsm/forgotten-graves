package me.mgin.graves.event.server;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TrinketDropHandler {
    /**
     * TrinketDropCallback handler; this method prevents Trinkets from handling item
     * drops unless graves is set to false.
     *
     * @param ref SlotReference
     * @param entity LivingEntity
     * @return DropRule
     */
    public static DropRule handleTrinketDrop(SlotReference ref, LivingEntity entity) {
        // The rule passed by the event handler is always DropRule.DEFAULT for some reason. This should accurately
        // get the rule.
        DropRule rule = ref.inventory().getSlotType().getDropRule();

        if (entity instanceof PlayerEntity player) {
            // Check if graves is enabled -- if not, it'll skip to vanilla behavior.
            boolean gravesEnabled = GravesConfig.resolveConfig("graves", player.getGameProfile()).main.graves;

            // This causes Forgotten Graves to only handle two drop rules: DEFAULT and KEEP. Slots with a DropRule of
            // KEEP are kept on the player still. And slots with DEFAULT are handled like vanilla items.
            if (gravesEnabled && rule.equals(DropRule.DEFAULT))
                return DropRule.KEEP;
        }

        return rule;
    }
}
