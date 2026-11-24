package ui.components;

import java.awt.Graphics2D;
import ui.theme.PixelTheme;

/** Small pixel pictures drawn from letters, so no image files are needed. */
public final class PixelIcons {

    private static final String[] HEART = {
        ".XX.XX.",
        "XXXXXXX",
        "XXXXXXX",
        ".XXXXX.",
        "..XXX..",
        "...X..."
    };

    private PixelIcons() { }

    /** Width of one heart, so screens can line several up. */
    public static int heartWidth(int pixel) {
        return HEART[0].length() * pixel;
    }

    /** A full heart is red. An empty heart is a grey outline shape. */
    public static void drawHeart(Graphics2D g2, int x, int y, int pixel, boolean full) {
        g2.setColor(full ? PixelTheme.RED : PixelTheme.DISABLED);
        for (int row = 0; row < HEART.length; row++) {
            for (int col = 0; col < HEART[row].length(); col++) {
                if (HEART[row].charAt(col) == 'X') {
                    g2.fillRect(x + col * pixel, y + row * pixel, pixel, pixel);
                }
            }
        }
    }
}
