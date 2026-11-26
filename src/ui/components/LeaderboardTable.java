package ui.components;

import java.awt.Color;
import ui.theme.PixelTheme;

/** The leader board. The row for the run we just played is painted mint. */
public class LeaderboardTable extends PixelTable {

    private int highlightedRow = -1;

    public LeaderboardTable(LeaderboardTableModel model) {
        super(model, new int[] {80, 330, 220, 190, 190, 142});
    }

    /** Which row to color mint. Use -1 for none. */
    public void setHighlightedRow(int row) {
        this.highlightedRow = row;
        repaint();
    }

    @Override
    protected Color backgroundFor(Object value, int row, int column, boolean selected) {
        if (row == highlightedRow) {
            return PixelTheme.MINT;
        }
        return super.backgroundFor(value, row, column, selected);
    }
}
