package me.mgin.graves.block.entity;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.registry.GraveBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;

public class GraveBlockEntity extends BlockEntity {
	private GameProfile graveOwner;
	private BlockState state;
	private DefaultedList<ItemStack> items;
	private int xp;
	private int noAge;
	private String customName;
	private String graveSkull;
	private Map<String, DefaultedList<ItemStack>> inventories =
		new HashMap<String, DefaultedList<ItemStack>>() {
			{
				put("items", DefaultedList.ofSize(41, ItemStack.EMPTY));
			}
		};

	public GraveBlockEntity(BlockPos pos, BlockState state) {
		super(GraveBlocks.GRAVE_BLOCK_ENTITY, pos, state);
		this.graveOwner = null;
		this.items = DefaultedList.ofSize(41, ItemStack.EMPTY);
		this.customName = "";
		this.graveSkull = "";
		this.xp = 0;
		this.noAge = 0;
		setState(state);
	}

	/**
	 * Set the GraveBlockEntity's items.
	 *
	 * @param items
	 */
	public void setItems(DefaultedList<ItemStack> items) {
		this.items = items;
		this.markDirty();
	}

	/**
	 * Set an inventory inside inventories.
	 * @param key
	 * @param items
	 */
	public void setInventory(String key, DefaultedList<ItemStack> items) {
		this.inventories.put(key, items);
		this.markDirty();
	}

	/**
	 * Retrieve an inventory from the inventories.
	 * @param key
	 * @return
	 */
	public DefaultedList<ItemStack> getInventory(String key) {
		return this.inventories.get(key);
	}

	/**
	 * Retrieve the GraveBlockEntity's items.
	 *
	 * @return
	 */
	public DefaultedList<ItemStack> getItems() {
		return items;
	}

	/**
	 * Determines whether the GraveBlockEntity has items in it.
	 *
	 * @return boolean
	 */
	public boolean hasItems() {
		return !items.isEmpty();
	}

	/**
	 * Store the grave owner's GameProfile.
	 *
	 * @param profile
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
	 * @param player
	 * @return boolean
	 */
	public boolean isGraveOwner(PlayerEntity player) {
		return graveOwner.getId().equals(player.getGameProfile().getId());
	}

	/**
	 * Set the GraveBlockEntity's custom name.
	 *
	 * @param name
	 */
	public void setCustomName(String name) {
		this.customName = name;
		this.markDirty();
	}

	/**
	 * Get the GraveBlockEntity's custom name.
	 *
	 * @return
	 */
	public String getCustomName() {
		return customName;
	}

	/**
	 * Determines whether the GraveBlockEntity has a custom name.
	 *
	 * @return
	 */
	public boolean hasCustomName() {
		return customName.length() > 0;
	}

	/**
	 * Set GraveBlockEntity's current state.
	 *
	 * @param state
	 */
	public void setState(BlockState state) {
		this.state = state;
	}

	/**
	 * Get GraveBlockEntity's current state.
	 *
	 * @return
	 */
	public BlockState getState() {
		return state;
	}

	/**
	 * Set the stored XP amount.
	 *
	 * @param xp
	 */
	public void setXp(int xp) {
		this.xp = xp;
		this.markDirty();
	}

	/**
	 * Get the stored XP amount.
	 *
	 * @return
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * Set whether the grave should age or not.
	 * <p>
	 * <strong>Note:</strong> The grave stops aging if the value is set to 1 (one).
	 *
	 * @param aging
	 */
	public void setNoAge(int aging) {
		this.noAge = aging;
		this.markDirty();
	}

	/**
	 * Get the current noAge value.
	 *
	 * @return int
	 */
	public int getNoAge() {
		return this.noAge;
	}

	/**
	 * Set the GraveBlockEntity's SkinURL OR SkullType string.
	 * <p>
	 * <strong>Note:</strong> A SkinURL is the base64 encoded string typically
	 * attached to custom player heads.
	 *
	 * @param graveSkull
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
		return this.graveSkull != "";
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);

		nbt.putInt("ItemCount", this.items.size());
		nbt.put("Items", Inventories.writeNbt(new NbtCompound(), this.items, true));
		nbt.putInt("XP", xp);
		nbt.putInt("noAge", noAge);

		if (graveOwner != null)
			nbt.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), graveOwner));

		if (customName != null && this.hasCustomName())
			nbt.putString("CustomName", customName);

		if (graveSkull != null)
			nbt.putString("GraveSkull", graveSkull);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		this.items = DefaultedList.ofSize(nbt.getInt("ItemCount"), ItemStack.EMPTY);
		Inventories.readNbt(nbt.getCompound("Items"), this.items);
		this.xp = nbt.getInt("XP");
		this.noAge = nbt.getInt("noAge");

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

	/**
	 * When called on the server, schedules a BlockEntity sync to client.
	 *
	 * @param world
	 * @param pos
	 */
	public void sync(World world, BlockPos pos) {
		((ServerWorld) world).getChunkManager().markForUpdate(pos);
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
