package me.mgin.graves.block.entity;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.Graves;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class GraveBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
	private DefaultedList<ItemStack> items;
	private int xp;
	private GameProfile graveOwner;
	private String customName;
	private BlockState state;

	public GraveBlockEntity(BlockPos pos, BlockState blockState) {
		super(Graves.GRAVE_BLOCK_ENTITY, pos, blockState);
		this.customName = "";
		this.graveOwner = null;
		this.xp = 0;
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

	public boolean isGraveOwner(GameProfile profile) {
		return graveOwner == profile;
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

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
		this.markDirty();
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.writeNbt(tag);

		this.items = DefaultedList.ofSize(tag.getInt("ItemCount"), ItemStack.EMPTY);

		Inventories.readNbt(tag.getCompound("Items"), this.items);

		this.xp = tag.getInt("XP");

		if (tag.contains("GraveOwner"))
			this.graveOwner = NbtHelper.toGameProfile(tag.getCompound("GraveOwner"));

		if (tag.contains("CustomName"))
			this.customName = tag.getString("CustomName");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		tag.putInt("ItemCount", this.items.size());

		tag.put("Items", Inventories.writeNbt(new NbtCompound(), this.items, true));

		tag.putInt("XP", xp);

		if (graveOwner != null)
			tag.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), graveOwner));
		if (customName != null && !customName.isEmpty())
			tag.putString("CustomName", customName);

		return tag;
	}

	@Override
	public void fromClientTag(NbtCompound compoundTag) {
		if (compoundTag.contains("GraveOwner"))
			this.graveOwner = NbtHelper.toGameProfile(compoundTag.getCompound("GraveOwner"));
		if (compoundTag.contains("CustomName"))
			this.customName = compoundTag.getString("CustomName");
	}

	@Override
	public NbtCompound toClientTag(NbtCompound compoundTag) {
		if (graveOwner != null)
			compoundTag.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), this.graveOwner));
		if (customName != null && !customName.isEmpty())
			compoundTag.putString("CustomName", customName);

		return compoundTag;
	}

	public BlockState getState() {
		return state;
	}

	public void setState(BlockState state) {
		this.state = state;
	}
}
