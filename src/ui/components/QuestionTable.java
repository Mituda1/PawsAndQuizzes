package ui.components;

import java.awt.Color;

/** The question list. Same look as every table, plus a soft color on the Level column. */
public class QuestionTable extends PixelTable {

    private static final int LEVEL_COLUMN = 4;

    public QuestionTable(QuestionTableModel model) {
        super(model, new int[] {56, 560, 170, 230, 120});
    }

    @Override
    protected Color backgroundFor(Object value, int row, int column, boolean selected) {
        if (!selected && column == LEVEL_COLUMN) {
            String level = String.valueOf(value);
            if ("Easy".equals(level)) {
                return new Color(0xCDEBDA);
            } else if ("Medium".equals(level)) {
                return new Color(0xFFEDB8);
            } else if ("Hard".equals(level)) {
                return new Color(0xF6BDBD);
            }
        }
        return super.backgroundFor(value, row, column, selected);
    }
}
