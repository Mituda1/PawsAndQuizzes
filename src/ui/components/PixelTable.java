package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import ui.theme.PixelTheme;

/**
 * Base for every table in the game: brown header, striped rows, mint selection.
 * A child table only chooses its column widths and, if it wants, row colors (backgroundFor).
 */
public class PixelTable extends JTable {

    private static final Color ALT_ROW = new Color(0xFFFAF0);

    protected PixelTable(TableModel model, int[] columnWidths) {
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

        for (int i = 0; i < columnWidths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(PixelTheme.px(columnWidths[i]));
        }
    }

    /** Background color for one cell. Children override this to add their own colors. */
    protected Color backgroundFor(Object value, int row, int column, boolean selected) {
        if (selected) {
            return PixelTheme.MINT;
        }
        return (row % 2 == 0) ? PixelTheme.CREAM : ALT_ROW;
    }

    /** Brown bar with cream pixel text. */
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
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

    private class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(table.getFont());
            setForeground(PixelTheme.OUTLINE);
            setBorder(BorderFactory.createEmptyBorder(0, PixelTheme.px(10), 0, PixelTheme.px(10)));
            setBackground(backgroundFor(value, row, column, selected));
            return this;
        }
    }
}
