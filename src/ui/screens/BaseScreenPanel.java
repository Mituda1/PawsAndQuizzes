package ui.screens;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JPanel;
import ui.theme.PixelTheme;

/**
 * Shared look for every screen after the menu: the "P&Q" header, the section name,
 * the step counter and the rule under them. Children only draw their own content.
 */
public abstract class BaseScreenPanel extends JPanel {

    protected static final int MARGIN = 63;
    protected static final int CONTENT_WIDTH = 1152;

    private final String sectionLabel;
    private final String stepText;

    protected BaseScreenPanel(String sectionLabel, String stepText) {
        this.sectionLabel = sectionLabel;
        this.stepText = stepText;
        setLayout(null);
        setBackground(PixelTheme.CREAM);
    }

    /** Draws the header, then asks the child to draw its own content. */
    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = PixelTheme.prepare(g);

        PixelTheme.drawText(g2, "P&Q", MARGIN, 60, PixelTheme.heading(16f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, sectionLabel, 150, 60, PixelTheme.heading(12f), PixelTheme.BROWN);
        if (stepText != null && !stepText.isEmpty()) {
            g2.setFont(PixelTheme.heading(12f));
            int width = g2.getFontMetrics().stringWidth(stepText);
            PixelTheme.drawText(g2, stepText, MARGIN + CONTENT_WIDTH - width, 60,
                    PixelTheme.heading(12f), PixelTheme.BROWN);
        }
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 87, CONTENT_WIDTH, 2);

        paintContent(g2);
        g2.dispose();
    }

    /** Draw this screen's own text and shapes. Coordinates are design pixels (1280x720). */
    protected abstract void paintContent(Graphics2D g2);

    /** Adds a component at a design-pixel position and size. */
    protected <T extends JComponent> T place(T component, int x, int y, int w, int h) {
        component.setBounds(PixelTheme.px(x), PixelTheme.px(y), PixelTheme.px(w), PixelTheme.px(h));
        add(component);
        return component;
    }
}
