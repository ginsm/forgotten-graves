package me.mgin.graves.block;

//? if >1.20.2 {
/*import com.mojang.serialization.MapCodec;
*///?}
import me.mgin.graves.block.decay.DecayStateManager;
import me.mgin.graves.command.DeleteCommand;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.item.Items;
import net.minecraft.block.HorizontalFacingBlock;
import me.mgin.graves.versioned.VersionedCode;
import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Permission;
import me.mgin.graves.block.utility.RetrieveGrave;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

import static me.mgin.graves.block.GraveBlocks.GRAVE_SET;

@SuppressWarnings("ALL")
public class GraveBlockBase extends HorizontalFacingBlock implements BlockEntityProvider, DecayingGrave, Waterloggable {
    private final BlockDecay blockDecay;
    public boolean brokenByPlayer = false;
    public String blockID;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private boolean countingSeconds = false;

    public GraveBlockBase(BlockDecay blockDecay, Settings settings, String blockID) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(Properties.HORIZONTAL_FACING,
            Direction.NORTH));
        this.blockDecay = blockDecay;
        this.blockID = blockID;
    }

    /**
     * Used when registering grave items and blocks.
     * @see Items
     * @see GraveBlocks
     */
    public String getBlockID() {
        return this.blockID;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING).add(Properties.WATERLOGGED);
    }

    /**
     * Prevents a grave item dropping when dying to explosions.
     */
    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    /**
     * Respawns the grave unless they were retrieved via {@link RetrieveGrave#retrieve} or deleted via
     * {@link DeleteCommand#execute}.
     */
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // Do nothing if the grave is simply decaying
        if (GRAVE_SET.contains(newState.getBlock())) return;

        // Do nothing if the player broke the grave
        if (this.brokenByPlayer) {
            this.setBrokenByPlayer(false);
            super.onStateReplaced(state, world, pos, newState, moved);
        } else {
            if (state.hasBlockEntity()) {
                world.setBlockState(pos, state);
                world.addBlockEntity(world.getBlockEntity(pos));
            }
        }
    }

    public void setBrokenByPlayer(boolean status) {
        this.brokenByPlayer = status;
    }

    /**
     * Allows for player owned grave retrieval via right click.
     */
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
     * to vanilla behavior when breaking a grave.
     */
    @Override
    //? if >1.20.2 {
    /*public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    *///?} else {
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    //?}
        this.setBrokenByPlayer(true);
        GraveBlockEntity graveEntity = (GraveBlockEntity) world.getBlockEntity(pos);

        //? if >1.20.2 {
        /*if (world.isClient) return state;
        *///?} else {
        if (world.isClient) return;
        //?}

        if (Permission.playerCanBreakGrave(player, graveEntity)) {
            // This will be true if the grave had an owner
            boolean retrieved = RetrieveGrave.retrieveWithInteract(player, world, pos);

            // Ensures dropped item stack has proper custom name
            if (!retrieved && graveEntity.hasCustomName() && !player.isCreative())
                onBreakRetainName(world, pos, player, graveEntity);
        }

        super.onBreak(world, pos, state, player);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        //? if >1.20.2 {
        /*return state;
        *///?}
    }

    public void onBreakRetainName(World world, BlockPos pos, PlayerEntity player, GraveBlockEntity graveEntity) {
        Text itemText = VersionedCode.textFromJson(graveEntity.getCustomName());

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
        return GraveBlockShapes.getGraveShape(state, getBlockID());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return GraveBlockShapes.getGraveShape(state, getBlockID());
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

        String customName = itemStack.getOrCreateSubNbt("display").getString("Name");
        graveEntity.setCustomName(
            //? if >1.20.2 {
            /*// Handle custom names with newline characters
            customName.replace("\\\\n", "\\n")
            *///?} else {
            customName
            //?}
        );
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        FluidState fluidState = context.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing()).with(WATERLOGGED,
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
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // Decay
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            // Schedule initial tick for counter
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if (!world.isClient()) {
            GraveBlockEntity entity = (GraveBlockEntity) world.getBlockEntity(pos);
            GravesConfig config = GravesConfig.getConfig();
            boolean inFinalStage = this.getDecayStage() == BlockDecay.FORGOTTEN;
            boolean decaying = config.decay.decayEnabled && entity.getNoDecay() == 0;

            // This is needed to make old graves start ticking; if countingSeconds is false when the old grave
            // gets a random tick, it'll schedule ticks.
            if (!this.countingSeconds) this.countingSeconds = true;

            if (decaying && !inFinalStage) {
                entity.incrementTimer("decay", 1);

                int secondsInStage = entity.getTimer("decay");
                int maxStageTimeSeconds = config.decay.maxStageTimeSeconds;

                // If the maxStageTimeSeconds is 0 then maxStageTimeSeconds is effectively disabled.
                if (maxStageTimeSeconds > 0 && secondsInStage >= maxStageTimeSeconds) {
                    DecayStateManager.increaseDecayState(world, pos);
                }
            }

            // Reschedule the next tick
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient && !this.countingSeconds) {
            // Schedule initial tick for counter if it's not already running
            world.scheduleBlockTick(pos, this, 20);
        }

        GraveBlockEntity entity = (GraveBlockEntity) world.getBlockEntity(pos);
        int secondsInStage = entity.getTimer("decay");
        int minStageTimeSeconds = GravesConfig.getConfig().decay.minStageTimeSeconds;

        if (secondsInStage >= minStageTimeSeconds) {
            this.tickDecay(state, world, pos, random);
        }
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

    //? if >1.20.2 {
    /*@Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }
    *///?}
}
