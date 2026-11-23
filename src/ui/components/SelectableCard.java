package ui.components;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import ui.theme.PixelTheme;

/**
 * A clickable card with a pixel border. It handles hover, focus, keyboard and the selected ring.
 * Children only describe what goes inside the card by overriding paintContent.
 */
public abstract class SelectableCard extends JComponent {

    private boolean selected;
    private boolean hovered;
    private Runnable onSelect = () -> { };

    protected SelectableCard() {
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                select();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        AbstractAction pick = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                select();
            }
        };
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pick");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "pick");
        getActionMap().put("pick", pick);
    }

    /** What to do when the player picks this card (by mouse or keyboard). */
    public void setOnSelect(Runnable onSelect) {
        this.onSelect = onSelect;
    }

    /** Called when the player picks this card. */
    public void select() {
        onSelect.run();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    /** Draw the inside of the card. Coordinates are design pixels, w and h are the card size. */
    protected abstract void paintContent(Graphics2D g2, int w, int h);

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = PixelTheme.prepare(g);
        int w = PixelTheme.design(getWidth());
        int h = PixelTheme.design(getHeight());

        PixelTheme.drawBox(g2, 0, 0, w, h, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);
        paintContent(g2, w, h);

        if (selected || hovered) {
            g2.setColor(selected ? PixelTheme.DARK_MINT : PixelTheme.YELLOW);
            g2.fillRect(2, 2, w - 4, 4);
            g2.fillRect(2, h - 6, w - 4, 4);
            g2.fillRect(2, 2, 4, h - 4);
            g2.fillRect(w - 6, 2, 4, h - 4);
        }
        if (isFocusOwner()) {
            g2.setColor(PixelTheme.BROWN);
            g2.drawRect(8, 8, w - 17, h - 17);
        }
        g2.dispose();
    }
}
