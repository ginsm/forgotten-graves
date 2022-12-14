package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import me.mgin.graves.client.registry.ClientEvents;
import me.mgin.graves.client.registry.ClientReceivers;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;
import me.mgin.graves.registry.GraveBlocks;

public class GravesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);

		ClientEvents.register();
		ClientReceivers.register();
	}
}
