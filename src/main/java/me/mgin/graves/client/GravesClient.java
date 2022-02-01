package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import me.mgin.graves.client.commands.ReloadClientCommand;
import me.mgin.graves.client.registry.ClientEvents;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;
import me.mgin.graves.registry.GraveBlocks;

public class GravesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);

		ClientCommandManager.DISPATCHER.register(
			ClientCommandManager.literal("reloadgraves")
					.executes(context -> ReloadClientCommand.execute(context))
		);

		ClientEvents.register();
	}
}
