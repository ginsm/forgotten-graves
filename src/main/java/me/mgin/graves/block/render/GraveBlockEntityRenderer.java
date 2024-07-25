package me.mgin.graves.block.render;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.render.text.GraveTextRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class GraveBlockEntityRenderer implements BlockEntityRenderer<GraveBlockEntity> {

    private final GraveSkullRenderer SKULL_RENDERER;
    private final GraveTextRenderer TEXT_RENDERER;

    public GraveBlockEntityRenderer(Context context) {
        this.SKULL_RENDERER = new GraveSkullRenderer(context.getLayerRenderDispatcher());
        this.TEXT_RENDERER = new GraveTextRenderer(context.getTextRenderer());
    }

    @Override
    public void render(GraveBlockEntity graveEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = graveEntity.getCachedState();
        Direction direction = state.get(Properties.HORIZONTAL_FACING);

        // Render skull and text
        SKULL_RENDERER.render(graveEntity, matrices, vertexConsumers, direction, state, light);
        TEXT_RENDERER.render(graveEntity, matrices, vertexConsumers, direction, light);
    }
}
