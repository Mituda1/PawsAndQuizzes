package ui.screens;

import java.awt.Graphics2D;
import manager.Leaderboard;
import manager.QuizEngine;
import model.PlayerScore;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.LeaderboardTableModel;
import ui.components.PixelButton;
import ui.theme.PixelTheme;

/** Shown when a run ends. It reads the finished score from the engine and the rank from the leaderboard. */
public class ResultsPanel extends BaseScreenPanel implements Refreshable {

    private final QuizEngine engine;
    private final Leaderboard leaderboard;
    private PlayerScore score;

    public ResultsPanel(Navigator navigator, QuizEngine engine, Leaderboard leaderboard) {
        super("RESULTS", "");
        this.engine = engine;
        this.leaderboard = leaderboard;

        PixelButton menu = new PixelButton("Main Menu", PixelTheme.CREAM);
        menu.setCenterText(true);
        menu.addActionListener(e -> navigator.goTo(Screen.MAIN_MENU));
        place(menu, MARGIN, 636, 224, 48);

        PixelButton board = new PixelButton("Leaderboard", PixelTheme.PEACH);
        board.setCenterText(true);
        board.addActionListener(e -> navigator.goTo(Screen.LEADERBOARD));
        place(board, 751, 636, 224, 48);

        PixelButton again = new PixelButton("Play Again", PixelTheme.MINT);
        again.setCenterText(true);
        again.addActionListener(e -> navigator.goTo(Screen.CATEGORY_SELECT));
        place(again, MARGIN + CONTENT_WIDTH - 224, 636, 224, 48);
    }

    @Override
    public void refresh() {
        score = engine.getLastScore();
        repaint();
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
        if (score == null) {
            return;
        }

        String name = score.getPlayerName().toUpperCase();
        String title = engine.isGameOver() ? "GOOD TRY, " + name + "!" : "NICE WORK, " + name + "!";
        PixelTheme.drawText(g2, title, MARGIN, 160, PixelTheme.heading(32f), PixelTheme.OUTLINE);

        String subtitle = engine.isGameOver()
                ? "You ran out of hearts. Every run teaches a little something."
                : "You broke " + engine.getCorrectCount() + " of " + engine.getTotalWaves() + " shields.";
        PixelTheme.drawText(g2, subtitle, MARGIN, 204, PixelTheme.body(24f), PixelTheme.BROWN);

        int rank = leaderboard.rankOf(score);
        String rankText = rank < 0 ? "-" : "#" + rank + " of " + leaderboard.size();

        stat(g2, 0, 0, "SCORE", String.format("%,d", score.getScore()));
        stat(g2, 1, 0, "ACCURACY", String.format("%.0f%%", score.getAccuracyPercent()));
        stat(g2, 2, 0, "CORRECT", score.getCorrectAnswers() + " / " + score.getTotalAnswered());
        stat(g2, 0, 1, "TIME", LeaderboardTableModel.formatTime(score.getTimeSeconds()));
        stat(g2, 1, 1, "CAT", score.getCatName());
        stat(g2, 2, 1, "RANK", rankText);
    }

    private void stat(Graphics2D g2, int column, int row, String label, String value) {
        int x = MARGIN + column * (360 + 36);
        int y = 244 + row * 172;
        PixelTheme.drawBox(g2, x, y, 360, 140, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);
        PixelTheme.drawText(g2, label, x + 24, y + 44, PixelTheme.heading(12f), PixelTheme.BROWN);
        PixelTheme.drawText(g2, value, x + 24, y + 106, PixelTheme.heading(32f), PixelTheme.OUTLINE);
    }
}
