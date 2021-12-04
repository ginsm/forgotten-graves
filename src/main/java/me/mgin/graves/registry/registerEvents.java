package me.mgin.graves.registry;

import me.mgin.graves.events.PlayerBlockBreakHandler;
import me.mgin.graves.events.UseBlockHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class registerEvents {
  public static void register() {
    PlayerBlockBreakEvents.BEFORE.register((World world, PlayerEntity player, BlockPos pos, BlockState state,	BlockEntity entity) ->
      PlayerBlockBreakHandler.handleEvent(player, pos, entity)
    );

    UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> 
      UseBlockHandler.handleEvent(player, world, hand, hitResult)
    );
  }
}
