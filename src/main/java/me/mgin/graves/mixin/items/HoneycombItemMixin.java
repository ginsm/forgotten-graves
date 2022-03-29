package me.mgin.graves.mixin.items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mgin.graves.block.api.Particles;
import me.mgin.graves.block.api.Permission;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

	@Inject(method = "useOnBlock", at = @At("HEAD"))
	private void onUseBlock(ItemUsageContext context, CallbackInfoReturnable<?> cir) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		Hand hand = context.getHand();

		if (blockEntity instanceof GraveBlockEntity graveEntity
				&& Permission.playerCanAttemptRetrieve(player, graveEntity))
			if (hand == Hand.MAIN_HAND && graveEntity.getNoAge() == 0) {
				player.getStackInHand(context.getHand()).decrement(1);
				graveEntity.setNoAge(1);
				Particles.spawnAtBlock(world, pos, ParticleTypes.WAX_ON, 8, 3);
				world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1f, 1f);
			}
	}

}
