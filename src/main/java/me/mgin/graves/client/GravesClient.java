package me.mgin.graves.client;

import me.mgin.graves.networking.ConfigNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import me.mgin.graves.networking.event.ConfigNetworkingEvents;
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
