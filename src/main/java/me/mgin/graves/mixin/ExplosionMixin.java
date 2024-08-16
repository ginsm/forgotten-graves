package me.mgin.graves.mixin;

import me.mgin.graves.block.entity.GraveBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// Handle versioned imports
//? if >=1.20 {
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
/*import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import me.mgin.graves.block.GraveBlocks;
*///?}

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final
    private World world;

    //? if >=1.20 {
    @Shadow @Final
    private ObjectArrayList<BlockPos> affectedBlocks;
    //?} else {
    /*@Unique
    private BlockPos lastPos;*/
    //?}

    //? if >=1.20 {
    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void removeGravesFromAffectedBlocks(boolean particles, CallbackInfo ci) {
        for (BlockPos blockPos : this.affectedBlocks) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof GraveBlockEntity) affectedBlocks.remove(blockPos);
        }
    }
    //?} else {
    /*@ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private BlockPos modifyAffectedBlocks(BlockPos pos) {
        lastPos = pos;
        return pos;
    }

    @ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private BlockState modifyAffectedBlocks(BlockState state) {
        if (GraveBlocks.GRAVE_SET.contains(state.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(lastPos);

            if (blockEntity instanceof GraveBlockEntity graveEntity) {
                if (graveEntity.getGraveOwner() != null)
                    return Blocks.AIR.getDefaultState();
            }
        }

        return state;
    }*/
    //?}
}