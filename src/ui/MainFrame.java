package ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import manager.CatFactory;
import manager.Leaderboard;
import manager.QuestionBank;
import manager.QuizEngine;
import manager.SampleQuestions;
import model.GameSession;
import ui.screens.CategorySelectPanel;
import ui.screens.LeaderboardPanel;
import ui.screens.LibraryPanel;
import ui.screens.MainMenuPanel;
import ui.screens.PlayerSetupPanel;
import ui.screens.QuestionFormPanel;
import ui.screens.QuizPanel;
import ui.screens.ResultsPanel;
import ui.theme.PixelTheme;

/**
 * The one and only window. It creates the shared game data (questions, scores, current run)
 * once and hands it to the screens that need it.
 */
public class MainFrame extends JFrame implements Navigator {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final Map<Screen, JPanel> screens = new EnumMap<>(Screen.class);

    // shared data
    private final QuestionBank questionBank = new QuestionBank();
    private final Leaderboard leaderboard = new Leaderboard();
    private final GameSession session = new GameSession();
    private final QuizEngine quizEngine = new QuizEngine(questionBank);

    public MainFrame() {
        super("Paws & Quizzes");
        SampleQuestions.loadInto(questionBank);

        root.setPreferredSize(new Dimension(
                PixelTheme.px(PixelTheme.DESIGN_W), PixelTheme.px(PixelTheme.DESIGN_H)));

        register(Screen.MAIN_MENU, new MainMenuPanel(this));
        register(Screen.PLAYER_SETUP, new PlayerSetupPanel(this, session, CatFactory.createAll()));
        register(Screen.CATEGORY_SELECT, new CategorySelectPanel(this, session, questionBank));

        register(Screen.GAMEPLAY, new QuizPanel(this, session, quizEngine, leaderboard));
        register(Screen.RESULTS, new ResultsPanel(this, quizEngine, leaderboard));
        register(Screen.LEADERBOARD, new LeaderboardPanel(this, quizEngine, leaderboard));

        QuestionFormPanel questionForm = new QuestionFormPanel(this, questionBank);
        register(Screen.QUESTION_FORM, questionForm);
        register(Screen.LIBRARY, new LibraryPanel(this, questionBank, questionForm));

        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void register(Screen screen, JPanel panel) {
        screens.put(screen, panel);
        root.add(panel, screen.name());
    }

    @Override
    public void goTo(Screen screen) {
        cards.show(root, screen.name());
        JPanel panel = screens.get(screen);
        if (panel instanceof Refreshable) {
            ((Refreshable) panel).refresh();   // after show(), so a screen may send the player somewhere else
        }
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
