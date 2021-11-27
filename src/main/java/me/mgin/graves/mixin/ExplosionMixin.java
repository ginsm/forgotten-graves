package me.mgin.graves.mixin;

import java.util.HashSet;
import java.util.Set;
import me.mgin.graves.Graves;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.Block;
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
	private Set<Block> graveBlocks = new HashSet<Block>() {
		{
			add(Graves.GRAVE);
			add(Graves.GRAVE_OLD);
			add(Graves.GRAVE_WEATHERED);
			add(Graves.GRAVE_FORGOTTEN);
		}
	};

	@ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private BlockPos modifyAffectedBlocks(BlockPos old) {
		lastPos = old;
		return old;
	}

	@ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private BlockState modifyAffectedBlocks(BlockState old) {
		if (graveBlocks.contains(old.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(lastPos);

			if (blockEntity instanceof GraveBlockEntity graveBlockEntity) {
				if (graveBlockEntity.getGraveOwner() != null)
					return Blocks.AIR.getDefaultState();
			}
		}

		return old;
	}
}
