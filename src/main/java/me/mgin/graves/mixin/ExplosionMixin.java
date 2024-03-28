package me.mgin.graves.mixin;

import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Explosion.class)
public class ExplosionMixin {
    // This prevents creepers from blowing up graves as they're spawning.
    @ModifyVariable(method = "affectWorld", at = @At("STORE"), ordinal = 1, print = false)
    private boolean modifyAffectedBlocks(boolean bool) {
        return false;
    }
}