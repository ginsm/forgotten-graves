package me.mgin.graves.block;

import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Permission;
import me.mgin.graves.block.utility.RetrieveGrave;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GraveBlockBase extends HorizontalFacingBlock implements BlockEntityProvider, DecayingGrave, Waterloggable {
    private final BlockDecay blockDecay;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public GraveBlockBase(BlockDecay blockDecay, Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(Properties.HORIZONTAL_FACING,
            Direction.NORTH));
        this.blockDecay = blockDecay;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING).add(Properties.WATERLOGGED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

        if (world.isClient) return ActionResult.PASS;

        if (hand != Hand.OFF_HAND)
            if (player.getStackInHand(hand).isEmpty() && Permission.playerCanUseGrave(player, graveEntity))
                RetrieveGrave.retrieveWithInteract(player, world, pos);

        return ActionResult.PASS;
    }

    /**
     * Either retrieves a player owned grave, drops a named grave, or defaults
     * to vanilla behavior.
     *
     * @param world World
     * @param pos BlockPos
     * @param state BlockState
     * @param player PlayerEntity
     */
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

        if (world.isClient) return;

        if (Permission.playerCanBreakGrave(player, graveEntity)) {
            // This will be true if the grave had an owner
            boolean retrieved = RetrieveGrave.retrieveWithInteract(player, world, pos);

            // Ensures dropped item stack has proper custom name
            if (!retrieved && graveEntity.hasCustomName() && !player.isCreative())
                onBreakRetainName(world, pos, player, graveEntity);
        }

        super.onBreak(world, pos, state, player);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);
        ItemStack itemStack = this.getItemStack();

        if (graveEntity.hasCustomName()) {
            Text itemText = Text.Serializer.fromJson(graveEntity.getCustomName());
            itemStack.setCustomName(itemText);
        }

        return itemStack;
    }

    public void onBreakRetainName(World world, BlockPos pos, PlayerEntity player, GraveBlockEntity graveEntity) {
        Text itemText = Text.Serializer.fromJson(graveEntity.getCustomName());

        // Create named item stack
        ItemStack itemStack = this.getItemStack();
        itemStack.setCustomName(itemText);
        ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5,
            (double) pos.getZ() + 0.5, itemStack);
        itemEntity.setToDefaultPickupDelay();

        // Break block
        spawnBreakParticles(world, player, pos, getDefaultState());
        world.removeBlock(pos, false);

        // Spawn entity
        world.spawnEntity(itemEntity);
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

    public ItemStack getItemStack() {
        GraveBlockBase decayedBlock = getDecayedBlock(this.blockDecay);
        return new ItemStack(decayedBlock);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof GraveBlockEntity graveEntity) || !itemStack.hasCustomName()) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

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

    // Decay
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDecay(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return DecayingGrave.getIncreasedDecayBlock(state.getBlock()).isPresent();
    }

    @Override
    public BlockDecay getDecayStage() {
        return this.blockDecay;
    }

    public static GraveBlockBase getDecayedBlock(BlockDecay blockDecay) {
        return switch (blockDecay) {
            default -> GraveBlocks.GRAVE;
            case OLD -> GraveBlocks.GRAVE_OLD;
            case WEATHERED -> GraveBlocks.GRAVE_WEATHERED;
            case FORGOTTEN -> GraveBlocks.GRAVE_FORGOTTEN;
        };
    }

    public void tickDecay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            if (graveEntity.getNoDecay() == 1)
                return;
        }

        DecayingGrave.super.tickDecay(state, world, pos, random);
    }
}
