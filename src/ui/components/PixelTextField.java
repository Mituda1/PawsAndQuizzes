package ui.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import ui.theme.PixelTheme;

/** Flat pixel text box: dark border, mint border while typing, red border when it has an error. */
public class PixelTextField extends JTextField {

    private static final Color FILL = new Color(0xFFFAF0);
    private static final Color PLACEHOLDER = new Color(0x9A8A7A);

    private boolean error;
    private String placeholder = "";

    public PixelTextField(int maxLength) {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, PixelTheme.px(16), 0, PixelTheme.px(16)));
        setFont(PixelTheme.scaled(PixelTheme.body(24f)));
        setForeground(PixelTheme.OUTLINE);
        setCaretColor(PixelTheme.OUTLINE);
        setSelectionColor(PixelTheme.YELLOW);
        setSelectedTextColor(PixelTheme.OUTLINE);

        // Stops typing (and pasting) once the text would be longer than maxLength.
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String safe = text == null ? "" : text;
                if (fb.getDocument().getLength() - length + safe.length() <= maxLength) {
                    super.replace(fb, offset, length, safe, attrs);
                }
            }
        });
    }

    public boolean hasError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
        repaint();
    }

    /** Grey hint text shown while the box is empty. */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D box = PixelTheme.prepare(g);
        int w = PixelTheme.design(getWidth());
        int h = PixelTheme.design(getHeight());
        if (error) {
            PixelTheme.drawBox(box, 0, 0, w, h, FILL, PixelTheme.RED, 4);
        } else if (isFocusOwner()) {
            PixelTheme.drawBox(box, 0, 0, w, h, FILL, PixelTheme.DARK_MINT, 4);
        } else {
            PixelTheme.drawBox(box, 0, 0, w, h, FILL, PixelTheme.OUTLINE, 2);
        }
        box.dispose();

        Graphics2D text = (Graphics2D) g.create();
        PixelTheme.hints(text);
        super.paintComponent(text);      // the typed text and the blinking cursor
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            text.setFont(getFont());
            text.setColor(PLACEHOLDER);
            FontMetrics fm = text.getFontMetrics();
            text.drawString(placeholder, getInsets().left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        }
        text.dispose();
    }
}
