package me.mgin.graves.client;

import me.mgin.graves.Graves;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class GravesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(Graves.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
		// Config?
	}
}
