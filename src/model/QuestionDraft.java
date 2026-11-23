package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The raw text typed into the add/edit form. The form fills it in, and build() turns it
 * into a real Question or throws IllegalArgumentException saying what is wrong.
 * This keeps the "what makes a valid question" rules out of the screen code.
 */
public class QuestionDraft {

    public static final int OPTION_COUNT = 4;

    private QuestionType type = QuestionType.MULTIPLE_CHOICE;
    private String text = "";
    private String category;
    private Difficulty difficulty = Difficulty.EASY;
    private String explanation = "";
    private List<String> options = new ArrayList<>(Arrays.asList("", "", "", ""));
    private int correctOptionIndex;
    private boolean trueFalseAnswer = true;
    private String acceptedAnswersText = "";

    /** Copies an existing question into a draft, so the form can edit it. */
    public static QuestionDraft from(Question question) {
        QuestionDraft draft = new QuestionDraft();
        draft.type = question.getType();
        draft.text = question.getText();
        draft.category = question.getCategory();
        draft.difficulty = question.getDifficulty();
        draft.explanation = question.getExplanation();

        switch (question.getType()) {
            case MULTIPLE_CHOICE:
                MultipleChoiceQuestion mc = (MultipleChoiceQuestion) question;
                List<String> shown = new ArrayList<>(mc.getChoices());
                while (shown.size() < OPTION_COUNT) {
                    shown.add("");
                }
                draft.options = new ArrayList<>(shown.subList(0, OPTION_COUNT));
                draft.correctOptionIndex = mc.getCorrectIndex();
                break;
            case TRUE_FALSE:
                draft.trueFalseAnswer = ((TrueFalseQuestion) question).isCorrectAnswer();
                break;
            case IDENTIFICATION:
                draft.acceptedAnswersText =
                        String.join(", ", ((IdentificationQuestion) question).getAcceptedAnswers());
                break;
            default:
                break;
        }
        return draft;
    }

    /** Builds the question with the given ID, or throws IllegalArgumentException with a friendly message. */
    public Question build(int id) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        switch (type) {
            case MULTIPLE_CHOICE:
                return buildMultipleChoice(id);
            case TRUE_FALSE:
                return new TrueFalseQuestion(id, text, category, difficulty, explanation, trueFalseAnswer);
            case IDENTIFICATION:
                return new IdentificationQuestion(id, text, category, difficulty, explanation, splitAnswers());
            default:
                throw new IllegalArgumentException("Choose a question type");
        }
    }

    private Question buildMultipleChoice(int id) {
        List<String> kept = new ArrayList<>();
        int newCorrectIndex = -1;
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i) == null ? "" : options.get(i).trim();
            if (!option.isEmpty()) {
                if (i == correctOptionIndex) {
                    newCorrectIndex = kept.size();
                }
                kept.add(option);
            }
        }
        if (kept.size() < 2) {
            throw new IllegalArgumentException("Fill in at least two options");
        }
        if (newCorrectIndex < 0) {
            throw new IllegalArgumentException("The correct option cannot be empty");
        }
        return new MultipleChoiceQuestion(id, text, category, difficulty, explanation, kept, newCorrectIndex);
    }

    private List<String> splitAnswers() {
        List<String> answers = new ArrayList<>();
        for (String part : acceptedAnswersText == null ? new String[0] : acceptedAnswersText.split(",")) {
            if (!part.trim().isEmpty()) {
                answers.add(part.trim());
            }
        }
        return answers;
    }

    // ----- getters and setters (no checking here: build() does all the checking) -----

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public boolean isTrueFalseAnswer() {
        return trueFalseAnswer;
    }

    public void setTrueFalseAnswer(boolean trueFalseAnswer) {
        this.trueFalseAnswer = trueFalseAnswer;
    }

    public String getAcceptedAnswersText() {
        return acceptedAnswersText;
    }

    public void setAcceptedAnswersText(String acceptedAnswersText) {
        this.acceptedAnswersText = acceptedAnswersText;
    }
}
