package ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import ui.theme.PixelTheme;

/**
 * A button that holds a short list of choices. Each click moves to the next choice
 * (and wraps around), like a dropdown without the popup. Used for filters and form fields.
 */
public class PixelChoiceButton extends PixelButton {

    private List<String> options = new ArrayList<>();
    private int index;
    private Runnable onChange = () -> { };

    public PixelChoiceButton(Color baseColor) {
        super("", baseColor);
        setFont(PixelTheme.heading(12f));
        setTextPadding(16);
        addActionListener(e -> next());
    }

    /** Replaces the choices. Keeps the current choice if it still exists, otherwise goes to the first. */
    public void setOptions(List<String> newOptions) {
        String old = getSelectedOption();
        options = new ArrayList<>(newOptions);
        index = Math.max(0, options.indexOf(old));
        updateText();
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public void next() {
        if (options.isEmpty()) {
            return;
        }
        index = (index + 1) % options.size();
        updateText();
        onChange.run();
    }

    public int getSelectedIndex() {
        return index;
    }

    public void setSelectedIndex(int newIndex) {
        if (newIndex >= 0 && newIndex < options.size()) {
            index = newIndex;
            updateText();
        }
    }

    public String getSelectedOption() {
        return options.isEmpty() ? null : options.get(index);
    }

    public void setSelectedOption(String option) {
        setSelectedIndex(options.indexOf(option));
    }

    private void updateText() {
        setText(options.isEmpty() ? "" : options.get(index));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = PixelTheme.prepare(g);       // small down-arrow on the right
        int w = PixelTheme.design(getWidth());
        int h = PixelTheme.design(getHeight());
        g2.setColor(isEnabled() ? PixelTheme.OUTLINE : PixelTheme.DISABLED);
        g2.fillRect(w - 28, h / 2 - 4, 12, 4);
        g2.fillRect(w - 26, h / 2, 8, 4);
        g2.fillRect(w - 24, h / 2 + 4, 4, 4);
        g2.dispose();
    }
}
