package ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import ui.theme.PixelTheme;

/**
 * Flat pixel button: 2px dark border, yellow on hover, darker when pressed.
 * Text is left-aligned like the Figma design.
 */
public class PixelButton extends JButton {

    private static final int BORDER = 2;

    private final Color baseColor;
    private int textPadding = 70;

    public PixelButton(String text, Color baseColor) {
        super(text);
        this.baseColor = baseColor;

        setFont(PixelTheme.heading(16f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Space (in design pixels) between the button's left edge and its label. */
    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = PixelTheme.prepare(g);
        int w = PixelTheme.design(getWidth());
        int h = PixelTheme.design(getHeight());
        ButtonModel model = getModel();

        Color fill;
        if (!isEnabled()) {
            fill = PixelTheme.DISABLED;
        } else if (model.isPressed()) {
            fill = PixelTheme.darken(baseColor, 0.85);
        } else if (model.isRollover()) {
            fill = PixelTheme.YELLOW;
        } else {
            fill = baseColor;
        }
        PixelTheme.drawBox(g2, 0, 0, w, h, fill, PixelTheme.OUTLINE, BORDER);

        if (isFocusOwner()) {   // keyboard focus ring
            g2.setColor(PixelTheme.BROWN);
            g2.drawRect(6, 6, w - 13, h - 13);
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int baseline = (h - fm.getHeight()) / 2 + fm.getAscent() + (model.isPressed() ? 2 : 0);
        g2.setColor(PixelTheme.OUTLINE);
        g2.drawString(getText(), textPadding, baseline);
        g2.dispose();
    }
}
