package me.mgin.graves.block.render.text;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraveTextFormatter {
    private Formatting color;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikethrough;
    private boolean obfuscated;

    public void resetFormatting() {
        this.color = Formatting.RESET;
        this.bold = false;
        this.italic = false;
        this.underline = false;
        this.strikethrough = false;
        this.obfuscated = false;
    }

    public List<Text> formatLines(List<String> lines) {
        List<Text> formattedLines = new ArrayList<>();

        for (String line : lines) {
            List<String> splitText = splitByFormattingCode(line);
            Text formattedText = Text.literal("");

            for (String segment : splitText) {
                if (segment.startsWith("&")) {
                    Formatting formatting = getFormatting(segment.charAt(1));
                    if (formatting != null) {
                        addFormatting(formatting);
                    }
                } else {
                    Text textSegment = Text.literal(segment);
                    formattedText = formattedText.copy().append(setFormatting(textSegment));
                }
            }

            formattedLines.add(formattedText);
        }

        return formattedLines;
    }

    public void addFormatting(Formatting formatting) {
        switch (formatting) {
            case BOLD -> this.bold = true;
            case ITALIC -> this.italic = true;
            case UNDERLINE -> this.underline = true;
            case STRIKETHROUGH -> this.strikethrough = true;
            case OBFUSCATED -> this.obfuscated = true;
            case RESET -> resetFormatting();
            default -> this.color = formatting; // Assuming all other cases are color
        }
    }

    public Text setFormatting(Text text) {
        Text result = text;

        if (this.color != null && this.color != Formatting.RESET) {
            result = result.copy().formatted(this.color);
        }
        if (this.bold) {
            result = result.copy().formatted(Formatting.BOLD);
        }
        if (this.italic) {
            result = result.copy().formatted(Formatting.ITALIC);
        }
        if (this.underline) {
            result = result.copy().formatted(Formatting.UNDERLINE);
        }
        if (this.strikethrough) {
            result = result.copy().formatted(Formatting.STRIKETHROUGH);
        }
        if (this.obfuscated) {
            result = result.copy().formatted(Formatting.OBFUSCATED);
        }

        return result;
    }

    public static String stripFormattingCodes(String text) {
        return text.replaceAll("&[a-zA-Z0-9]", "");
    }

    public static List<String> splitByFormattingCode(String text) {
        String pattern = "(&[a-zA-Z0-9])";
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.add(text.substring(lastEnd, matcher.start()));
            }
            result.add(matcher.group());
            lastEnd = matcher.end();
        }
        if (lastEnd < text.length()) {
            result.add(text.substring(lastEnd));
        }

        return result;
    }

    public static Formatting getFormatting(char code) {
        return switch (code) {
            case '0' -> Formatting.BLACK;
            case '1' -> Formatting.DARK_BLUE;
            case '2' -> Formatting.DARK_GREEN;
            case '3' -> Formatting.DARK_AQUA;
            case '4' -> Formatting.DARK_RED;
            case '5' -> Formatting.DARK_PURPLE;
            case '6' -> Formatting.GOLD;
            case '7' -> Formatting.GRAY;
            case '8' -> Formatting.DARK_GRAY;
            case '9' -> Formatting.BLUE;
            case 'a' -> Formatting.GREEN;
            case 'b' -> Formatting.AQUA;
            case 'c' -> Formatting.RED;
            case 'd' -> Formatting.LIGHT_PURPLE;
            case 'e' -> Formatting.YELLOW;
            case 'f' -> Formatting.WHITE;
            case 'k' -> Formatting.OBFUSCATED;
            case 'l' -> Formatting.BOLD;
            case 'm' -> Formatting.STRIKETHROUGH;
            case 'n' -> Formatting.UNDERLINE;
            case 'o' -> Formatting.ITALIC;
            case 'r' -> Formatting.RESET;
            default -> null;
        };
    }
}
