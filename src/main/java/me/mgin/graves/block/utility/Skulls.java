package me.mgin.graves.block.utility;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;

import me.mgin.graves.block.entity.GraveBlockEntity;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

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
    public static Map<String, SkullWrapper> skulls = new HashMap<>() {
        {
            put("wither_skeleton_skull",
                new SkullWrapper(SkullBlock.Type.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_SKULL));
            put("skeleton_skull",
                new SkullWrapper(SkullBlock.Type.SKELETON, EntityModelLayers.SKELETON_SKULL));
            put("player_head",
                new SkullWrapper(SkullBlock.Type.PLAYER, EntityModelLayers.PLAYER_HEAD));
            put("zombie_head",
                new SkullWrapper(SkullBlock.Type.ZOMBIE, EntityModelLayers.ZOMBIE_HEAD));
            put("creeper_head",
                new SkullWrapper(SkullBlock.Type.CREEPER, EntityModelLayers.CREEPER_HEAD));
            /*? if >=1.20 {*/
            put("piglin_head",
                new SkullWrapper(SkullBlock.Type.PIGLIN, EntityModelLayers.PIGLIN_HEAD));
            /*?}*/

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
     * @param model EntityModelLayer
     * @param modelLoader EntityModelLoader
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
     * @param skullType SkullType
     * @param profile GameProfile
     * @return RenderLayer
     */
    public static RenderLayer getSkullLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile) {
        return SkullBlockEntityRenderer.getRenderLayer(skullType, profile);
    }

    /**
     * Leverages Minecraft's NbtHelper to create a profile with the appropriate
     * texture, signature, and owner.
     *
     * @return GameProfile
     */
    public static GameProfile getCustomSkullProfile(NbtCompound graveSkull) {
        return NbtHelper.toGameProfile(graveSkull);
    }

    /**
     * This method will use stored data within the GraveBlockEntity to determine
     * whether it should render the player's head, a custom player head, a basic
     * head item, or nothing at all.
     * <p>
     * You can see how the custom player head or in game player head data is stored
     * in me.mgin.graves.events.server.UseBlockHandler.
     *
     * @param graveEntity GraveBlockEntity
     * @param modelLoader EntityModelLoader
     * @param blockAge int
     * @param matrices MatrixStack
     * @param light int
     * @param vertexConsumers VertexConsumerProvider
     */
    public static void renderSkull(GraveBlockEntity graveEntity, EntityModelLoader modelLoader, int blockAge,
                                   MatrixStack matrices, int light, VertexConsumerProvider vertexConsumers) {
        GameProfile profile = null;
        SkullWrapper skullData = null;
        float yaw = Float.max(10f, blockAge * 12f);

        // Handle player-owned grave skulls
        if (graveEntity.getGraveOwner() != null) {
            profile = graveEntity.getGraveOwner();
            skullData = Skulls.skulls.get(blockAge >= 2 ? "skeleton_skull" : "player_head");
        }

        // Handle custom grave skulls
        else if (graveEntity.hasGraveSkull()) {
            NbtCompound graveSkull = graveEntity.getGraveSkull();
            String graveSkullValue = graveSkull.getString("Value");

            // Handle non-custom heads (like skele, wither skele, zombie, creeper, etc)
            // This is set to the item name in Skull.java's handle method.
            if (Skulls.skulls.containsKey(graveSkullValue)) {
                skullData = Skulls.skulls.get(graveSkullValue);
            }

            // Handle custom heads (creates a custom profile)
            else {
                profile = getCustomSkullProfile(graveSkull);
                skullData = Skulls.skulls.get("player_head");
            }
        }

        // Render the skull
        if (skullData != null) {
            SkullBlockEntityModel model = getSkullModel(skullData.model(), modelLoader);
            RenderLayer layer = getSkullLayer(skullData.type(), profile);

            SkullBlockEntityRenderer.renderSkull(
                null, yaw, 0f, matrices, vertexConsumers, light, model, layer
            );
        }
    }

    /**
     * Wrapper that lets you bundle the type and model for a given skull.
     */
    public record SkullWrapper(SkullBlock.SkullType type, EntityModelLayer model) { }
}
