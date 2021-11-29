package me.mgin.graves.client.render;

import me.mgin.graves.block.GraveBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class GraveBlockEntityRenderer implements BlockEntityRenderer<GraveBlockEntity> {

	private final TextRenderer textRenderer;
	private SkullBlockEntityModel skull;
	private int blockAge = 0;
	private EntityModelLoader renderLayer;

	public GraveBlockEntityRenderer(Context ctx) {
		super();
		this.renderLayer = ctx.getLayerRenderDispatcher();
		this.textRenderer = ctx.getTextRenderer();
	}

	public SkullBlockEntityModel getSkull(BlockState state) {
		SkullBlockEntityModel skull = new SkullEntityModel(blockAge >= 2
				? renderLayer.getModelPart(EntityModelLayers.SKELETON_SKULL)
				: renderLayer.getModelPart(EntityModelLayers.PLAYER_HEAD));
		skull.setHeadRotation(1f, 2f, 2f);
		return skull;
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
		// matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(10));

		if (blockEntity.getGraveOwner() != null) {
			this.skull = getSkull(state);
			// Make the yaw a configurable value
			float yaw = Float.max(10f, blockAge * 12f);
			if (blockAge >= 2)
				SkullBlockEntityRenderer.renderSkull(null, yaw, 0f, matrices, vertexConsumers, light, skull,
						SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.SKELETON, null));
			else
				SkullBlockEntityRenderer.renderSkull(null, yaw, 0f, matrices, vertexConsumers, light, skull,
						SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.PLAYER, blockEntity.getGraveOwner()));
		}

		matrices.pop();
		// Outline
		if (blockEntity.getGraveOwner() != null) {
			String text = "";
			if (blockEntity.getGraveOwner() != null) {
				text = blockEntity.getGraveOwner().getName();
			} else if (blockEntity.getCustomNametag() != null) {
				if (!blockEntity.getCustomNametag().isEmpty()) {
					text = blockEntity.getCustomNametag().substring(9);
					text = text.substring(0, text.length() - 2);
				}
			}

			// Main Text
			matrices.push();

			int width = this.textRenderer.getWidth(text);

			float scale = 0.7F / width;

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

			this.textRenderer.draw(text, 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0,
					light);
			matrices.pop();
		}
	}
}
