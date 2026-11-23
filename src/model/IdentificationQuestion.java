package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** The player types the answer. Capital letters and extra spaces are forgiven. */
public class IdentificationQuestion extends Question {

    private final List<String> acceptedAnswers;

    public IdentificationQuestion(int id, String text, String category, Difficulty difficulty,
                                  String explanation, List<String> acceptedAnswers) {
        super(id, text, category, difficulty, explanation);

        if (acceptedAnswers == null || acceptedAnswers.isEmpty()) {
            throw new IllegalArgumentException("At least one accepted answer is required");
        }
        List<String> cleaned = new ArrayList<>();
        for (String answer : acceptedAnswers) {
            cleaned.add(requireText(answer, "Accepted answer"));
        }
        this.acceptedAnswers = cleaned;
    }

    /** Overloaded constructor for the common case of a single correct answer. */
    public IdentificationQuestion(int id, String text, String category, Difficulty difficulty,
                                  String explanation, String answer) {
        this(id, text, category, difficulty, explanation, Collections.singletonList(answer));
    }

    @Override
    public QuestionType getType() {
        return QuestionType.IDENTIFICATION;
    }

    public List<String> getAcceptedAnswers() {
        return Collections.unmodifiableList(acceptedAnswers);
    }

    /** Typed questions have no buttons, so there are no choices to show. */
    @Override
    public List<String> getChoices() {
        return Collections.emptyList();
    }

    @Override
    public String getCorrectAnswerText() {
        return acceptedAnswers.get(0);
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        String typed = normalize(answer);
        for (String accepted : acceptedAnswers) {
            if (normalize(accepted).equals(typed)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
