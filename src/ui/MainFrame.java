package ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ui.screens.MainMenuPanel;
import ui.screens.PlaceholderPanel;
import ui.theme.PixelTheme;

/** The one and only window. Swaps screens with a CardLayout. */
public class MainFrame extends JFrame implements Navigator {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public MainFrame() {
        super("Paws & Quizzes");

        root.setPreferredSize(new Dimension(
                PixelTheme.px(PixelTheme.DESIGN_W), PixelTheme.px(PixelTheme.DESIGN_H)));

        root.add(new MainMenuPanel(this), Screen.MAIN_MENU.name());

        // Temporary screens. Replace each one with the real panel as you build it.
        root.add(new PlaceholderPanel("Player Setup", this), Screen.PLAYER_SETUP.name());
        root.add(new PlaceholderPanel("Question Library", this), Screen.LIBRARY.name());
        root.add(new PlaceholderPanel("Leaderboard", this), Screen.LEADERBOARD.name());

        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void goTo(Screen screen) {
        cards.show(root, screen.name());
    }

    @Override
    public void exitGame() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Quit Paws & Quizzes?", "Quit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
