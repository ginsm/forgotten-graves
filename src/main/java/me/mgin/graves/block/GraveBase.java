package me.mgin.graves.block;

import java.util.Random;
import me.mgin.graves.block.api.Permission;
import me.mgin.graves.block.api.RetrieveGrave;
import me.mgin.graves.block.degradation.AgingGrave;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.registry.GraveBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class GraveBase extends HorizontalFacingBlock implements BlockEntityProvider, AgingGrave, Waterloggable {

	private final BlockAge blockAge;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public GraveBase(BlockAge blockAge, Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(Properties.HORIZONTAL_FACING,
				Direction.NORTH));
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

	public static GraveBase getAgedBlock(BlockAge blockAge) {
		switch (blockAge) {
			default :
				return GraveBlocks.GRAVE;
			case OLD :
				return GraveBlocks.GRAVE_OLD;
			case WEATHERED :
				return GraveBlocks.GRAVE_WEATHERED;
			case FORGOTTEN :
				return GraveBlocks.GRAVE_FORGOTTEN;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING).add(Properties.WATERLOGGED);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

		if (world.isClient)
			return ActionResult.PASS;

		if (hand != Hand.OFF_HAND)
			if (player.getStackInHand(hand).isEmpty() && Permission.playerCanUseGrave(player, graveEntity))
				useGrave(player, world, pos);

		return ActionResult.PASS;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

		if (world.isClient)
			return;

		if (Permission.playerCanBreakGrave(player, graveEntity))
			if (useGrave(player, world, pos))
				return;

		if (graveEntity.getGraveOwner() == null)
			if (!world.isClient && graveEntity.hasCustomName() && !player.isCreative()) {
				onBreakRetainName(world, pos, player, graveEntity);
				return;
			}

		super.onBreak(world, pos, state, player);
	}

	public void onBreakRetainName(World world, BlockPos pos, PlayerEntity player, GraveBlockEntity graveEntity) {
		Text itemText = Text.Serializer.fromJson(graveEntity.getCustomName());

		ItemStack itemStack = this.getItemStack();
		itemStack.setCustomName(itemText);

		ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5,
				(double) pos.getZ() + 0.5, itemStack);
		itemEntity.setToDefaultPickupDelay();

		spawnBreakParticles(world, player, pos, getDefaultState());
		world.spawnEntity(itemEntity);
		world.removeBlock(pos, false);

		world.emitGameEvent((Entity) player, GameEvent.BLOCK_DESTROY, pos);
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

	private boolean useGrave(PlayerEntity player, World world, BlockPos pos) {
		if (RetrieveGrave.retrieve(player, world, pos)) {
			// Add break particles?
			world.removeBlock(pos, false);
			return true;
		}

		return false;
	}

	public ItemStack getItemStack() {
		GraveBase agedBlock = getAgedBlock(this.blockAge);
		return new ItemStack(agedBlock);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (!(blockEntity instanceof GraveBlockEntity) || !itemStack.hasCustomName()) {
			super.onPlaced(world, pos, state, placer, itemStack);
			return;
		}

		GraveBlockEntity graveEntity = (GraveBlockEntity) blockEntity;

		graveEntity.setCustomName(itemStack.getOrCreateSubNbt("display").getString("Name"));
	}

	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos blockPos = context.getBlockPos();
		FluidState fluidState = context.getWorld().getFluidState(blockPos);
		return this.getDefaultState().with(FACING, context.getPlayerFacing()).with(WATERLOGGED,
				fluidState.getFluid() == Fluids.WATER);
	}

	// Waterlogging
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	// Degradation
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

		if (blockEntity instanceof GraveBlockEntity graveEntity) {
			if (graveEntity.getNoAge() == 1)
				return;
		}

		AgingGrave.super.tickDegradation(state, world, pos, random);
	}
}
