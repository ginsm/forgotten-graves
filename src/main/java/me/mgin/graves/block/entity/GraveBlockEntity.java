package me.mgin.graves.block.entity;

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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GraveBlockEntity extends BlockEntity {
    private GameProfile graveOwner;
    private BlockState state;
    private int[] xp;
    private int noDecay;
    private String customName;
    private NbtCompound graveSkull;
    private long mstime;
    private final Map<String, Integer> timers = new HashMap<>();
    private final Map<String, DefaultedList<ItemStack>> inventories = new HashMap<>();

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(GraveBlocks.GRAVE_BLOCK_ENTITY, pos, state);
        this.graveOwner = null;
        this.customName = "";
        this.graveSkull = null;
        this.xp = new int[]{0,0};
        this.noDecay = 0;
        this.mstime = 0;
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
        return !customName.isEmpty();
    }

    /**
     * Get GraveBlockEntity's current state.
     *
     * @return BlockState
     */
    public BlockState getState() {
        return this.getCachedState();
    }

    /**
     * Set the stored XP amount.
     *
     * @param xp int[]
     */
    public void setXp(int[] xp) {
        this.xp = xp;
        this.markDirty();
    }

    /**
     * Get the stored XP amount.
     *
     * @return int[]
     */
    public int[] getXp() {
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

    public int getTimer(String key) {
        return this.timers.getOrDefault(key, 0);
    }

    public void incrementTimer(String key, int amount) {
        this.timers.put(key, getTimer(key) + amount);
        this.markDirty();
    }

    public void resetTimer(String key) {
        this.timers.put(key, 0);
        this.markDirty();
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
    public void setGraveSkull(NbtCompound graveSkull) {
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
    public NbtCompound getGraveSkull() {
        return this.graveSkull;
    }

    /**
     * Determine whether the GraveBlockEntity has a GraveSkull entry.
     *
     * @return boolean
     */
    public boolean hasGraveSkull() {
        if (this.graveSkull == null) {
            return false;
        }
        return !this.graveSkull.isEmpty();
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

        nbt.putIntArray("XP", xp);
        nbt.putInt("noDecay", noDecay);
        nbt.putLong("mstime", mstime);

        // Used for tracking time spent at each stage and expiration
        NbtCompound timersNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : timers.entrySet()) {
            timersNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("timers", timersNbt);

        if (graveOwner != null)
            nbt.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), graveOwner));

        if (customName != null && this.hasCustomName())
            nbt.putString("CustomName", customName);

        if (graveSkull != null)
            nbt.put("GraveSkull", graveSkull);
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

        this.xp = nbt.getIntArray("XP");
        this.noDecay = nbt.getInt("noDecay");
        this.mstime = nbt.getLong("mstime");

        // Used for tracking time spent at each stage and expiration
        timers.clear();
        NbtCompound timersNbt = nbt.getCompound("timers");
        for (String key : timersNbt.getKeys()) {
            timers.put(key, timersNbt.getInt(key));
        }

        if (nbt.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(nbt.getCompound("GraveOwner"));

        if (nbt.contains("CustomName"))
            this.customName = nbt.getString("CustomName");

        if (nbt.contains("GraveSkull"))
            this.graveSkull = (NbtCompound) nbt.get("GraveSkull");

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
