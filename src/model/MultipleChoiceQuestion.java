package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A question with 2 to 6 options and exactly one correct option. */
public class MultipleChoiceQuestion extends Question {

    private final List<String> options;
    private final int correctIndex;

    public MultipleChoiceQuestion(int id, String text, String category, Difficulty difficulty,
                                  String explanation, List<String> options, int correctIndex) {
        super(id, text, category, difficulty, explanation);

        if (options == null || options.size() < 2 || options.size() > 6) {
            throw new IllegalArgumentException("A multiple-choice question needs 2 to 6 options");
        }
        List<String> cleaned = new ArrayList<>();
        for (String option : options) {
            String trimmed = requireText(option, "Option");
            for (String existing : cleaned) {
                if (existing.equalsIgnoreCase(trimmed)) {
                    throw new IllegalArgumentException("Options must be different from each other");
                }
            }
            cleaned.add(trimmed);
        }
        if (correctIndex < 0 || correctIndex >= cleaned.size()) {
            throw new IllegalArgumentException("Correct answer must be one of the options");
        }
        this.options = cleaned;
        this.correctIndex = correctIndex;
    }

    @Override
    public String getTypeName() {
        return "Multiple Choice";
    }

    /** Options in their original order. The quiz engine shuffles a copy when it plays the question. */
    @Override
    public List<String> getChoices() {
        return Collections.unmodifiableList(options);
    }

    @Override
    public String getCorrectAnswerText() {
        return options.get(correctIndex);
    }

    /** Grades by option text, so it still works after the options are shuffled. */
    @Override
    public boolean checkAnswer(String answer) {
        return answer != null && answer.trim().equalsIgnoreCase(getCorrectAnswerText());
    }

    /** Overloaded version: grades by the option's position in the original order. */
    public boolean checkAnswer(int originalIndex) {
        return originalIndex == correctIndex;
    }
}
