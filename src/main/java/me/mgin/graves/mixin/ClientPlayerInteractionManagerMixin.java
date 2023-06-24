package me.mgin.graves.mixin;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.client.GravesClient;
import me.mgin.graves.config.ConfigOptions;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.config.enums.GraveRetrievalType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
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
    private MinecraftClient client;

    @Inject(
        method = "breakBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        cancellable = true
    )
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState state,
                            Block block) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        
        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            // Unowned graves can always be broken
            if (graveEntity.getGraveOwner() == null) return;

            // Cases in which the grave should break
            boolean graveRobbing = resolveConfig("graveRobbing").server.graveRobbing;
            boolean canBreak = resolveConfig("retrievalType").main.retrievalType.equals(GraveRetrievalType.BREAK);
            boolean canOverride = false;
            boolean isOwner = false;

            // Get data from client
            if (client.player != null) {
                canOverride = client.player.hasPermissionLevel(
                    resolveConfig("OPOverrideLevel").server.OPOverrideLevel
                );
                isOwner = graveEntity.getGraveOwner().equals(client.player.getGameProfile());
            }

            if (!graveRobbing || !canOverride || !canBreak || !isOwner) {
                cir.setReturnValue(false);
            }
        }
    }

    private static GravesConfig resolveConfig(String option) {
        if (GravesClient.SERVER_CONFIG == null) return GravesConfig.getConfig();

        GravesConfig serverConfig = GravesClient.SERVER_CONFIG;

        if (ConfigOptions.options.get("server").contains(option)) {
            return serverConfig;
        }

        if (serverConfig.server.clientOptions.contains(option)) {
            return GravesConfig.getConfig();
        }

        return serverConfig;
    }
}