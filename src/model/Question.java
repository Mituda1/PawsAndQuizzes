package model;

import java.util.List;

/**
 * Parent of every question type. It holds what all questions share
 * (id, text, category, difficulty, explanation) and forces each child
 * to define its own grading and answer choices.
 */
public abstract class Question implements Gradable {

    private int id;
    private String text;
    private String category;
    private Difficulty difficulty;
    private String explanation;

    protected Question(int id, String text, String category, Difficulty difficulty, String explanation) {
        setId(id);
        setText(text);
        setCategory(category);
        setDifficulty(difficulty);
        setExplanation(explanation);
    }

    // ----- what each question type must provide -----

    /** Which kind of question this is. */
    public abstract QuestionType getType();

    /** Answer choices to display. Empty for typed (identification) questions. */
    public abstract List<String> getChoices();

    /** The correct answer as text, used for the feedback popup. */
    public abstract String getCorrectAnswerText();

    // ----- shared behavior -----

    /** Short name shown in the GUI, e.g. "Multiple Choice". */
    public String getTypeName() {
        return getType().getLabel();
    }

    @Override
    public int getBasePoints() {
        return difficulty.getBasePoints();
    }

    public int getTimeLimitSeconds() {
        return difficulty.getTimeLimitSeconds();
    }

    // ----- getters and validating setters -----

    public int getId() {
        return id;
    }

    public final void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Question ID must be a positive number");
        }
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public final void setText(String text) {
        this.text = requireText(text, "Question text");
    }

    public String getCategory() {
        return category;
    }

    public final void setCategory(String category) {
        this.category = requireText(category, "Category");
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public final void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty must be chosen");
        }
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public final void setExplanation(String explanation) {
        this.explanation = explanation == null ? "" : explanation.trim();
    }

    /** Shared check used by this class and its children. Throws if the value is null or blank. */
    protected static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return "#" + id + " [" + getTypeName() + ", " + category + ", " + difficulty + "] " + text;
    }
}
