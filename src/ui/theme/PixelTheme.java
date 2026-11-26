package ui.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * One place for colors, fonts and drawing helpers.
 * designed on a 1280x720 canvas and scaled down automatically
 * 
 */
public final class PixelTheme {

    public static final int DESIGN_W = 1280;
    public static final int DESIGN_H = 720;

    // Palette from the Figma design
    public static final Color CREAM = new Color(0xFFF4E0);
    public static final Color BROWN = new Color(0x5C3D2E);
    public static final Color PEACH = new Color(0xF7B7A3);
    public static final Color MINT = new Color(0x9BD7B5);
    public static final Color YELLOW = new Color(0xFFD97A);
    public static final Color RED = new Color(0xE86A6A);
    public static final Color OUTLINE = new Color(0x2B1B17);
    public static final Color DARK_MINT = new Color(0x6FB38E);
    public static final Color DISABLED = new Color(0xCDBFA8);

    /** 1.0 on big screens, smaller when the window would not fit. */
    public static final double SCALE = computeScale();

    private static final Font PIXEL_FONT = loadFont("PressStart2P-Regular.ttf");
    private static final Font BODY_FONT = loadFont(
            "PixelifySans-Regular.ttf", "PixelifySans-VariableFont_wght.ttf", "PixelifySans[wght].ttf");

    private PixelTheme() { }

    // ---------- sizes ----------

    /** Convert a design-space number (1280x720) to real screen pixels. */
    public static int px(int designValue) {
        return (int) Math.round(designValue * SCALE);
    }

    /** Convert real screen pixels back to design-space. */
    public static int design(int screenValue) {
        return (int) Math.round(screenValue / SCALE);
    }

    private static double computeScale() {
        try {
            Rectangle usable = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            double byHeight = (usable.height - 48) / (double) DESIGN_H; // 48 = title bar + margin
            double byWidth = (usable.width - 16) / (double) DESIGN_W;
            return Math.max(0.5, Math.min(1.0, Math.min(byHeight, byWidth)));
        } catch (Exception e) {      // headless or unknown screen
            return 1.0;
        }
    }

    // ---------- fonts ----------

    /** Press Start 2P for titles and buttons (falls back to a bold monospace font). */
    public static Font heading(float size) {
        return PIXEL_FONT != null ? PIXEL_FONT.deriveFont(size)
                : new Font(Font.MONOSPACED, Font.BOLD, Math.round(size));
    }

    /** Pixelify Sans for body text (falls back to a plain sans font). */
    public static Font body(float size) {
        return BODY_FONT != null ? BODY_FONT.deriveFont(size)
                : new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(size));
    }

    /** A design-size font made the right size for real Swing components (tables, text boxes). */
    public static Font scaled(Font designFont) {
        return designFont.deriveFont((float) (designFont.getSize2D() * SCALE));
    }

    public static boolean isPixelFontLoaded() {
        return PIXEL_FONT != null;
    }

    private static Font loadFont(String... fileNames) {
        for (String name : fileNames) {
            try (InputStream in = PixelTheme.class.getResourceAsStream("/assets/fonts/" + name)) {
                if (in != null) {
                    return Font.createFont(Font.TRUETYPE_FONT, in);
                }
            } catch (Exception ignored) { }
            File file = new File("src/assets/fonts/" + name);   // when run straight from the project folder
            if (file.exists()) {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, file);
                } catch (Exception ignored) { }
            }
        }
        return null;
    }

    // ---------- drawing helpers ----------

    /** Applies the pixel-art rendering hints (hard edges, crisp text) without scaling. */
    public static void hints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        boolean crisp = isPixelFontLoaded() && SCALE == 1.0;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                crisp ? RenderingHints.VALUE_TEXT_ANTIALIAS_OFF : RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** Copy of the graphics with hard pixel edges, scaled to the design canvas. Dispose it when done. */
    public static Graphics2D prepare(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        hints(g2);
        g2.scale(SCALE, SCALE);
        return g2;
    }

    /** Splits text into lines that fit inside maxWidth (design pixels), using the given font metrics. */
    public static List<String> wrap(FontMetrics fm, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String attempt = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(attempt) > maxWidth && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(attempt);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    /** Flat box with a solid border: the basic building block of every pixel panel and button. */
    public static void drawBox(Graphics2D g2, int x, int y, int w, int h, Color fill, Color border, int borderWidth) {
        g2.setColor(border);
        g2.fillRect(x, y, w, h);
        g2.setColor(fill);
        g2.fillRect(x + borderWidth, y + borderWidth, w - 2 * borderWidth, h - 2 * borderWidth);
    }

    public static void drawText(Graphics2D g2, String text, int x, int baselineY, Font font, Color color) {
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, x, baselineY);
    }

    public static Color darken(Color c, double factor) {
        return new Color(
                (int) (c.getRed() * factor), (int) (c.getGreen() * factor), (int) (c.getBlue() * factor));
    }
}
