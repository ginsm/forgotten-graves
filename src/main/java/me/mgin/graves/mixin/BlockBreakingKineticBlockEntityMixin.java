package me.mgin.graves.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com/simibubi/create/content/kinetics/base/BlockBreakingKineticBlockEntity")
public class BlockBreakingKineticBlockEntityMixin {
    @ModifyReturnValue(method = "isBreakable(Lnet/minecraft/block/BlockState;F)Z", at = @At("RETURN"))
    private static boolean stopDrillBreakingGraves(boolean result, BlockState state, float hardness) {
        return result && !(state.getBlock() instanceof GraveBlockBase);
    }
}