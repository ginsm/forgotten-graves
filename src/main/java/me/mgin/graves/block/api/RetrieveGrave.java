package me.mgin.graves.block.api;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RetrieveGrave {
  static public boolean retrieve(PlayerEntity player, World world, BlockPos pos) {
    if (world.isClient)
      return false;

    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (!(blockEntity instanceof GraveBlockEntity))
      return false;

    GraveBlockEntity graveEntity = (GraveBlockEntity) blockEntity;
    graveEntity.sync(world, pos);

    if (graveEntity.getItems() == null)
      return false;
    if (graveEntity.getGraveOwner() == null)
      return false;

    if (!Permission.playerCanAttemptRetrieve(player, graveEntity))
      if (!Permission.playerCanOverride(player))
        return false;

    DefaultedList<ItemStack> items = graveEntity.getItems();
    DefaultedList<ItemStack> inventory = DefaultedList.of();

    inventory.addAll(player.getInventory().main);
    inventory.addAll(player.getInventory().armor);
    inventory.addAll(player.getInventory().offHand);

    for (GravesApi gravesApi : Graves.apiMods) {
      inventory.addAll(gravesApi.getInventory(player));
    }

    // Retrieve the appropriate config
    GraveDropType dropType = GravesConfig.resolveConfig("dropType", player.getGameProfile()).main.dropType;

    if (dropType == GraveDropType.PUT_IN_INVENTORY) {
      player.getInventory().clear();

      List<ItemStack> armor = items.subList(36, 40);
      DefaultedList<ItemStack> extraItems = DefaultedList.of();

      // Equip items that do not have curse of binding
      for (int i = 0; i < armor.size(); i++) {
        if (EnchantmentHelper.hasBindingCurse(armor.get(i))) {
          extraItems.add(armor.get(i));
          continue;
        }
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor.get(i));
        player.equipStack(equipmentSlot, armor.get(i));
      }

      player.equipStack(EquipmentSlot.OFFHAND, items.get(40));

      List<ItemStack> mainInventory = items.subList(0, 36);

      for (int i = 0; i < mainInventory.size(); i++) {
        player.getInventory().setStack(i, mainInventory.get(i));
      }

      List<Integer> openArmorSlots = getInventoryOpenSlots(player.getInventory().armor);

      for (int i = 0; i < 4; i++) {
        if (openArmorSlots.contains(i)) {
          player.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i),
              inventory.subList(36, 40).get(i));
        } else
          extraItems.add(inventory.subList(36, 40).get(i));
      }

      if (player.getInventory().offHand.get(0) == ItemStack.EMPTY) {
        player.equipStack(EquipmentSlot.OFFHAND, inventory.get(40));
      } else {
        extraItems.add(inventory.get(40));
      }

      extraItems.addAll(inventory.subList(0, 36));

      if (inventory.size() > 41)
        extraItems.addAll(inventory.subList(41, inventory.size()));

      List<Integer> openSlots = getInventoryOpenSlots(player.getInventory().main);

      int inventoryOffset = 41;

      // Equip third party inventories
      for (GravesApi GravesApi : Graves.apiMods) {
          int newOffset = inventoryOffset + GravesApi.getInventorySize(player);
          if (newOffset > items.size()) newOffset = items.size();

          // Add any unequipped items to extraItems
          extraItems.addAll(GravesApi.setInventory(items.subList(inventoryOffset, newOffset), player));
          inventoryOffset = newOffset;
      }

      for (int i = 0; i < openSlots.size(); i++) {
        player.getInventory().setStack(openSlots.get(i), extraItems.get(i));
      }

      DefaultedList<ItemStack> dropItems = DefaultedList.of();

      dropItems.addAll(extraItems.subList(openSlots.size(), extraItems.size()));

      ItemScatterer.spawn(world, pos, dropItems);
    } else if (dropType == GraveDropType.DROP_ITEMS) {
      ItemScatterer.spawn(world, pos, graveEntity.getItems());
    }

    player.addExperience((int) (1 * graveEntity.getXp()));

    // spawnBreakParticles(world, player, pos, defaultState);

    world.removeBlock(pos, false);
    return true;
  }

  static private List<Integer> getInventoryOpenSlots(DefaultedList<ItemStack> inventory) {
    List<Integer> openSlots = new ArrayList<>();
    for (int i = 0; i < inventory.size(); i++) {
      if (inventory.get(i) == ItemStack.EMPTY)
        openSlots.add(i);
    }
    return openSlots;
  }
}
