package me.mgin.graves.mixin.items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.util.Particles;
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
    BlockPos blockPos = context.getBlockPos();
    PlayerEntity player = context.getPlayer();
    BlockEntity blockEntity = world.getBlockEntity(blockPos);
    Hand hand = context.getHand();

    if (blockEntity instanceof GraveBlockEntity graveBlockEntity && graveBlockEntity.isGraveOwner(player))
      if (hand == Hand.MAIN_HAND && graveBlockEntity.getNoAge() == 0) {
        player.getStackInHand(context.getHand()).decrement(1);
        graveBlockEntity.setNoAge(1);
        Particles.spawnAtBlock(ParticleTypes.WAX_ON, 8, 3, world, blockPos);
        world.playSound(null, blockPos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1f, 1f);
      }
  }
  
}
