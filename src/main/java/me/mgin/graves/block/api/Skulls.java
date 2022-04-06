package me.mgin.graves.block.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class Skulls {

	/**
	 * <strong>Keys:</strong> Skull item names (i.e. "player_head",
	 * "skeleton_skull")
	 * <p>
	 * <strong>Values:</strong> A wrapper class that contains the SkullBlock.Type
	 * and EntityModelLayer values for each head. You can retrieve the model and
	 * type from the value with .getModel() and .getType() respectively.
	 * <p>
	 * <strong>Note:</strong> "dragon_head" is currently disabled -- the matrices
	 * will need to be adjusted in order for it to render properly.
	 */
	public static Map<String, SkullWrapper> skulls = new HashMap<String, SkullWrapper>() {
		{
			put("wither_skeleton_skull",
					new SkullWrapper(SkullBlock.Type.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_SKULL));

			put("skeleton_skull", new SkullWrapper(SkullBlock.Type.SKELETON, EntityModelLayers.SKELETON_SKULL));

			put("player_head", new SkullWrapper(SkullBlock.Type.PLAYER, EntityModelLayers.PLAYER_HEAD));

			put("zombie_head", new SkullWrapper(SkullBlock.Type.ZOMBIE, EntityModelLayers.ZOMBIE_HEAD));

			put("creeper_head", new SkullWrapper(SkullBlock.Type.CREEPER, EntityModelLayers.CREEPER_HEAD));

			// tentative
			// put("dragon_head", new SkullWrapper(
			// SkullBlock.Type.DRAGON,
			// EntityModelLayers.DRAGON_SKULL
			// ));
		}
	};

	/**
	 * Generates a new SkullBlockEntityModel based on the given model.
	 *
	 * @param model
	 * @return SkullBlockEntityModel
	 */
	public static SkullBlockEntityModel getSkullModel(EntityModelLayer model, EntityModelLoader modelLoader) {
		SkullBlockEntityModel skull = new SkullEntityModel(modelLoader.getModelPart(model));
		skull.setHeadRotation(1f, 2f, 2f);
		return skull;
	}

	/**
	 * Generate a RenderLayer for the given SkullType.
	 *
	 * @param skullType
	 * @param profile
	 * @return
	 */
	public static RenderLayer getSkullLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile) {
		return SkullBlockEntityRenderer.getRenderLayer(skullType, profile);
	}

	/**
	 * Generates a GameProfile with a random UUID and attaches a texture property to
	 * it utilizing the given SkinURL.
	 *
	 * @param skinURL
	 *            - Base64 Skin URL
	 * @return Custom GameProfile
	 */
	public static GameProfile getCustomSkullProfile(String skinURL) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", skinURL));
		return profile;
	}

	/**
	 * This method will use stored data within the GraveBlockEntity to determine
	 * whether it should render the player's head, a custom player head, a basic
	 * head item, or nothing at all.
	 * <p>
	 * You can see how the custom player head or in game player head data is stored
	 * in me.mgin.graves.events.UseBlockHandler.
	 *
	 * @param graveEntity
	 * @param state
	 * @param matrices
	 * @param light
	 * @param vertexConsumers
	 */
	public static void renderSkull(GraveBlockEntity graveEntity, EntityModelLoader modelLoader, int blockAge,
			BlockState state, MatrixStack matrices, int light, VertexConsumerProvider vertexConsumers) {
		GameProfile profile = null;
		SkullWrapper skullData = null;
		float yaw = Float.max(10f, blockAge * 12f);

		if (graveEntity.getGraveOwner() != null) {
			profile = graveEntity.getGraveOwner();
			skullData = Skulls.skulls.get(blockAge >= 2 ? "skeleton_skull" : "player_head");
		}

		else if (graveEntity.hasGraveSkull()) {
			String graveSkull = graveEntity.getGraveSkull();

			if (Skulls.skulls.containsKey(graveSkull)) {
				skullData = Skulls.skulls.get(graveSkull);
			} else {
				profile = getCustomSkullProfile(graveSkull);
				skullData = Skulls.skulls.get("player_head");
			}
		}

		if (skullData != null) {
			SkullBlockEntityRenderer.renderSkull(null, yaw, 0f, matrices, vertexConsumers, light,
					getSkullModel(skullData.getModel(), modelLoader), getSkullLayer(skullData.getType(), profile));
		}
	}

	/**
	 * Wrapper that lets you bundle the type and model for a given skull.
	 */
	public static class SkullWrapper {
		public SkullWrapper(SkullBlock.SkullType type, EntityModelLayer model) {
			this.type = type;
			this.model = model;
		}

		private SkullBlock.SkullType type;
		private EntityModelLayer model;

		public SkullBlock.SkullType getType() {
			return this.type;
		}

		public EntityModelLayer getModel() {
			return this.model;
		}
	}
}
