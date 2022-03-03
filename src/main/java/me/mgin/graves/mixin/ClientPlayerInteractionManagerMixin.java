package me.mgin.graves.mixin;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.GraveRetrievalType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow
	@Final
	private ClientPlayNetworkHandler networkHandler;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState state,
			Block block) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		PlayerEntity player = client.player;

		GraveRetrievalType retrievalType = GravesConfig.resolveConfig("retrievalType",
				player.getGameProfile()).main.retrievalType;
		boolean graveRobbingEnabled = GravesConfig.getConfig().server.enableGraveRobbing;

		if (blockEntity instanceof GraveBlockEntity graveEntity && graveEntity.getGraveOwner() != null) {
			if (retrievalType != GraveRetrievalType.ON_BREAK && retrievalType != GraveRetrievalType.ON_BOTH)
				cir.setReturnValue(false);

			if (!graveEntity.getGraveOwner().getId().equals(this.networkHandler.getProfile().getId())
					&& !graveRobbingEnabled)
				cir.setReturnValue(false);
		}
	}
}
