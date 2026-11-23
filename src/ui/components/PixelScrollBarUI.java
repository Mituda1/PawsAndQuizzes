package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import ui.theme.PixelTheme;

/** Flat scroll bar: a brown block on a tan track, with no arrow buttons. */
public class PixelScrollBarUI extends BasicScrollBarUI {

    private static final Color TRACK = new Color(0xF0DDB8);

    @Override
    protected void configureScrollBarColors() {
        thumbColor = PixelTheme.BROWN;
        trackColor = TRACK;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return emptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return emptyButton();
    }

    private JButton emptyButton() {
        JButton button = new JButton();
        Dimension none = new Dimension(0, 0);
        button.setPreferredSize(none);
        button.setMinimumSize(none);
        button.setMaximumSize(none);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
        g.setColor(TRACK);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
        g.setColor(PixelTheme.OUTLINE);
        g.fillRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        g.setColor(PixelTheme.BROWN);
        g.fillRect(bounds.x + 4, bounds.y + 4, bounds.width - 8, bounds.height - 8);
    }
}
