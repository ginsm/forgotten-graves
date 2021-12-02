package me.mgin.graves.mixin.items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
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

    if (blockEntity instanceof GraveBlockEntity graveBlockEntity)
      if (hand == Hand.MAIN_HAND && graveBlockEntity.getNoAge() == 0) {
        player.getStackInHand(context.getHand()).decrement(1);
        graveBlockEntity.setNoAge(1);
      }
  }
  
}
