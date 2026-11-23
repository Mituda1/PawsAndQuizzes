package ui.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;
import ui.theme.PixelTheme;

/** Card for one quiz topic: icon, name, live question count and a short description. */
public class CategoryCard extends SelectableCard {

    private final String name;
    private final String icon;
    private final String description;
    private final Color artColor;
    private int questionCount;

    public CategoryCard(String name, String icon, String description, Color artColor, int questionCount) {
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.artColor = artColor;
        this.questionCount = questionCount;
    }

    public String getName() {
        return name;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
        repaint();
    }

    @Override
    protected void paintContent(Graphics2D g2, int w, int h) {
        PixelTheme.drawBox(g2, 12, 12, w - 24, 112, artColor, PixelTheme.OUTLINE, 2);
        g2.setFont(PixelTheme.heading(40f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(PixelTheme.OUTLINE);
        g2.drawString(icon, (w - fm.stringWidth(icon)) / 2, 12 + (112 - fm.getHeight()) / 2 + fm.getAscent());

        PixelTheme.drawText(g2, name, 20, 160, PixelTheme.heading(16f), PixelTheme.OUTLINE);
        String count = questionCount + (questionCount == 1 ? " question" : " questions");
        PixelTheme.drawText(g2, count, 20, 188, PixelTheme.body(18f), PixelTheme.BROWN);

        g2.setFont(PixelTheme.body(16f));
        List<String> lines = PixelTheme.wrap(g2.getFontMetrics(), description, w - 40);
        g2.setColor(PixelTheme.BROWN);
        int y = 218;
        for (String line : lines) {
            g2.drawString(line, 20, y);
            y += 22;
        }
    }
}
