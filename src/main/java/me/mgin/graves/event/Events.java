package me.mgin.graves.event;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.mgin.graves.event.server.PlayerBlockBreakHandler;
import me.mgin.graves.event.server.TrinketDropHandler;
import me.mgin.graves.event.server.UseBlockHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Events {
    /**
     * Registers all generic server-side event handlers.
     */
    public static void registerServerEvents() {
        // Handle player using grave
        UseBlockCallback.EVENT.register(UseBlockHandler::handleEvent);

        // Handle player breaking grave
        PlayerBlockBreakEvents.BEFORE.register(
            (World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) ->
                PlayerBlockBreakHandler.handleBeforeEvent(player, entity)
        );

        // Needed to override trinket drop behavior
        if (FabricLoader.getInstance().isModLoaded("trinkets"))
            TrinketDropCallback.EVENT.register(
                (TrinketEnums.DropRule rule, ItemStack stack, SlotReference ref, LivingEntity entity) ->
                    TrinketDropHandler.handleTrinketDrop(ref, entity)
            );
    }
}
