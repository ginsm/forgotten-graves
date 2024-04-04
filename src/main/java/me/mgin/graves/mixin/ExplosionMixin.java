package me.mgin.graves.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final
    private World world;

    @Shadow @Final
    private ObjectArrayList<BlockPos> affectedBlocks;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void removeGravesFromAffectedBlocks(boolean particles, CallbackInfo ci) {
        for (BlockPos blockPos : this.affectedBlocks) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof GraveBlockEntity) affectedBlocks.remove(blockPos);
        }
    }
}