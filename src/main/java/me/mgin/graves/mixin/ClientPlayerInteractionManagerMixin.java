package me.mgin.graves.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.mgin.graves.block.entity.GraveBlockEntity;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

  @Shadow
  @Final
  private ClientPlayNetworkHandler networkHandler;

  @Shadow
  @Final
  private MinecraftClient client;


  @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState blockState, Block block) {
    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (blockEntity instanceof GraveBlockEntity graveBlockEntity && graveBlockEntity.getGraveOwner() != null) {
      if (!graveBlockEntity.getGraveOwner().getId().equals(this.networkHandler.getProfile().getId()))
        cir.setReturnValue(false);
    }
  }
  
}
