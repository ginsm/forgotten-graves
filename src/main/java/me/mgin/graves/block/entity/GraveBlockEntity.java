package me.mgin.graves.block.entity;

import com.mojang.authlib.GameProfile;

import me.mgin.graves.config.GraveRetrievalType;
import me.mgin.graves.config.GravesConfig;
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
	private DefaultedList<ItemStack> items;
	private int xp;
	private int noAge;
	private GameProfile graveOwner;
	private String customName;
	private String graveSkull;
	private BlockState state;

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
		return customName != "";
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

	/**
	 * Determines whether any of the following is true:
	 *
	 * <p>
	 * The grave has no owner, the player is the owner, grave robbing is enabled, or
	 * the player's operator permission level meets the requirements.
	 * </p>
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean playerCanAttemptRetrieve(PlayerEntity player) {
		boolean graveRobbing = GravesConfig.getConfig().serverSettings.enableGraveRobbing;

		if (getGraveOwner() == null || isGraveOwner(player) || graveRobbing || playerCanOverride(player)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines whether operator override is enabled, and if the player meets the
	 * necessary override level requirements.
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean playerCanOverride(PlayerEntity player) {
		int operatorOverrideLevel = GravesConfig.getConfig().serverSettings.minOperatorOverrideLevel;
		return (operatorOverrideLevel != -1 && player.hasPermissionLevel(operatorOverrideLevel));
	}

	/**
	 * Determines whether the player can break the block with left click
	 * (RetrievalType.ON_BREAK || RetrievalType.ON_BOTH).
	 *
	 * <p>
	 * In addition, it checks whether the player is the owner, if grave robbing is
	 * enabled, or if the player can override the protection with the proper
	 * operator level.
	 * <p>
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean playerCanBreakGrave(PlayerEntity player) {
		GraveRetrievalType retrievalType = GravesConfig.getConfig().mainSettings.retrievalType;

		if (playerCanAttemptRetrieve(player))
			if (retrievalType == GraveRetrievalType.ON_BREAK || retrievalType == GraveRetrievalType.ON_BOTH)
				return true;

		return false;
	}

	/**
	 * Determines whether the player can use the block with right click
	 * (RetrievalType.ON_USE || RetrievalType.ON_BOTH).
	 *
	 * <p>
	 * In addition, it checks whether the player is the owner, if grave robbing is
	 * enabled, or if the player can override the protection with the proper
	 * operator level.
	 * </p>
	 *
	 * @param player
	 * @return
	 */
	public boolean playerCanUseGrave(PlayerEntity player) {
		GraveRetrievalType retrievalType = GravesConfig.getConfig().mainSettings.retrievalType;

		if (playerCanAttemptRetrieve(player))
			if (retrievalType == GraveRetrievalType.ON_USE || retrievalType == GraveRetrievalType.ON_BOTH)
				return true;

		return false;
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

		if (customName != null && !customName.isEmpty())
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
