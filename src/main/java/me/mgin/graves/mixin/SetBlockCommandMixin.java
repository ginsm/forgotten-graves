package me.mgin.graves.mixin;

import me.mgin.graves.block.GraveBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(SetBlockCommand.class)
public class SetBlockCommandMixin {

    @Inject(method = "execute", at = @At("HEAD"))
    private static void execute$setGraveBrokenByPlayer(ServerCommandSource source, BlockPos pos,
                                                       BlockStateArgument block, SetBlockCommand.Mode mode, @Nullable Predicate<CachedBlockPosition> condition, CallbackInfoReturnable<Integer> cir) {
        ServerWorld world = source.getWorld();
        Block blockAtPos = world.getBlockState(pos).getBlock();

        // Mark grave block as broken by player so that the command actually succeeds
        if (blockAtPos instanceof GraveBlockBase graveBlock) {
            graveBlock.setBrokenByPlayer(true);
        }
    }
}
