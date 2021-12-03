package me.mgin.graves.block.entity;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import me.mgin.graves.config.GraveRetrievalType;
import me.mgin.graves.config.GravesConfig;
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
	private BlockState state;

	public GraveBlockEntity(BlockPos pos, BlockState blockState) {
		super(Graves.GRAVE_BLOCK_ENTITY, pos, blockState);
		this.customName = "";
		this.graveOwner = null;
		this.xp = 0;
		this.noAge = 0;
		this.items = DefaultedList.ofSize(41, ItemStack.EMPTY);
		setState(blockState);
	}

	public void setItems(DefaultedList<ItemStack> items) {
		this.items = items;
		this.markDirty();
	}

	public DefaultedList<ItemStack> getItems() {
		return items;
	}

	public boolean hasItems() {
		return items.isEmpty();
	}

	public void setGraveOwner(GameProfile gameProfile) {
		this.graveOwner = gameProfile;
		this.markDirty();
	}

	public GameProfile getGraveOwner() {
		return graveOwner;
	}

	public boolean isGraveOwner(PlayerEntity player) {
		return player.getGameProfile().getId() == graveOwner.getId();
	}

	public boolean playerCanRetrieve(PlayerEntity player) {
		boolean graveRobbing = GravesConfig.getConfig().mainSettings.enableGraveRobbing;
		int operatorOverrideLevel = GravesConfig.getConfig().mainSettings.minOperatorOverrideLevel;;

		if (getGraveOwner() == null || isGraveOwner(player) || graveRobbing)
			return true;

		if (operatorOverrideLevel != -1 && player.hasPermissionLevel(operatorOverrideLevel)) {
			System.out.println("[Graves] Operator overrided grave protection at: " + pos);
			return true;
		}

		return false;
	}

	public boolean playerCanBreak(PlayerEntity player) {
		GraveRetrievalType retrievalType = GravesConfig.getConfig().mainSettings.retrievalType;

		if (!playerCanRetrieve(player) || (retrievalType != GraveRetrievalType.ON_BREAK && retrievalType != GraveRetrievalType.ON_BOTH))
			return false;

		return true;
	}

	public boolean playerCanUse(PlayerEntity player) {
		GraveRetrievalType retrievalType = GravesConfig.getConfig().mainSettings.retrievalType;

		if (!playerCanRetrieve(player) || (retrievalType != GraveRetrievalType.ON_USE && retrievalType != GraveRetrievalType.ON_BOTH))
			return false;

		return true;
	}

	public void setCustomNametag(String text) {
		this.customName = text;
		this.markDirty();
	}

	public boolean hasCustomNametag() {
		return customName == "";
	}

	public String getCustomNametag() {
		return customName;
	}

	public void sync(World world, BlockPos pos) {
		((ServerWorld) world).getChunkManager().markForUpdate(pos);
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
		this.markDirty();
	}

	public void setNoAge(int aging) {
		this.noAge = aging;
		this.markDirty();
	}

	public int getNoAge() {
		return this.noAge;
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

		if (nbt.contains("CustomName")) {
			this.customName = nbt.getString("CustomName");
			System.out.println(this.customName);
		}

		super.markDirty();
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
	}

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

	public BlockState getState() {
		return state;
	}

	public void setState(BlockState state) {
		this.state = state;
	}
}
