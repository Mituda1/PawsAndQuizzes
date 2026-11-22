package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import ui.theme.PixelTheme;

/**
 * Placeholder art for the main menu: a cozy room with a sitting cat.
 * Everything is drawn with rectangles, so no image files are needed yet.
 * Later, replace paintComponent with drawImage() calls using your asset pack sprites.
 */
public class RoomIllustration extends JPanel {

    public static final int W = 577;
    public static final int H = 455;

    private static final int FLOOR_Y = 281;
    private static final int CAT_PIXEL = 9;

    // K = outline, P = fur, R = nose/tongue, . = empty
    private static final String[] CAT = {
        "..KK........KK..",
        "..KPK......KPK..",
        "..KPPK....KPPK..",
        "..KPPPKKKKPPPK..",
        ".KPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPK.",
        ".KPPKKPPPPKKPPK.",
        ".KPPKKPPPPKKPPK.",
        ".KPPPPPRRPPPPPK.",
        ".KPPPPPPRPPPPPK.",
        "..KPPPPPPPPPPK..",
        "..KPPPPPPPPPPK..",
        ".KPPPKPPPPKPPPK.",
        ".KPPPKPPPPKPPPK.",
        ".KKKKKKKKKKKKKK.",
        "................"
    };

    // The scene is drawn 1:1 into this image, then scaled once with nearest-neighbor.
    // This avoids thin gaps between pixels when the window is scaled down.
    private final BufferedImage scene = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);

    private boolean bob;                 // tiny idle animation
    private final Timer idleTimer = new Timer(600, e -> {
        bob = !bob;
        repaint();
    });

    public RoomIllustration() {
        setOpaque(false);
        setPreferredSize(new Dimension(PixelTheme.px(W), PixelTheme.px(H)));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        idleTimer.start();
    }

    @Override
    public void removeNotify() {
        idleTimer.stop();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawScene();
        Graphics2D screen = (Graphics2D) g.create();
        screen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        screen.drawImage(scene, 0, 0, getWidth(), getHeight(), null);
        screen.dispose();
    }

    private void drawScene() {
        Graphics2D g2 = scene.createGraphics();
        g2.setClip(0, 0, W, H);

        drawWall(g2);
        drawFloor(g2);
        drawWindow(g2);
        drawSofa(g2);
        drawRug(g2);
        drawCat(g2, 256, 238 + (bob ? 4 : 0));

        // frame
        g2.setClip(null);
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(0, 0, W, 3);
        g2.fillRect(0, H - 3, W, 3);
        g2.fillRect(0, 0, 3, H);
        g2.fillRect(W - 3, 0, 3, H);
        g2.dispose();
    }

    private void drawWall(Graphics2D g2) {
        g2.setColor(PixelTheme.PEACH);
        g2.fillRect(0, 0, W, FLOOR_Y);
        g2.setColor(PixelTheme.CREAM);          // picture rail
        g2.fillRect(0, 48, W, 6);
    }

    private void drawFloor(Graphics2D g2) {
        g2.setColor(PixelTheme.YELLOW);
        g2.fillRect(0, FLOOR_Y, W, H - FLOOR_Y);

        g2.setColor(PixelTheme.OUTLINE);
        int rowHeight = 36;
        int row = 0;
        for (int y = FLOOR_Y; y < H; y += rowHeight, row++) {
            g2.fillRect(0, y, W, 2);
            for (int x = (row % 2 == 0) ? 0 : 48; x < W; x += 96) {
                g2.fillRect(x, y, 2, rowHeight);
            }
        }
        g2.fillRect(0, FLOOR_Y - 2, W, 4);       // wall / floor edge
    }

    private void drawWindow(Graphics2D g2) {
        int x = 312, y = 40, w = 176, h = 160;
        PixelTheme.drawBox(g2, x, y, w, h, PixelTheme.MINT, PixelTheme.OUTLINE, 3);
        g2.setColor(PixelTheme.BROWN);                          // thick frame
        g2.fillRect(x + 3, y + 3, w - 6, 6);
        g2.fillRect(x + 3, y + h - 9, w - 6, 6);
        g2.fillRect(x + 3, y + 3, 6, h - 6);
        g2.fillRect(x + w - 9, y + 3, 6, h - 6);
        g2.setColor(PixelTheme.CREAM);                          // cross bars
        g2.fillRect(x + w / 2 - 4, y + 9, 8, h - 18);
        g2.fillRect(x + 9, y + h / 2 - 4, w - 18, 8);
    }

    private void drawSofa(Graphics2D g2) {
        PixelTheme.drawBox(g2, 50, 160, 213, 73, PixelTheme.MINT, PixelTheme.DARK_MINT, 3);  // back
        PixelTheme.drawBox(g2, 33, 223, 207, 64, PixelTheme.MINT, PixelTheme.DARK_MINT, 3);  // seat
        PixelTheme.drawBox(g2, 65, 176, 78, 63, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);    // pillow
        PixelTheme.drawBox(g2, 161, 176, 78, 63, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);   // pillow
    }

    private void drawRug(Graphics2D g2) {
        PixelTheme.drawBox(g2, 128, 321, 318, 94, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);
        g2.setColor(PixelTheme.PEACH);
        g2.fillRect(144, 338, 288, 5);
    }

    private void drawCat(Graphics2D g2, int left, int top) {
        for (int row = 0; row < CAT.length; row++) {
            for (int col = 0; col < CAT[row].length(); col++) {
                Color c = colorFor(CAT[row].charAt(col));
                if (c != null) {
                    g2.setColor(c);
                    g2.fillRect(left + col * CAT_PIXEL, top + row * CAT_PIXEL, CAT_PIXEL, CAT_PIXEL);
                }
            }
        }
    }

    private Color colorFor(char symbol) {
        switch (symbol) {
            case 'K': return PixelTheme.OUTLINE;
            case 'P': return PixelTheme.PEACH;
            case 'R': return PixelTheme.RED;
            default: return null;
        }
    }
}
