package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import ui.theme.PixelTheme;

/** The question list, styled to match the game: brown header, striped rows, mint selection. */
public class QuestionTable extends JTable {

    private static final Color ALT_ROW = new Color(0xFFFAF0);
    private static final int[] COLUMN_WIDTHS = {56, 560, 170, 230, 120};

    public QuestionTable(QuestionTableModel model) {
        super(model);
        setFont(PixelTheme.scaled(PixelTheme.body(18f)));
        setRowHeight(PixelTheme.px(32));
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        setBackground(PixelTheme.CREAM);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, PixelTheme.px(32)));
        getTableHeader().setDefaultRenderer(new HeaderRenderer());
        setDefaultRenderer(Object.class, new CellRenderer());

        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(PixelTheme.px(COLUMN_WIDTHS[i]));
        }
    }

    /** Brown bar with cream pixel text. */
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setText(String.valueOf(value).toUpperCase());
            setFont(PixelTheme.scaled(PixelTheme.heading(12f)));
            setBackground(PixelTheme.BROWN);
            setForeground(PixelTheme.CREAM);
            setBorder(BorderFactory.createEmptyBorder(0, PixelTheme.px(10), 0, PixelTheme.px(10)));
            return this;
        }
    }

    /** Striped rows, mint when selected, and a soft color on the Level column. */
    private static class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(table.getFont());
            setForeground(PixelTheme.OUTLINE);
            setBorder(BorderFactory.createEmptyBorder(0, PixelTheme.px(10), 0, PixelTheme.px(10)));

            Color background = (row % 2 == 0) ? PixelTheme.CREAM : ALT_ROW;
            if (selected) {
                background = PixelTheme.MINT;
            } else if (column == 4) {
                String level = String.valueOf(value);
                if ("Easy".equals(level)) {
                    background = new Color(0xCDEBDA);
                } else if ("Medium".equals(level)) {
                    background = new Color(0xFFEDB8);
                } else if ("Hard".equals(level)) {
                    background = new Color(0xF6BDBD);
                }
            }
            setBackground(background);
            return this;
        }
    }
}
