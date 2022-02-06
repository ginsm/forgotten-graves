package me.mgin.graves.registry;

import com.mojang.authlib.GameProfile;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.mgin.graves.Graves;
import me.mgin.graves.events.PlayerBlockBreakHandler;
import me.mgin.graves.events.TrinketDropHandler;
import me.mgin.graves.events.UseBlockHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerEvents {
	public static void register() {

		PlayerBlockBreakEvents.BEFORE.register((World world, PlayerEntity player, BlockPos pos, BlockState state,
				BlockEntity entity) -> PlayerBlockBreakHandler.handleBeforeEvent(player, pos, entity));

		UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand,
				BlockHitResult hitResult) -> UseBlockHandler.handleEvent(player, world, hand, hitResult));

		// Remove client configs on disconnect
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			GameProfile profile = handler.player.getGameProfile();
			Graves.clientConfigs.remove(profile);
		});

		if (FabricLoader.getInstance().isModLoaded("trinkets"))
			TrinketDropCallback.EVENT.register((DropRule rule, ItemStack stack, SlotReference ref,
					LivingEntity entity) -> TrinketDropHandler.handleTrinketDrop(rule, stack, ref, entity));

	}
}
