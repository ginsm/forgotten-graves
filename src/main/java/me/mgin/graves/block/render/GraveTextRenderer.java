package me.mgin.graves.block.render;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.List;

public class GraveTextRenderer {
    private final TextRenderer textRenderer;
    private static final int MAX_CHAR_PER_LINE = 15;
    private static final float MAX_TEXT_WIDTH = 65.0f; // The maximum width allowed for the text
    public static final float TEXT_SCALE = 0.012f;
    public static final float TEXT_HEIGHT = 0.7f;

    public GraveTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public void render(GraveBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                        Direction direction, int light) {
        // Get the grave name
        String text = getGraveName(entity);

        // Abort rendering if the text length is 0
        if (text.length() == 0) return;

        // Wrap the text
        List<String> lines = wrapText(text);

        // Get the scale
        float maxLineWidth = getMaxLineWidth(lines);
        float scale = Math.min(TEXT_SCALE, MAX_TEXT_WIDTH / maxLineWidth * TEXT_SCALE);

        // Render the wrapped text
        renderWrappedLines(lines, matrices, vertexConsumers, direction, scale, light);
    }

    private String getGraveName(GraveBlockEntity graveEntity) {
        boolean hasOwner = graveEntity.getGraveOwner() != null;
        boolean hasCustomName = graveEntity.getCustomName() != null;
        boolean customNameNotEmpty = !graveEntity.getCustomName().isEmpty();

        // Handle owned graves
        if (hasOwner) {
            return graveEntity.getGraveOwner().getName();
        }

        // Get the grave's custom name
        String customName = graveEntity.getCustomName();

        // Handle different custom name formats
        if (hasCustomName && customNameNotEmpty) {
            // String-ified NBT Names
            if (customName.contains("\"text\":")) {
                try {
                    return StringNbtReader.parse(customName).getString("text");
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            // Quoted Names
            if (customName.startsWith("\"") && customName.endsWith(("\""))) {
                return customName.substring(1, customName.length() - 1);
            }
        }

        // Default to just returning the custom name
        return customName;
    }

    private List<String> wrapText(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int lineLength = 0;

        for (String word : text.split(" ")) {
            if (lineLength + word.length() > MAX_CHAR_PER_LINE) {
                lines.add(line.toString());
                line = new StringBuilder();
                lineLength = 0;
            }
            if (lineLength > 0) {
                line.append(" ");
                lineLength++;
            }
            line.append(word);
            lineLength += word.length();
        }

        if (lineLength > 0) {
            lines.add(line.toString());
        }

        return lines;
    }

    private float getMaxLineWidth(List<String> lines) {
        float maxLineWidth = 0;

        for (String line : lines) {
            float lineWidth = textRenderer.getWidth(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        return maxLineWidth;
    }

    private void renderWrappedLines(List<String> lines, MatrixStack matrices, VertexConsumerProvider vertexConsumer,
                                    Direction direction, float scale,
                                    int light) {
        matrices.push();

        // Rotate the text based on direction and set scale
        rotateText(direction, matrices);
        matrices.scale(scale, -scale, scale);

        // Draw each line
        int yOffset = 0;
        for (String line : lines) {
            float xOffset = -textRenderer.getWidth(line) / 2.0f;
            textRenderer.draw(line, xOffset, yOffset, 0xFFFFFF, false, matrices.peek().getPositionMatrix(),
                vertexConsumer,
                net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL, 0, light);
            yOffset += 10; // Adjust line spacing as needed
        }

        matrices.pop();
    }

    private void rotateText(Direction direction, MatrixStack matrices) {
        switch (direction) {
            case NORTH:
                matrices.translate(0.5, TEXT_HEIGHT, 0.0626);
                break;
            case EAST:
                // 90 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                matrices.translate(0.5f, TEXT_HEIGHT, -0.9374f);
                break;
            case SOUTH:
                // 180 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                matrices.translate(-0.5, TEXT_HEIGHT, -0.9374f);
                break;
            case WEST:
                // 270 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                matrices.translate(-0.5f, TEXT_HEIGHT, 0.0626f);
                break;
            case UP, DOWN:
                break;
        }
    }
}
