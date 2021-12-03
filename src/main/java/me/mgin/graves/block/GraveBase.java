package me.mgin.graves.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.mgin.graves.Graves;
import me.mgin.graves.api.GravesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.GraveDropType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GraveBase extends HorizontalFacingBlock implements BlockEntityProvider, AgingGrave {

	private final BlockAge blockAge;

	public GraveBase(BlockAge blockAge, Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
		this.blockAge = blockAge;
	}

	FlowerBlock f;

	public int getWeathered() {
		int stage;
		switch (blockAge) {
			default :
				stage = 0;
				break;
			case OLD :
				stage = 1;
				break;
			case WEATHERED :
				stage = 2;
				break;
			case FORGOTTEN :
				stage = 3;
				break;
		}
		return stage;
	}

	public GraveBase getAgedBlock() {
		switch (blockAge) {
			default :
				return Graves.GRAVE;
			case OLD :
				return Graves.GRAVE_OLD;
			case WEATHERED :
				return Graves.GRAVE_WEATHERED;
			case FORGOTTEN :
				return Graves.GRAVE_FORGOTTEN;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) world.getBlockEntity(pos);

		if (hand != Hand.OFF_HAND)
			if (player.getStackInHand(hand).isEmpty() && graveBlockEntity.playerCanUseGrave(player))
				useGrave(player, world, pos);

		return ActionResult.PASS;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) world.getBlockEntity(pos);

		if (graveBlockEntity.playerCanBreakGrave(player))
			if (useGrave(player, world, pos))
				return;

		super.onBreak(world, pos, state, player);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ct) {
		return VoxelShapes.cuboid(0.062f, 0f, 0f, 0.938f, 0.1875f, 0.938f);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0.062f, 0f, 0.062f, 0.938f, 0.02f, 0.938f);
	}

	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.IGNORE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GraveBlockEntity(pos, state);
	}

	private boolean useGrave(PlayerEntity playerEntity, World world, BlockPos pos) {
		if (world.isClient)
			return false;

		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (!(blockEntity instanceof GraveBlockEntity))
			return false;

		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) blockEntity;
		graveBlockEntity.sync(world, pos);

		if (graveBlockEntity.getItems() == null)
			return false;
		if (graveBlockEntity.getGraveOwner() == null)
			return false;

		if (!graveBlockEntity.playerCanAttemptRetrieve(playerEntity))
			if (!graveBlockEntity.playerCanOverride(playerEntity))
				return false;

		DefaultedList<ItemStack> items = graveBlockEntity.getItems();

		DefaultedList<ItemStack> inventory = DefaultedList.of();

		inventory.addAll(playerEntity.getInventory().main);
		inventory.addAll(playerEntity.getInventory().armor);
		inventory.addAll(playerEntity.getInventory().offHand);

		GraveDropType dropType = GravesConfig.getConfig().mainSettings.dropType;

		if (dropType == GraveDropType.PUT_IN_INVENTORY) {
			playerEntity.getInventory().clear();

			List<ItemStack> armor = items.subList(36, 40);

			for (int i = 0; i < armor.size(); i++) {
				EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor.get(i));
				playerEntity.equipStack(equipmentSlot, armor.get(i));
			}

			playerEntity.equipStack(EquipmentSlot.OFFHAND, items.get(40));

			List<ItemStack> mainInventory = items.subList(0, 36);

			for (int i = 0; i < mainInventory.size(); i++) {
				playerEntity.getInventory().setStack(i, mainInventory.get(i));
			}

			DefaultedList<ItemStack> extraItems = DefaultedList.of();

			List<Integer> openArmorSlots = getInventoryOpenSlots(playerEntity.getInventory().armor);

			for (int i = 0; i < 4; i++) {
				if (openArmorSlots.contains(i)) {
					playerEntity.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i),
							inventory.subList(36, 40).get(i));
				} else
					extraItems.add(inventory.subList(36, 40).get(i));
			}

			if (playerEntity.getInventory().offHand.get(0) == ItemStack.EMPTY) {
				playerEntity.equipStack(EquipmentSlot.OFFHAND, inventory.get(40));
			} else {
				extraItems.add(inventory.get(40));
			}

			extraItems.addAll(inventory.subList(0, 36));

			List<Integer> openSlots = getInventoryOpenSlots(playerEntity.getInventory().main);

			for (int i = 0; i < openSlots.size(); i++) {
				playerEntity.getInventory().setStack(openSlots.get(i), extraItems.get(i));
			}

			DefaultedList<ItemStack> dropItems = DefaultedList.of();

			dropItems.addAll(extraItems.subList(openSlots.size(), extraItems.size()));

			int inventoryOffset = 41;

			for (GravesApi GravesApi : Graves.apiMods) {
				GravesApi.setInventory(
						items.subList(inventoryOffset, inventoryOffset + GravesApi.getInventorySize(playerEntity)),
						playerEntity);
				inventoryOffset += GravesApi.getInventorySize(playerEntity);
			}

			ItemScatterer.spawn(world, pos, dropItems);
		} else if (dropType == GraveDropType.DROP_ITEMS) {
			ItemScatterer.spawn(world, pos, graveBlockEntity.getItems());
		}

		playerEntity.addExperience((int) (1 * graveBlockEntity.getXp()));

		spawnBreakParticles(world, playerEntity, pos, getDefaultState());

		world.removeBlock(pos, false);
		return true;
	}

	private List<Integer> getInventoryOpenSlots(DefaultedList<ItemStack> inventory) {
		List<Integer> openSlots = new ArrayList<>();
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i) == ItemStack.EMPTY)
				openSlots.add(i);
		}
		return openSlots;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (!(blockEntity instanceof GraveBlockEntity) || !itemStack.hasCustomName()) {
			super.onPlaced(world, pos, state, placer, itemStack);
			return;
		}

		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) blockEntity;

		graveBlockEntity.setCustomNametag(itemStack.getOrCreateSubNbt("display").getString("Name"));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return AgingGrave.getIncreasedOxidationBlock(state.getBlock()).isPresent();
	}

	@Override
	public BlockAge getDegradationLevel() {
		return this.blockAge;
	}

	@Override
	public void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof GraveBlockEntity graveBlockEntity) {
			if (graveBlockEntity.getNoAge() == 1)
				return;
		}

		AgingGrave.super.tickDegradation(state, world, pos, random);
	}
}
