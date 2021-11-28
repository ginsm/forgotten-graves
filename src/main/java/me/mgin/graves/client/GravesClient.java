package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import me.mgin.graves.Graves;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;

public class GravesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(Graves.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
	}
}
