package me.mgin.graves.block.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.mgin.graves.Graves;
import me.mgin.graves.api.GravesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GraveDropType;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RetrieveGrave {
  static public boolean retrieve(PlayerEntity player, World world, BlockPos pos) {
    // Edge case checking & variable initialization
    if (world.isClient)
      return false;

    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (!(blockEntity instanceof GraveBlockEntity graveEntity))
      return false;

    graveEntity.sync(world, pos);
    
    if (graveEntity.getInventory("Items") == null) return false;
    if (graveEntity.getGraveOwner() == null) return false;

    if (!Permission.playerCanAttemptRetrieve(player, graveEntity))
      if (!Permission.playerCanOverride(player))
        return false;

    // Get inventories (grave & player)
    DefaultedList<ItemStack> items = graveEntity.getInventory("Items");
    DefaultedList<ItemStack> inventory = Inventory.getMainInventory(player);

    // Add any other inventories to inventory
    for (GravesApi mod : Graves.apiMods) {
      inventory.addAll(mod.getInventory(player));
    }

    // Resolve drop type
    GraveDropType dropType = GravesConfig.resolveConfig("dropType", player.getGameProfile()).main.dropType;

    if (dropType == GraveDropType.PUT_IN_INVENTORY) {
      // Clear player's current inventory
      player.getInventory().clear();

      DefaultedList<ItemStack> extraItems = DefaultedList.of();
      
      // Equip armor slots that do not have curse of binding
      List<ItemStack> armor = items.subList(36, 40);

      for (int i = 0; i < armor.size(); i++) {
        if (EnchantmentHelper.hasBindingCurse(armor.get(i))) {
          extraItems.add(armor.get(i));
          continue;
        }

        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor.get(i));
        player.equipStack(equipmentSlot, armor.get(i));
      }

      if (!EnchantmentHelper.hasBindingCurse(items.get(40)))
        player.equipStack(EquipmentSlot.OFFHAND, items.get(40));

      // Restore grave inventory
      List<ItemStack> mainInventory = items.subList(0, 36);

      for (int i = 0; i < mainInventory.size(); i++) {
        player.getInventory().setStack(i, mainInventory.get(i));
      }
     
      // Equip third party inventories
      for (GravesApi mod : Graves.apiMods) {
        DefaultedList<ItemStack> modInventory = graveEntity.getInventory(mod.getModID());
        int size = mod.getInventorySize(player);

        if (size == modInventory.size()) {
          DefaultedList<ItemStack> unequippedItems = mod.setInventory(modInventory, player);
          extraItems.addAll(unequippedItems);
        } else {
          extraItems.addAll(modInventory);
        }
      }

      // Preserve previous inventory
      extraItems.addAll(inventory.subList(0, 36));

      if (inventory.size() > 41) {
        extraItems.addAll(inventory.subList(41, inventory.size()));
      }

      // Remove any empty or air slots from extraItems
      extraItems.removeIf(item -> 
        item == ItemStack.EMPTY || item.getItem() == Items.AIR
      );

      // Move extra items to open slots
      DefaultedList<Integer> openSlots = Inventory.getInventoryOpenSlots(player.getInventory().main);

      for (int i = 0; i < openSlots.size(); i++) {
        if (extraItems.size() > 0) {
          player.getInventory().setStack(openSlots.get(i), extraItems.get(0));
          extraItems.remove(0);
        }
      }

      // Drop any excess items
      DefaultedList<ItemStack> dropItems = DefaultedList.of();
      dropItems.addAll(extraItems);
      ItemScatterer.spawn(world, pos, dropItems);
    } else if (dropType == GraveDropType.DROP_ITEMS) {
      // Drop all items
      ItemScatterer.spawn(world, pos, items);
    }

    player.addExperience((int) (1 * graveEntity.getXp()));

    // spawnBreakParticles(world, player, pos, defaultState);

    world.removeBlock(pos, false);
    return true;
  }
}
