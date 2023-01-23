package me.mgin.graves.client;

import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.ConfigNetworkingEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import me.mgin.graves.networking.config.event.ConfigNetworkingEvents;
import me.mgin.graves.block.render.GraveBlockEntityRenderer;
import me.mgin.graves.block.GraveBlocks;

public class GravesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
        ConfigNetworkingEvents.registerClientEvents();
        ConfigNetworking.registerS2CPackets();
    }
}
