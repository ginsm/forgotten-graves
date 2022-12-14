package me.mgin.graves.mixin;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.registry.GraveBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Explosion.class)
public class ExplosionMixin {
	@Shadow
	@Final
	private World world;
	private BlockPos lastPos;

	@ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private BlockPos modifyAffectedBlocks(BlockPos old) {
		lastPos = old;
		return old;
	}

	@ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private BlockState modifyAffectedBlocks(BlockState old) {
		if (GraveBlocks.GRAVE_MAP.containsKey(old.getBlock()))  {
			BlockEntity blockEntity = world.getBlockEntity(lastPos);

			if (blockEntity instanceof GraveBlockEntity graveEntity) {
				if (graveEntity.getGraveOwner() != null)
					return Blocks.AIR.getDefaultState();
			}
		}

		return old;
	}
}
