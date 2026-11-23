package ui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import model.Cat;
import ui.theme.PixelTheme;

/**
 * Draws a sitting cat from a 16x16 grid of letters, using the colors the Cat reports.
 * One shape, many cats: swap this for real sprite images when you choose your asset pack.
 */
public final class CatSprite {

    public static final int SIZE = 16;

    // K = outline, P = fur, D = ears and paws, E = eyes, R = nose, . = empty
    private static final String[] ROWS = {
        "..KK........KK..",
        "..KDK......KDK..",
        "..KDDK....KDDK..",
        "..KPPPKKKKPPPK..",
        ".KPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPK.",
        ".KPPEEPPPPEEPPK.",
        ".KPPEEPPPPEEPPK.",
        ".KPPPPPDDPPPPPK.",
        ".KPPPPPRRPPPPPK.",
        "..KPPPPPPPPPPK..",
        "..KPPPPPPPPPPK..",
        ".KPPPKPPPPKPPPK.",
        ".KDDDKPPPPKDDDK.",
        ".KKKKKKKKKKKKKK.",
        "................"
    };

    static {
        if (ROWS.length != SIZE) {
            throw new IllegalStateException("Cat sprite must have " + SIZE + " rows");
        }
        for (String row : ROWS) {
            if (row.length() != SIZE) {
                throw new IllegalStateException("Cat sprite row has wrong length: " + row);
            }
        }
    }

    private CatSprite() { }

    /** Draws the cat with its top-left corner at (x, y). Each sprite pixel becomes pixelSize screen pixels. */
    public static void draw(Graphics2D g2, Cat cat, int x, int y, int pixelSize) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Color c = colorFor(ROWS[row].charAt(col), cat);
                if (c != null) {
                    g2.setColor(c);
                    g2.fillRect(x + col * pixelSize, y + row * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }

    private static Color colorFor(char symbol, Cat cat) {
        switch (symbol) {
            case 'K': return PixelTheme.OUTLINE;
            case 'P': return new Color(cat.getFurRgb());
            case 'D': return new Color(cat.getPointsRgb());
            case 'E': return new Color(cat.getEyeRgb());
            case 'R': return PixelTheme.RED;
            default: return null;
        }
    }
}
