package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import me.mgin.graves.Graves;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;
import net.minecraft.client.render.RenderLayer;

public class GravesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Graves.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
        //Config?
    }
}
