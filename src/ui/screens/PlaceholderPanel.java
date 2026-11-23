package ui.screens;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Supplier;
import javax.swing.JPanel;
import ui.Navigator;
import ui.Screen;
import ui.components.PixelButton;
import ui.theme.PixelTheme;

/** Stand-in for screens you have not built yet. Delete it once every screen is real. */
public class PlaceholderPanel extends JPanel {

    private final String title;
    private final Supplier<String> detail;

    public PlaceholderPanel(String title, Navigator navigator) {
        this(title, navigator, null);
    }

    /** The optional detail line is asked for every time the panel is drawn (handy for showing the session). */
    public PlaceholderPanel(String title, Navigator navigator, Supplier<String> detail) {
        this.title = title;
        this.detail = detail;
        setLayout(null);
        setBackground(PixelTheme.CREAM);

        PixelButton back = new PixelButton("Back", PixelTheme.PEACH);
        back.setBounds(PixelTheme.px(79), PixelTheme.px(600), PixelTheme.px(176), PixelTheme.px(56));
        back.addActionListener(e -> navigator.goTo(Screen.MAIN_MENU));
        add(back);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = PixelTheme.prepare(g);
        PixelTheme.drawText(g2, title.toUpperCase(), 79, 200, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Coming soon: build this screen next.", 79, 250,
                PixelTheme.body(24f), PixelTheme.BROWN);
        if (detail != null) {
            PixelTheme.drawText(g2, detail.get(), 79, 320, PixelTheme.body(24f), PixelTheme.OUTLINE);
        }
        g2.dispose();
    }
}
