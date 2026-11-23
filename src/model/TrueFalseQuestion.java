package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A statement that is either true or false. */
public class TrueFalseQuestion extends Question {

    private final boolean correctAnswer;

    public TrueFalseQuestion(int id, String text, String category, Difficulty difficulty,
                             String explanation, boolean correctAnswer) {
        super(id, text, category, difficulty, explanation);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.TRUE_FALSE;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public List<String> getChoices() {
        return Collections.unmodifiableList(Arrays.asList("True", "False"));
    }

    @Override
    public String getCorrectAnswerText() {
        return correctAnswer ? "True" : "False";
    }

    @Override
    public boolean checkAnswer(String answer) {
        return answer != null && answer.trim().equalsIgnoreCase(getCorrectAnswerText());
    }
}
