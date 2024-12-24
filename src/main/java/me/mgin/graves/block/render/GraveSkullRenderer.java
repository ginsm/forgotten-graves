package me.mgin.graves.block.render;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.render.packs.GraveResourcePack;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;

public class GraveSkullRenderer {
    private final EntityModelLoader skullRenderer;

    public GraveSkullRenderer(EntityModelLoader modelLoader) {
        this.skullRenderer = modelLoader;
    }

    public void render(GraveBlockEntity graveEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       Direction direction, BlockState state, int light) {
        int decayStage = ((GraveBlockBase) state.getBlock()).getDecayStage().ordinal();

        matrices.push();

        // Set scale and raise the skull up 1/16th of a block
        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.translate(0, 0.08f, 0);

        // Rotate the skull based on the direction
        rotateSkull(direction, matrices, decayStage);

        // render the skull
        renderSkull(graveEntity, skullRenderer, decayStage, matrices, light, vertexConsumers);

        matrices.pop();
    }

    private void rotateSkull(Direction direction, MatrixStack matrices, int decayStage) {
        GraveResourcePack pack = GraveResourcePackManager.getActivePack();

        switch (direction) {
            case NORTH:
                // 180 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                matrices.translate(-1.2, 0.25 - (decayStage * 0.03), -0.99 + pack.getSkullOffset());
                break;
            case SOUTH:
                matrices.translate(0.15, 0.25 - (decayStage * 0.03), 0.34 + pack.getSkullOffset());
                break;
            case EAST:
                // 90 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                matrices.translate(-1.2, 0.25 - (decayStage * 0.03), 0.34 + pack.getSkullOffset());
                break;
            case WEST:
                // 270 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                matrices.translate(0.15, 0.25 - (decayStage * 0.03), -0.99 + pack.getSkullOffset());
                break;
            case UP:
            case DOWN:
                break;
        }

        // 50 deg (X)
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(0.872665f));
    }

    public static void renderSkull(GraveBlockEntity graveEntity, EntityModelLoader modelLoader, int blockAge,
                                   MatrixStack matrices, int light, VertexConsumerProvider vertexConsumers) {
        GameProfile profile = null;
        SkullWrapper skullData = null;
        float yaw = Float.max(10f, blockAge * 12f);

        // Handle player-owned grave skulls
        if (graveEntity.getGraveOwner() != null) {
            profile = graveEntity.getGraveOwner();
            skullData = skulls.get(blockAge >= 2 ? "skeleton_skull" : "player_head");
        }

        // Handle custom grave skulls
        else if (graveEntity.hasGraveSkull()) {
            NbtCompound graveSkull = graveEntity.getGraveSkull();
            String graveSkullValue = graveSkull.getString("Value");

            // Handle non-custom heads (like skeleton, wither skeleton, zombie, creeper, etc).
            // This is set to the item name in Skull.java's handle method.
            if (skulls.containsKey(graveSkullValue)) {
                skullData = skulls.get(graveSkullValue);
            }

            // Handle custom heads (creates a custom profile)
            else {
                profile = getCustomSkullProfile(graveSkull);
                skullData = skulls.get("player_head");
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
     * Contains information about the skull and its model layers.
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
            //? if >=1.20 {
            put("piglin_head",
                new SkullWrapper(SkullBlock.Type.PIGLIN, EntityModelLayers.PIGLIN_HEAD));
            //?}
        }
    };

    /**
     * Generates a new SkullBlockEntityModel based on the given model.
     */
    public static SkullBlockEntityModel getSkullModel(EntityModelLayer model, EntityModelLoader modelLoader) {
        SkullBlockEntityModel skull = new SkullEntityModel(modelLoader.getModelPart(model));
        skull.setHeadRotation(1f, 2f, 2f);
        return skull;
    }

    /**
     * Generate a RenderLayer for the given SkullType.
     */
    public static RenderLayer getSkullLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile) {
        return SkullBlockEntityRenderer.getRenderLayer(skullType, profile);
    }

    /**
     * Leverages Minecraft's NbtHelper to create a profile with the appropriate
     * texture, signature, and owner.
     */
    public static GameProfile getCustomSkullProfile(NbtCompound graveSkull) {
        return NbtHelper.toGameProfile(graveSkull);
    }

    /**
     * Wrapper that lets you bundle the type and model for a given skull.
     */
    public record SkullWrapper(SkullBlock.SkullType type, EntityModelLayer model) { }
}
