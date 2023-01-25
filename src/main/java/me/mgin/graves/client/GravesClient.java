package me.mgin.graves.client;

import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.render.GraveBlockEntityRenderer;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.event.ConfigNetworkingEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class GravesClient implements ClientModInitializer {
    public static GravesConfig SERVER_CONFIG = null;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
        ConfigNetworkingEvents.registerClientEvents();
        ConfigNetworking.registerS2CPackets();
    }
}
