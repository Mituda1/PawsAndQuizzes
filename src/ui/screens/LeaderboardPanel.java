package ui.screens;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import manager.Leaderboard;
import manager.QuizEngine;
import model.PlayerScore;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.LeaderboardTable;
import ui.components.LeaderboardTableModel;
import ui.components.PixelButton;
import ui.components.PixelScrollBarUI;
import ui.theme.PixelTheme;

/** Ranked list of every run played in this session, best first. */
public class LeaderboardPanel extends BaseScreenPanel implements Refreshable {

    private final QuizEngine engine;
    private final Leaderboard leaderboard;
    private final LeaderboardTableModel tableModel = new LeaderboardTableModel();
    private final LeaderboardTable table = new LeaderboardTable(tableModel);

    public LeaderboardPanel(Navigator navigator, QuizEngine engine, Leaderboard leaderboard) {
        super("LEADERBOARD", "");
        this.engine = engine;
        this.leaderboard = leaderboard;

        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(BorderFactory.createLineBorder(PixelTheme.OUTLINE, 2));
        scroll.getViewport().setBackground(PixelTheme.CREAM);
        scroll.getVerticalScrollBar().setUI(new PixelScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(PixelTheme.px(16), 0));
        scroll.getVerticalScrollBar().setUnitIncrement(PixelTheme.px(32));
        place(scroll, MARGIN, 176, CONTENT_WIDTH, 424);

        PixelButton back = new PixelButton("Back", PixelTheme.CREAM);
        back.setCenterText(true);
        back.addActionListener(e -> navigator.goTo(Screen.MAIN_MENU));
        place(back, MARGIN, 636, 176, 48);
    }

    @Override
    public void refresh() {
        List<PlayerScore> all = leaderboard.getAll();
        tableModel.setScores(all);
        table.setHighlightedRow(all.indexOf(engine.getLastScore()));
        setStepText(all.size() + (all.size() == 1 ? " RUN" : " RUNS"));
        repaint();
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        PixelTheme.drawText(g2, "TINY PAWS. BIG BRAINS.", MARGIN, 142, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        String note = leaderboard.size() == 0 ? "No runs yet. Go play a round!" : "Best runs from this session.";
        PixelTheme.drawText(g2, note, 800, 140, PixelTheme.body(18f), PixelTheme.BROWN);
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
    }
}
