package ui.screens;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import ui.Navigator;
import ui.Screen;
import ui.components.PixelButton;
import ui.components.RoomIllustration;
import ui.theme.PixelTheme;

/**
 * Screen 1: Main Menu. Layout numbers are in design pixels (1280x720)
 * and match the Figma frame. This class only builds the screen; it never
 * holds quiz or game logic.
 */
public class MainMenuPanel extends JPanel {

    private static final int LEFT = 79;
    private static final int BUTTON_W = 383;
    private static final int BUTTON_H = 56;

    private final Navigator navigator;

    public MainMenuPanel(Navigator navigator) {
        this.navigator = navigator;
        setLayout(null);
        setBackground(PixelTheme.CREAM);

        addButton("Play", PixelTheme.MINT, 384, BUTTON_W, () -> navigator.goTo(Screen.PLAYER_SETUP));
        addButton("Question Library", PixelTheme.CREAM, 456, BUTTON_W, () -> navigator.goTo(Screen.LIBRARY));
        addButton("Leaderboard", PixelTheme.CREAM, 528, BUTTON_W, () -> navigator.goTo(Screen.LEADERBOARD));
        addButton("Quit", PixelTheme.RED, 600, 176, navigator::exitGame);

        RoomIllustration room = new RoomIllustration();
        room.setBounds(PixelTheme.px(623), PixelTheme.px(144),
                PixelTheme.px(RoomIllustration.W), PixelTheme.px(RoomIllustration.H));
        add(room);
    }

    private void addButton(String label, Color color, int y, int width, Runnable action) {
        PixelButton button = new PixelButton(label, color);
        button.setBounds(PixelTheme.px(LEFT), PixelTheme.px(y), PixelTheme.px(width), PixelTheme.px(BUTTON_H));
        button.addActionListener(e -> action.run());
        add(button);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);                 // cream background
        Graphics2D g2 = PixelTheme.prepare(g);

        PixelTheme.drawText(g2, "A LITTLE KNOWLEDGE. A LOT OF PURR.", 63, 60,
                PixelTheme.heading(16f), PixelTheme.OUTLINE);
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(63, 87, 1152, 2);            // header rule

        PixelTheme.drawText(g2, "PAWS &", LEFT, 212, PixelTheme.heading(56f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "QUIZZES", LEFT, 292, PixelTheme.heading(56f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Big brains. Tiny paws. Cozy adventures.", LEFT, 346,
                PixelTheme.body(24f), PixelTheme.BROWN);

        PixelTheme.drawText(g2, "A no-pressure quiz quest", 279, 636, PixelTheme.body(18f), PixelTheme.BROWN);
        PixelTheme.drawText(g2, "MAKE FRIENDS. BREAK SHIELDS.", 655, 645,
                PixelTheme.heading(14f), PixelTheme.OUTLINE);

        g2.setColor(PixelTheme.YELLOW);          // little sparkle
        g2.fillRect(1171, 628, 8, 8);
        g2.setColor(PixelTheme.OUTLINE);         // bottom edge line
        g2.fillRect(0, 717, 1280, 3);
        g2.dispose();
    }
}
