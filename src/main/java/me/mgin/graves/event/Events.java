package me.mgin.graves.event;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.mgin.graves.event.server.*;
import me.mgin.graves.inventory.Trinkets;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Events {
    /**
     * Registers all generic server-side event handlers.
     */
    public static void registerServerEvents() {
        // Handle player attacking grave
        AttackBlockCallback.EVENT.register(AttackBlockHandler::handle);

        // Handle player using grave
        UseBlockCallback.EVENT.register(UseBlockHandler::handleEvent);

        // Give a compass upon death
        ServerPlayerEvents.AFTER_RESPAWN.register(DeathCompass::give);

        // Handle player breaking grave
        PlayerBlockBreakEvents.BEFORE.register(
            (World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) ->
                PlayerBlockBreakHandler.handleBeforeEvent(player, entity)
        );

        // Ensure Resolute Ivy is removed from items in Trinket slots when using Connector + Forge Botania.
        if (FabricLoader.getInstance().isModLoaded("connectormod")) {
            ServerPlayerEvents.AFTER_RESPAWN.register(
                    (ServerPlayerEntity old, ServerPlayerEntity player, boolean alive) -> Trinkets.removeResoluteIvy(player)
            );
        }

        // Needed to override trinket drop behavior
        if (FabricLoader.getInstance().isModLoaded("trinkets"))
            TrinketDropCallback.EVENT.register(
                (TrinketEnums.DropRule rule, ItemStack stack, SlotReference ref, LivingEntity entity) ->
                    TrinketDropHandler.handleTrinketDrop(ref, entity)
            );
    }
}
