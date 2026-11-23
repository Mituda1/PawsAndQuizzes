package ui.screens;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import manager.QuestionBank;
import model.Difficulty;
import model.Question;
import model.QuestionType;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.PixelButton;
import ui.components.PixelChoiceButton;
import ui.components.PixelScrollBarUI;
import ui.components.PixelTextField;
import ui.components.QuestionTable;
import ui.components.QuestionTableModel;
import ui.theme.PixelTheme;

/**
 * Screen: the Question Library. Search, filter, add, edit and delete questions.
 * It only shows and forwards clicks. Storing, searching and rules live in QuestionBank and QuestionDraft.
 */
public class LibraryPanel extends BaseScreenPanel implements Refreshable {

    private static final String ALL_CATEGORIES = "All categories";
    private static final String ALL_LEVELS = "All levels";
    private static final String ALL_TYPES = "All types";

    private final Navigator navigator;
    private final QuestionBank bank;
    private final QuestionFormPanel form;

    private final PixelTextField searchField = new PixelTextField(40);
    private final PixelChoiceButton categoryChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final PixelChoiceButton levelChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final PixelChoiceButton typeChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final QuestionTableModel tableModel = new QuestionTableModel();
    private final QuestionTable table = new QuestionTable(tableModel);
    private final PixelButton editButton = new PixelButton("Edit", PixelTheme.PEACH);
    private final PixelButton deleteButton = new PixelButton("Delete", PixelTheme.RED);

    public LibraryPanel(Navigator navigator, QuestionBank bank, QuestionFormPanel form) {
        super("QUESTION LIBRARY", "");
        this.navigator = navigator;
        this.bank = bank;
        this.form = form;

        // filter row
        searchField.setPlaceholder("Search text or ID...");
        place(searchField, MARGIN, 160, 420, 48);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        categoryChoice.setOptions(Arrays.asList(ALL_CATEGORIES));
        List<String> levels = new ArrayList<>();
        levels.add(ALL_LEVELS);
        for (Difficulty d : Difficulty.values()) {
            levels.add(d.getLabel());
        }
        levelChoice.setOptions(levels);
        List<String> types = new ArrayList<>();
        types.add(ALL_TYPES);
        types.addAll(QuestionType.labels());
        typeChoice.setOptions(types);

        place(categoryChoice, 495, 160, 260, 48);
        place(levelChoice, 767, 160, 180, 48);
        place(typeChoice, 959, 160, 256, 48);
        categoryChoice.setOnChange(this::applyFilters);
        levelChoice.setOnChange(this::applyFilters);
        typeChoice.setOnChange(this::applyFilters);

        // table
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(BorderFactory.createLineBorder(PixelTheme.OUTLINE, 2));
        scroll.getViewport().setBackground(PixelTheme.CREAM);
        scroll.getVerticalScrollBar().setUI(new PixelScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(PixelTheme.px(16), 0));
        scroll.getVerticalScrollBar().setUnitIncrement(PixelTheme.px(32));
        place(scroll, MARGIN, 224, CONTENT_WIDTH, 380);

        table.getSelectionModel().addListSelectionListener(e -> updateButtons());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    onEdit();
                }
            }
        });

        // footer buttons
        PixelButton back = new PixelButton("Back", PixelTheme.CREAM);
        back.setCenterText(true);
        back.addActionListener(e -> navigator.goTo(Screen.MAIN_MENU));
        place(back, MARGIN, 636, 176, 48);

        PixelButton add = new PixelButton("Add", PixelTheme.MINT);
        add.setCenterText(true);
        add.addActionListener(e -> {
            form.open(null);
            navigator.goTo(Screen.QUESTION_FORM);
        });
        place(add, 663, 636, 176, 48);

        editButton.setCenterText(true);
        editButton.addActionListener(e -> onEdit());
        place(editButton, 851, 636, 176, 48);

        deleteButton.setCenterText(true);
        deleteButton.addActionListener(e -> onDelete());
        place(deleteButton, 1039, 636, 176, 48);

        updateButtons();
    }

    private Question selectedQuestion() {
        int row = table.getSelectedRow();
        return row < 0 ? null : tableModel.getQuestionAt(table.convertRowIndexToModel(row));
    }

    private void onEdit() {
        Question selected = selectedQuestion();
        if (selected != null) {
            form.open(selected);
            navigator.goTo(Screen.QUESTION_FORM);
        }
    }

    private void onDelete() {
        Question selected = selectedQuestion();
        if (selected == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete question #" + selected.getId() + "?\n" + selected.getText(),
                "Delete question", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                bank.remove(selected.getId());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not delete", JOptionPane.WARNING_MESSAGE);
            }
            applyFilters();
        }
    }

    /** Reads the search box and the three filter buttons, asks the bank, and shows the result. */
    private void applyFilters() {
        String keyword = searchField.getText();
        String category = categoryChoice.getSelectedIndex() == 0 ? null : categoryChoice.getSelectedOption();
        Difficulty level = levelChoice.getSelectedIndex() == 0
                ? null : Difficulty.fromLabel(levelChoice.getSelectedOption());
        String type = typeChoice.getSelectedIndex() == 0 ? null : typeChoice.getSelectedOption();

        List<Question> rows = bank.filter(keyword, category, level, type);
        tableModel.setQuestions(rows);

        boolean filtered = !keyword.trim().isEmpty() || category != null || level != null || type != null;
        setStepText(filtered ? rows.size() + " / " + bank.size() + " SHOWN" : bank.size() + " QUESTIONS");
        updateButtons();
    }

    private void updateButtons() {
        boolean hasSelection = table.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    @Override
    public void refresh() {
        List<String> categories = new ArrayList<>();
        categories.add(ALL_CATEGORIES);
        categories.addAll(bank.getCategories());
        categoryChoice.setOptions(categories);
        applyFilters();
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        PixelTheme.drawText(g2, "CURIOSITY, SORTED.", MARGIN, 142, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Select a row to edit or delete it.", 680, 140,
                PixelTheme.body(18f), PixelTheme.BROWN);

        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
    }
}
