package ui.screens;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import manager.QuestionBank;
import model.Difficulty;
import model.Question;
import model.QuestionDraft;
import model.QuestionType;
import ui.Navigator;
import ui.Screen;
import ui.components.PixelButton;
import ui.components.PixelChoiceButton;
import ui.components.PixelTextField;
import ui.theme.PixelTheme;

/**
 * Screen: add a new question or edit an existing one.
 * The form copies what you typed into a QuestionDraft. The draft builds the real question
 * (or says what is wrong), and the QuestionBank stores it.
 */
public class QuestionFormPanel extends BaseScreenPanel {

    private final Navigator navigator;
    private final QuestionBank bank;

    private final PixelChoiceButton typeChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final PixelChoiceButton categoryChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final PixelChoiceButton levelChoice = new PixelChoiceButton(PixelTheme.CREAM);
    private final PixelChoiceButton correctChoice = new PixelChoiceButton(PixelTheme.YELLOW);
    private final PixelTextField questionField = new PixelTextField(150);
    private final PixelTextField[] optionFields = new PixelTextField[QuestionDraft.OPTION_COUNT];
    private final PixelTextField acceptedField = new PixelTextField(120);
    private final PixelTextField explanationField = new PixelTextField(150);

    private Integer editingId;       // null while adding a new question
    private String errorMessage;

    public QuestionFormPanel(Navigator navigator, QuestionBank bank) {
        super("QUESTION FORM", "");
        this.navigator = navigator;
        this.bank = bank;

        place(typeChoice, MARGIN, 194, 280, 48);
        place(categoryChoice, 355, 194, 320, 48);
        place(levelChoice, 687, 194, 200, 48);
        typeChoice.setOptions(QuestionType.labels());
        levelChoice.setOptions(difficultyLabels());
        typeChoice.setOnChange(this::applyType);

        place(questionField, MARGIN, 280, CONTENT_WIDTH, 48);
        for (int i = 0; i < optionFields.length; i++) {
            optionFields[i] = place(new PixelTextField(60), 0, 0, 560, 48);
        }
        place(acceptedField, MARGIN, 364, CONTENT_WIDTH, 48);
        place(correctChoice, MARGIN, 522, 200, 48);
        place(explanationField, 283, 522, 932, 48);
        questionField.setPlaceholder("Type the question here...");
        explanationField.setPlaceholder("Why is it the right answer? (optional)");
        acceptedField.setPlaceholder("Example: Rizal, Jose Rizal");

        for (JComponent field : new JComponent[] {questionField, acceptedField, explanationField}) {
            watch((PixelTextField) field);
        }
        for (PixelTextField field : optionFields) {
            watch(field);
        }

        PixelButton cancel = new PixelButton("Cancel", PixelTheme.CREAM);
        cancel.setCenterText(true);
        cancel.addActionListener(e -> navigator.goTo(Screen.LIBRARY));
        place(cancel, MARGIN, 636, 176, 48);

        PixelButton save = new PixelButton("Save", PixelTheme.MINT);
        save.setCenterText(true);
        save.addActionListener(e -> onSave());
        place(save, MARGIN + CONTENT_WIDTH - 176, 636, 176, 48);

        open(null);
    }

    private static List<String> difficultyLabels() {
        List<String> labels = new ArrayList<>();
        for (Difficulty d : Difficulty.values()) {
            labels.add(d.getLabel());
        }
        return labels;
    }

    /** Typing in any box clears the red error message. */
    private void watch(PixelTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError();
            }
        });
    }

    /** Opens the form empty (null) or filled with an existing question. */
    public void open(Question question) {
        editingId = question == null ? null : question.getId();
        categoryChoice.setOptions(bank.getAllCategories());

        QuestionDraft draft = question == null ? new QuestionDraft() : QuestionDraft.from(question);
        typeChoice.setSelectedOption(draft.getType().getLabel());
        if (question == null) {
            categoryChoice.setSelectedIndex(0);
        } else {
            categoryChoice.setSelectedOption(draft.getCategory());
        }
        levelChoice.setSelectedOption(draft.getDifficulty().getLabel());
        questionField.setText(draft.getText());
        explanationField.setText(draft.getExplanation());
        for (int i = 0; i < optionFields.length; i++) {
            optionFields[i].setText(draft.getOptions().get(i));
        }
        acceptedField.setText(draft.getAcceptedAnswersText());

        applyType();                       // sets what "correct answer" can be, for this type
        if (draft.getType() == QuestionType.MULTIPLE_CHOICE) {
            correctChoice.setSelectedIndex(draft.getCorrectOptionIndex());
        } else if (draft.getType() == QuestionType.TRUE_FALSE) {
            correctChoice.setSelectedOption(draft.isTrueFalseAnswer() ? "True" : "False");
        }
        clearError();
    }

    /** Shows only the boxes that this type of question needs. */
    private void applyType() {
        QuestionType type = QuestionType.fromLabel(typeChoice.getSelectedOption());
        boolean multipleChoice = type == QuestionType.MULTIPLE_CHOICE;
        boolean typed = type == QuestionType.IDENTIFICATION;

        for (int i = 0; i < optionFields.length; i++) {
            optionFields[i].setVisible(multipleChoice);
            int column = i % 2;
            int row = i / 2;
            setDesignBounds(optionFields[i], column == 0 ? MARGIN : 655, 364 + row * 74, 560, 48);
        }
        acceptedField.setVisible(typed);
        correctChoice.setVisible(!typed);
        if (multipleChoice) {
            correctChoice.setOptions(Arrays.asList("A", "B", "C", "D"));
        } else if (!typed) {
            correctChoice.setOptions(Arrays.asList("True", "False"));
        }
        setDesignBounds(explanationField, typed ? MARGIN : 283, 522, typed ? CONTENT_WIDTH : 932, 48);
        repaint();
    }

    private QuestionDraft toDraft() {
        QuestionType type = QuestionType.fromLabel(typeChoice.getSelectedOption());
        QuestionDraft draft = new QuestionDraft();
        draft.setType(type);
        draft.setText(questionField.getText());
        draft.setCategory(categoryChoice.getSelectedOption());
        draft.setDifficulty(Difficulty.fromLabel(levelChoice.getSelectedOption()));
        draft.setExplanation(explanationField.getText());

        List<String> options = new ArrayList<>();
        for (PixelTextField field : optionFields) {
            options.add(field.getText());
        }
        draft.setOptions(options);
        if (type == QuestionType.MULTIPLE_CHOICE) {
            draft.setCorrectOptionIndex(correctChoice.getSelectedIndex());
        } else if (type == QuestionType.TRUE_FALSE) {
            draft.setTrueFalseAnswer("True".equals(correctChoice.getSelectedOption()));
        }
        draft.setAcceptedAnswersText(acceptedField.getText());
        return draft;
    }

    private void onSave() {
        clearError();
        try {
            int id = editingId != null ? editingId : bank.getNextId();
            Question question = toDraft().build(id);
            if (editingId == null) {
                bank.add(question);
            } else {
                bank.update(question);
            }
        } catch (IllegalArgumentException ex) {
            errorMessage = ex.getMessage();
            repaint();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Check the form", JOptionPane.WARNING_MESSAGE);
            return;
        }
        navigator.goTo(Screen.LIBRARY);
    }

    private void clearError() {
        errorMessage = null;
        repaint();
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        String title = editingId == null ? "NEW QUESTION" : "EDIT QUESTION #" + editingId;
        PixelTheme.drawText(g2, title, MARGIN, 142, PixelTheme.heading(32f), PixelTheme.OUTLINE);

        QuestionType type = QuestionType.fromLabel(typeChoice.getSelectedOption());
        label(g2, "TYPE", MARGIN, 186);
        label(g2, "CATEGORY", 355, 186);
        label(g2, "LEVEL", 687, 186);
        label(g2, "QUESTION", MARGIN, 272);

        if (type == QuestionType.MULTIPLE_CHOICE) {
            label(g2, "OPTION A", MARGIN, 356);
            label(g2, "OPTION B", 655, 356);
            label(g2, "OPTION C", MARGIN, 430);
            label(g2, "OPTION D", 655, 430);
            label(g2, "CORRECT", MARGIN, 514);
        } else if (type == QuestionType.TRUE_FALSE) {
            label(g2, "CORRECT", MARGIN, 514);
        } else {
            label(g2, "ACCEPTED ANSWERS (SEPARATE WITH COMMAS)", MARGIN, 356);
        }
        label(g2, "EXPLANATION", type == QuestionType.IDENTIFICATION ? MARGIN : 283, 514);

        if (errorMessage != null) {
            PixelTheme.drawText(g2, errorMessage, MARGIN, 600, PixelTheme.body(20f), PixelTheme.RED);
        }
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
    }

    private static void label(Graphics2D g2, String text, int x, int baseline) {
        PixelTheme.drawText(g2, text, x, baseline, PixelTheme.heading(12f), PixelTheme.OUTLINE);
    }
}
