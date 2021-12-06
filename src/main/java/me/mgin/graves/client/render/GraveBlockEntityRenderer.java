package me.mgin.graves.client.render;

import me.mgin.graves.api.SkullApi;
import me.mgin.graves.block.GraveBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class GraveBlockEntityRenderer implements BlockEntityRenderer<GraveBlockEntity> {

	private final TextRenderer textRenderer;
	private EntityModelLoader modelLoader;
	private int blockAge = 0;

	public GraveBlockEntityRenderer(Context ctx) {
		super();
		this.modelLoader = ctx.getLayerRenderDispatcher();
		this.textRenderer = ctx.getTextRenderer();
	}

	@Override
	public void render(GraveBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, int overlay) {

		BlockState state = blockEntity.getCachedState();
		blockAge = ((GraveBase) state.getBlock()).getWeathered();
		Direction direction = state.get(Properties.HORIZONTAL_FACING);

		matrices.push();
		matrices.scale(0.75f, 0.75f, 0.75f);
		matrices.translate(0, 0.08f, 0);

		switch (direction) {
			case NORTH :
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
				matrices.translate(-1.2, 0.25 - (blockAge * 0.03), -0.99);
				break;
			case SOUTH :
				matrices.translate(0.15, 0.25 - (blockAge * 0.03), 0.34);
				break;
			case EAST :
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
				matrices.translate(-1.2, 0.25 - (blockAge * 0.03), 0.34);
				break;
			case WEST :
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
				matrices.translate(0.15, 0.25 - (blockAge * 0.03), -0.99);
				break;
			case UP :
			case DOWN :
				break;
		}

		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(50));

		SkullApi.renderSkull(blockEntity, modelLoader, blockAge, state, matrices, light, vertexConsumers);

		matrices.pop();

		// Outline
		if (blockEntity.getGraveOwner() != null
				|| (blockEntity.getCustomName() != null && !blockEntity.getCustomName().isEmpty())) {
			String text = "";

			if (blockEntity.getGraveOwner() != null) {
				text = blockEntity.getGraveOwner().getName();
			} else {
				text = blockEntity.getCustomName().substring(9);
				text = text.substring(0, text.length() - 2);
			}

			// Main Text
			matrices.push();

			int width = this.textRenderer.getWidth(text);

			float scale = (text.length() > 5 ? 0.7F : 0.44F) / width;

			switch (direction) {
				case NORTH :
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
					matrices.translate(-1, 0, -1);
					break;
				case SOUTH :
					break;
				case EAST :
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
					matrices.translate(-1, 0, 0);
					break;
				case WEST :
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
					matrices.translate(0, 0, -1);
					break;
				case UP :
				case DOWN :
					break;
			}

			matrices.translate(0.5, 0, 0.5);
			matrices.translate(0, 0.6, 0.435);
			matrices.scale(-1, -1, 0);
			matrices.scale(scale, scale, scale);
			matrices.translate(-width / 2.0, -4.5, 0);

			this.textRenderer.draw(text, 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers,
					false, 0, light);
			matrices.pop();
		}
	}
}
