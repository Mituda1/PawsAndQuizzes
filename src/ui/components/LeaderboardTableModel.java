package ui.components;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.PlayerScore;

/** Feeds the leaderboard scores (already sorted best first) to a table. */
public class LeaderboardTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"Rank", "Player", "Cat", "Score", "Accuracy", "Time"};

    private List<PlayerScore> scores = new ArrayList<>();

    public void setScores(List<PlayerScore> scores) {
        this.scores = new ArrayList<>(scores);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return scores.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        PlayerScore s = scores.get(row);
        switch (column) {
            case 0: return String.format("%02d", row + 1);
            case 1: return s.getPlayerName();
            case 2: return s.getCatName();
            case 3: return String.format("%,d", s.getScore());
            case 4: return String.format("%.0f%%", s.getAccuracyPercent());
            default: return formatTime(s.getTimeSeconds());
        }
    }

    /** 75 seconds becomes "01:15". */
    public static String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
