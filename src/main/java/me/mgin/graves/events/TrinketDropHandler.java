package me.mgin.graves.events;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TrinketDropHandler {
  public static DropRule handleTrinketDrop(DropRule rule, ItemStack stack, SlotReference ref, LivingEntity entity) {
    // Prevent Trinkets from handling dropInventory
    return DropRule.KEEP;
  }
}
