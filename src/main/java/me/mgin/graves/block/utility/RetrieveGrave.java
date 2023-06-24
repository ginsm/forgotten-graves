package me.mgin.graves.block.utility;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class RetrieveGrave {
    /**
     * Retrieve the grave by interacting with it; this takes permissions into consideration.
     *
     * @param player Player
     * @param world World
     * @param pos BlockPos
     * @return boolean
     */
    static public boolean retrieveWithInteract(PlayerEntity player, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Edge case checking & variable initialization
        if (!(blockEntity instanceof GraveBlockEntity graveEntity)) return false;
        if (world.isClient) return false;
        if (graveEntity.getInventory("Items") == null) return false;
        if (graveEntity.getGraveOwner() == null) return false;

        // Ensure the player has proper permission to retrieve grave
        if (!Permission.playerCanAttemptRetrieve(player, graveEntity))
            if (!Permission.playerCanOverride(player))
                return false;

        return retrieve(player, graveEntity, world, pos, false, true);
    }

    /**
     * Retrieve the grave via a command; the given player should be the player who will be receiving the grave
     * contents. Whether the grave exists in the world or not doesn't matter; But if it does exist, it will be
     * removed during issuing this command. And lastly, any items that need to be dropped will be dropped on
     * top of the player.
     *
     * @param player         PlayerEntity
     * @param graveEntityTag NbtCompound
     */
    static public void retrieveWithCommand(PlayerEntity player, NbtCompound graveEntityTag) {
        // Get the coordinates from the grave entity tag
        BlockPos pos = new BlockPos(
            graveEntityTag.getInt("x"),
            graveEntityTag.getInt("y"),
            graveEntityTag.getInt("z")
        );

        // Iterate over dimensions to locate dimension where the grave can be found
        for (ServerWorld world : Objects.requireNonNull(player.getServer()).getWorlds()) {
            String dimensionKey = String.valueOf(world.getDimensionKey().getValue());
            String storedDimensionKey = graveEntityTag.getString("dimension");
            GraveBlockEntity graveEntity = null;
            boolean destroyGrave = true;

            // Keep iterating if in the wrong dimension
            if (!dimensionKey.equals(storedDimensionKey)) continue;

            // Attempt to locate the requested grave in the dimension
            if (world.getBlockEntity(pos) instanceof GraveBlockEntity graveBlockEntity) {
                // Ensure the grave creation times are the same
                if (graveBlockEntity.getMstime() == graveEntityTag.getLong("mstime")) {
                    graveEntity = graveBlockEntity;
                }
            }

            // Create a new grave entity if it did not exist in the world
            if (graveEntity == null ) {
                // Create new grave block entity and read the nbt tag into it
                graveEntity = new GraveBlockEntity(pos, GraveBlocks.GRAVE.getDefaultState());
                graveEntity.readNbt(graveEntityTag);
                // Do not delete any grave in that location
                destroyGrave = false;
            }

            retrieve(player, graveEntity, world, pos, true, destroyGrave);
            return;
        }

    }

    /**
     * Retrieves the grave, respecting the user's dropType setting.
     *
     * @param player Player
     * @param graveEntity GraveBlockEntity
     * @param world World
     * @param pos BlockPos
     * @return boolean
     */
    static public boolean retrieve(PlayerEntity player, GraveBlockEntity graveEntity, World world, BlockPos pos,
                                   boolean dropOnPlayer, boolean destroyGrave) {
        // Keeps track of items to be dropped
        DefaultedList<ItemStack> droppedItems = DefaultedList.of();

        // Resolve and handle the drop types
        GameProfile profile = player.getGameProfile();
        GraveDropType dropType = GravesConfig.resolve("dropType", profile);
        boolean shiftSwapsDropType = GravesConfig.resolve("sneakSwapsDropType", profile);

        // Swap drop type if holding shift
        if (shiftSwapsDropType && player.isSneaking()) {
            dropType = dropType == GraveDropType.DROP ? GraveDropType.EQUIP : GraveDropType.DROP;
        }

        if (dropType == GraveDropType.EQUIP) {
            droppedItems = equipInventoryItems(player, graveEntity);
        } else if (dropType == GraveDropType.DROP) {
            droppedItems = getInventoryItems(graveEntity);
        }

        // Drop items on the ground; either on the player or where the grave is located.
        ItemScatterer.spawn(
            dropOnPlayer ? player.getWorld() : world,
            dropOnPlayer ? player.getBlockPos() : pos,
            droppedItems
        );

        // Add player experience back
        player.addExperience(graveEntity.getXp());

        // Remove block if it exists
        if (world.getBlockEntity(pos) instanceof GraveBlockEntity && destroyGrave) {
            world.removeBlock(pos, false);
        }

        // Mark as retrieved in global state
        GameProfile owner = graveEntity.getGraveOwner(); // Needed as the owner might not be the one retrieving
        PlayerState playerState = ServerState.getPlayerState(player.getServer(), owner.getId());

        for (int i = 0; i < playerState.graves.size(); i++) {
            NbtCompound grave = (NbtCompound) playerState.graves.get(i);

            assert player.getServer() != null;
            if (grave.getLong("mstime") == graveEntity.getMstime()) {
                // Set retrieved as true
                grave.putBoolean("retrieved", true);
                playerState.graves.set(i, grave);

                // Mark server state dirty
                ServerState.getServerState(player.getServer()).markDirty();
            }
        }

        return true;
    }

    /**
     * Handles the "DROP" drop type by looping through the different inventories and creating
     * a list of every item from the different inventories.
     *
     * @param graveEntity GraveBlockEntity
     * @return {@code DefaultedList<ItemStack>}
     */
    static public DefaultedList<ItemStack> getInventoryItems(GraveBlockEntity graveEntity) {
        // Keeps track of items to be dropped
        DefaultedList<ItemStack> droppedItems = DefaultedList.of();

        // Add loaded inventories to droppedItems list
        for (InventoriesApi api : Graves.inventories) {
            DefaultedList<ItemStack> modInventory = graveEntity.getInventory(api.getID());

            if (modInventory != null)
                droppedItems.addAll(modInventory);
        }

        // Add any unloaded inventories to droppedItems list
        for (String modID : Graves.unloadedInventories) {
            DefaultedList<ItemStack> modInventory = graveEntity.getInventory(modID);

            if (modInventory != null)
                droppedItems.addAll(modInventory);
        }

        return droppedItems;
    }

    /**
     * Handles the "INVENTORY" drop type by looping through the different inventories and attempting
     * to equip them; returning a list of any items that could not be equipped.
     *
     * @param player PlayerEntity
     * @param graveEntity GraveBlockEntity
     * @return {@code DefaultedList<ItemStack>}
     */
    static public DefaultedList<ItemStack> equipInventoryItems(PlayerEntity player, GraveBlockEntity graveEntity) {
        // Keeps track of items to be dropped
        DefaultedList<ItemStack> droppedItems = DefaultedList.of();

        // Store old inventories as one big inventory
        DefaultedList<ItemStack> oldInventory = DefaultedList.of();

        for (InventoriesApi api : Graves.inventories) {
            DefaultedList<ItemStack> inventory = api.getInventory(player);

            // Skip empty inventories
            if (inventory == null) continue;

            oldInventory.addAll(inventory);
            api.clearInventory(player);
        }

        // Equip inventories
        for (InventoriesApi api : Graves.inventories) {
            DefaultedList<ItemStack> inventory = graveEntity.getInventory(api.getID());

            if (inventory == null)
                continue;

            if (api.getInventorySize(player) == inventory.size()) {
                DefaultedList<ItemStack> unequippedItems = api.setInventory(inventory, player);
                droppedItems.addAll(unequippedItems);
            } else {
                droppedItems.addAll(inventory);
            }
        }

        // Check for any potentially unloaded inventories; store them if found
        for (String modID : Graves.unloadedInventories) {
            DefaultedList<ItemStack> inventory = graveEntity.getInventory(modID);
            if (inventory != null)
                droppedItems.addAll(inventory);
        }

        // Preserve previous inventory
        droppedItems.addAll(oldInventory);

        // Remove any empty or air slots from extraItems
        droppedItems.removeIf(item -> item == ItemStack.EMPTY || item.getItem() == Items.AIR);

        // Move extra items to open slots
        DefaultedList<Integer> openSlots = Inventory.getInventoryOpenSlots(player.getInventory().main);

        for (Integer openSlot : openSlots) {
            if (droppedItems.size() > 0) {
                player.getInventory().setStack(openSlot, droppedItems.get(0));
                droppedItems.remove(0);
            }
        }

        return droppedItems;
    }
}
