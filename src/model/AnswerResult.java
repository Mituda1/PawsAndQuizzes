package model;

/** What happened when the player answered (or ran out of time). Read-only once created. */
public class AnswerResult {

    private final boolean correct;
    private final boolean timedOut;
    private final int points;
    private final String correctAnswer;
    private final String explanation;
    private final int heartsLeft;
    private final int streak;

    public AnswerResult(boolean correct, boolean timedOut, int points, String correctAnswer,
                        String explanation, int heartsLeft, int streak) {
        this.correct = correct;
        this.timedOut = timedOut;
        this.points = points;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.heartsLeft = heartsLeft;
        this.streak = streak;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    /** Points earned for this answer (0 when wrong). */
    public int getPoints() {
        return points;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getHeartsLeft() {
        return heartsLeft;
    }

    /** How many right answers in a row, counting this one. */
    public int getStreak() {
        return streak;
    }
}
