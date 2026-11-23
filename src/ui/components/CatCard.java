package ui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import model.Cat;
import ui.theme.PixelTheme;

/** Card for one playable cat: sprite, name, breed and perk. */
public class CatCard extends SelectableCard {

    private final Cat cat;
    private final Color artColor;

    public CatCard(Cat cat, Color artColor) {
        this.cat = cat;
        this.artColor = artColor;
    }

    public Cat getCat() {
        return cat;
    }

    @Override
    protected void paintContent(Graphics2D g2, int w, int h) {
        PixelTheme.drawBox(g2, 12, 12, w - 24, 104, artColor, PixelTheme.OUTLINE, 2);
        int pixel = 6;
        CatSprite.draw(g2, cat, (w - CatSprite.SIZE * pixel) / 2, 16, pixel);

        PixelTheme.drawText(g2, cat.getName(), 20, 146, PixelTheme.heading(16f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, cat.getBreed(), 20, 168, PixelTheme.body(18f), PixelTheme.BROWN);

        PixelTheme.drawBox(g2, 20, 178, w - 40, 28, PixelTheme.YELLOW, PixelTheme.OUTLINE, 2);
        PixelTheme.drawText(g2, cat.getPerkDescription(), 28, 198, PixelTheme.body(16f), PixelTheme.OUTLINE);
    }
}
