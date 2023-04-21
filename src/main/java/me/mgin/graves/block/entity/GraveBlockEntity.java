package me.mgin.graves.block.entity;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;

import me.mgin.graves.Graves;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.util.NbtHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;

public class GraveBlockEntity extends BlockEntity {
    private GameProfile graveOwner;
    private BlockState state;
    private int xp;
    private int noDecay;
    private String customName;
    private String graveSkull;
    private long mstime;
    private final Map<String, DefaultedList<ItemStack>> inventories = new HashMap<>() {
    };

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(GraveBlocks.GRAVE_BLOCK_ENTITY, pos, state);
        this.graveOwner = null;
        this.customName = "";
        this.graveSkull = "";
        this.xp = 0;
        this.noDecay = 0;
        this.mstime = 0;
        setState(state);
    }

    /**
     * Set an inventory inside inventories.
     *
     * @param key String
     * @param items {@code DefaultedList<ItemStack>}
     */
    public void setInventory(String key, DefaultedList<ItemStack> items) {
        this.inventories.put(key, items);
        this.markDirty();
    }

    /**
     * Retrieve an inventory from the inventories.
     *
     * @param key String
     * @return {@code DefaultedList<ItemStack>}
     */
    public DefaultedList<ItemStack> getInventory(String key) {
        return this.inventories.get(key);
    }

    /**
     * Store the grave owner's GameProfile.
     *
     * @param profile GameProfile
     */
    public void setGraveOwner(GameProfile profile) {
        this.graveOwner = profile;
        this.markDirty();
    }

    /**
     * Retrieve the grave owner's GameProfile.
     *
     * @return GameProfile
     */
    public GameProfile getGraveOwner() {
        return graveOwner;
    }

    /**
     * Determines whether the player's gameprofile ID matches the grave owner's
     * gameprofile ID.
     *
     * @param player GameProfile
     * @return boolean
     */
    public boolean isGraveOwner(PlayerEntity player) {
        return graveOwner.getId().equals(player.getGameProfile().getId());
    }

    /**
     * Set the GraveBlockEntity's custom name.
     *
     * @param name String
     */
    public void setCustomName(String name) {
        this.customName = name;
        this.markDirty();
    }

    /**
     * Get the GraveBlockEntity's custom name.
     *
     * @return String
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * Determines whether the GraveBlockEntity has a custom name.
     *
     * @return boolean
     */
    public boolean hasCustomName() {
        return customName.length() > 0;
    }

    /**
     * Set GraveBlockEntity's current state.
     *
     * @param state BlockState
     */
    public void setState(BlockState state) {
        this.state = state;
    }

    /**
     * Get GraveBlockEntity's current state.
     *
     * @return BlockState
     */
    public BlockState getState() {
        return state;
    }

    /**
     * Set the stored XP amount.
     *
     * @param xp int
     */
    public void setXp(int xp) {
        this.xp = xp;
        this.markDirty();
    }

    /**
     * Get the stored XP amount.
     *
     * @return int
     */
    public int getXp() {
        return xp;
    }

    /**
     * Set the time the grave was made
     *
     * @param timeInMilliseconds long
     */
    public void setMstime(long timeInMilliseconds) {
        this.mstime = timeInMilliseconds;
        this.markDirty();
    }

    /**
     * Get the time the grave was made (in milliseconds)
     *
     */
    public long getMstime() {
        return mstime;
    }

    /**
     * Set whether the grave should age or not.
     * <p>
     * <strong>Note:</strong> The grave stops aging if the value is set to 1 (one).
     *
     * @param aging int
     */
    public void setNoDecay(int aging) {
        this.noDecay = aging;
        this.markDirty();
    }

    /**
     * Get the current noDecay value.
     *
     * @return int
     */
    public int getNoDecay() {
        return this.noDecay;
    }

    /**
     * Set the GraveBlockEntity's SkinURL OR SkullType string.
     * <p>
     * <strong>Note:</strong> A SkinURL is the base64 encoded string typically
     * attached to custom player heads.
     *
     * @param graveSkull String
     */
    public void setGraveSkull(String graveSkull) {
        this.graveSkull = graveSkull;
        this.markDirty();
    }

    /**
     * Retrieve the GraveBlockEntity's SkinURL OR SkullType string.
     * <p>
     * <strong>Note:</strong> A SkinURL is the base64 encoded string typically
     * attached to custom player heads.
     *
     * @return String (SkinURL | SkullType)
     */
    public String getGraveSkull() {
        return this.graveSkull;
    }

    /**
     * Determine whether the GraveBlockEntity has a GraveSkull entry.
     *
     * @return boolean
     */
    public boolean hasGraveSkull() {
        return !this.graveSkull.equals("");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        for (InventoriesApi api : Graves.inventories) {
            String id = api.getID();
            DefaultedList<ItemStack> inventory = this.getInventory(id);

            if (inventory != null) {
                nbt = NbtHelper.writeInventory(id, inventory, nbt);
            }
        }

        for (String modID : Graves.unloadedInventories) {
            DefaultedList<ItemStack> inventory = this.getInventory(modID);

            if (inventory != null) {
                nbt = NbtHelper.writeInventory(modID, inventory, nbt);
            }
        }

        nbt.putInt("XP", xp);
        nbt.putInt("noDecay", noDecay);
        nbt.putLong("mstime", mstime);

        if (graveOwner != null)
            nbt.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), graveOwner));

        if (customName != null && this.hasCustomName())
            nbt.putString("CustomName", customName);

        if (graveSkull != null)
            nbt.putString("GraveSkull", graveSkull);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // Needed for backwards compatibility
        nbt = NbtHelper.upgradeOldGraves(nbt);
        super.readNbt(nbt);

        // Store loaded inventories
        for (InventoriesApi api : Graves.inventories) {
            String id = api.getID();
            if (nbt.contains(id)) {
                this.setInventory(id, NbtHelper.readInventory(id, nbt));
            }
        }

        // Store unloaded inventories
        for (String modID : Graves.unloadedInventories) {
            if (nbt.contains(modID)) {
                this.setInventory(modID, NbtHelper.readInventory(modID, nbt));
            }
        }

        this.xp = nbt.getInt("XP");
        this.noDecay = nbt.getInt("noDecay");
        this.mstime = nbt.getLong("mstime");

        if (nbt.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(nbt.getCompound("GraveOwner"));

        if (nbt.contains("CustomName"))
            this.customName = nbt.getString("CustomName");

        if (nbt.contains("GraveSkull"))
            this.graveSkull = nbt.getString("GraveSkull");

        super.markDirty();
    }

    /**
     * Retrieves the NBT data for the given GraveBlockEntity instance.
     *
     * @return NbtCompound
     */
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        this.writeNbt(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, (BlockEntity b) -> this.toNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.toNbt();
    }
}
