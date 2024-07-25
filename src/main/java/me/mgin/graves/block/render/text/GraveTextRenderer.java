package me.mgin.graves.block.render.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraveTextRenderer {
    private final TextRenderer textRenderer;
    private static final int MAX_CHAR_PER_LINE = 15;
    private static final float MAX_TEXT_WIDTH = 65.0f; // The maximum width allowed for the text
    private static final int MAX_LINES = 6; // The  maximum amount of lines
    public static final float TEXT_SCALE = 0.012f;
    public static float TEXT_HEIGHT = 0.7f;

    private final Map<String, List<Text>> cachedLinesMap = new HashMap<>();
    private final Map<String, Float> cachedScaleMap = new HashMap<>();

    public GraveTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public void render(GraveBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                        Direction direction, int light) {
        // Get the grave name
        String text = getGraveName(entity);
        String cacheKey = generateCacheKey(entity, text);

        // Abort rendering if the text length is 0
        if (text.length() == 0) return;

        List<Text> formattedLines;
        float scale;

        // Update cache if necessary
        if (!cachedLinesMap.containsKey(cacheKey)) {
            // Wrap the text
            List<String> lines = wrapText(text);

            // Get the scale and cache it (excludes color codes)
            float maxLineWidth = getMaxLineWidth(lines);
            scale = Math.min(TEXT_SCALE, MAX_TEXT_WIDTH / maxLineWidth * TEXT_SCALE);

            // Format the lines
            GraveTextFormatter formatter = new GraveTextFormatter();
            formattedLines = formatter.formatLines(lines);

            // Cache the formatted lines and scale
            cachedLinesMap.put(cacheKey, formattedLines);
            cachedScaleMap.put(cacheKey, scale);
        } else {
            // Retrieve cached scale/lines
            formattedLines = cachedLinesMap.get(cacheKey);
            scale = cachedScaleMap.get(cacheKey);
        }

        // Render the wrapped text
        renderFormattedLines(formattedLines, matrices, vertexConsumers, direction, light, scale);
    }

    private String generateCacheKey(GraveBlockEntity entity, String text) {
        return entity.getPos().toString() + ":" + text;
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

            // Strings with escaped quotes
            if (customName.startsWith("\"") && customName.endsWith(("\""))) {
                return customName.replace("\"", "");
            }
        }

        // Default to just returning the custom name
        return customName;
    }

    private List<String> wrapText(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int lineLength = 0;

        // Split text by newline characters, accounting for double escaping
        String[] splitText = text.split("\\\\n");

        for (String segment : splitText) {
            // Segments are empty if they're newlines
            if (segment.isEmpty()) {
                if (lines.size() < MAX_LINES) {
                    lines.add(""); // Adds an empty line (effectively adding a newline)
                }
            }  else {
                String[] words = segment.split(" ");

                for (String word : words) {
                    // Strip color codes from the word to calculate its length properly
                    String strippedWord = GraveTextFormatter.stripFormattingCodes(word);
                    int strippedLength = strippedWord.length();

                    // Handles cases where the word overflows the max char per line value
                    if (lineLength + strippedLength > MAX_CHAR_PER_LINE) {
                        // Max lines hasn't been reached, add to next line
                        if (lines.size() < MAX_LINES - 1) {
                            lines.add(line.toString());
                            line = new StringBuilder();
                            lineLength = 0;
                        }
                        // Max lines has been reached, add ellipsis and stop generating lines
                        else {
                            line.append("...");
                            lines.add(line.toString());
                            return lines;
                        }
                    }

                    // Adds spaces between words
                    if (line.length() > 0) {
                        line.append(" ");
                        lineLength++;
                    }

                    line.append(word);
                    lineLength += strippedLength;
                }

                // Adds the current line to lines
                if (lineLength > 0) {
                    // Max lines hasn't been reached, add to next line
                    if (lines.size() < MAX_LINES - 1) {
                        lines.add(line.toString());
                    }
                    // Max lines has been reached, add ellipsis and stop generating lines
                    else {
                        lines.add("...");
                        return lines;
                    }

                    // Reset line and lineLength
                    line = new StringBuilder();
                    lineLength = 0;
                }
            }
        }

        return lines;
    }

    private float getMaxLineWidth(List<String> lines) {
        float maxLineWidth = 0;

        for (String line : lines) {
            // The formatting codes are invisible and shouldn't be included in the max line width
            String strippedLine = GraveTextFormatter.stripFormattingCodes(line);
            float lineWidth = textRenderer.getWidth(strippedLine);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        return maxLineWidth;
    }

    private void renderFormattedLines(List<Text> formattedLines, MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers, Direction direction, int light,
                                      float scale) {
        matrices.push();
        rotateText(direction, matrices, formattedLines);
        matrices.scale(scale, -scale, scale);

        int yOffset = 0;
        for (Text line : formattedLines) {
            float xOffset = -textRenderer.getWidth(line) / 2.0f;
            textRenderer.draw(line, xOffset, yOffset, 0xFFFFFF, false, matrices.peek().getPositionMatrix(),
                vertexConsumers, net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL, 0, light);
            yOffset += 10;
        }

        matrices.pop();
    }


    private void rotateText(Direction direction, MatrixStack matrices, List<Text> lines) {
        float textHeight = TEXT_HEIGHT + (lines.size() * .025f);

        switch (direction) {
            case NORTH:
                matrices.translate(0.5, textHeight, 0.0626);
                break;
            case EAST:
                // 90 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                matrices.translate(0.5f, textHeight, -0.9374f);
                break;
            case SOUTH:
                // 180 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                matrices.translate(-0.5, textHeight, -0.9374f);
                break;
            case WEST:
                // 270 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                matrices.translate(-0.5f, textHeight, 0.0626f);
                break;
            case UP, DOWN:
                break;
        }
    }
}
