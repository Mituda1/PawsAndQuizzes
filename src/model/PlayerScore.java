package model;

/**
 * One finished run, ready for the leaderboard.
 * Sorting a list of these puts the best run first: higher score, then
 * higher accuracy, then faster time.
 */
public class PlayerScore implements Comparable<PlayerScore> {

    public static final int MAX_NAME_LENGTH = 12;

    private final String playerName;
    private final String catName;
    private final int score;
    private final int correctAnswers;
    private final int totalAnswered;
    private final int timeSeconds;

    public PlayerScore(String playerName, String catName, int score,
                       int correctAnswers, int totalAnswered, int timeSeconds) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (playerName.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be " + MAX_NAME_LENGTH + " characters or fewer");
        }
        if (score < 0 || correctAnswers < 0 || totalAnswered < 0 || timeSeconds < 0) {
            throw new IllegalArgumentException("Score, answers and time cannot be negative");
        }
        if (correctAnswers > totalAnswered) {
            throw new IllegalArgumentException("Correct answers cannot be more than total answered");
        }
        this.playerName = playerName.trim();
        this.catName = (catName == null || catName.trim().isEmpty()) ? "Unknown" : catName.trim();
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalAnswered = totalAnswered;
        this.timeSeconds = timeSeconds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCatName() {
        return catName;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalAnswered() {
        return totalAnswered;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public double getAccuracyPercent() {
        return totalAnswered == 0 ? 0.0 : (correctAnswers * 100.0) / totalAnswered;
    }

    @Override
    public int compareTo(PlayerScore other) {
        int byScore = Integer.compare(other.score, this.score);
        if (byScore != 0) {
            return byScore;
        }
        int byAccuracy = Double.compare(other.getAccuracyPercent(), this.getAccuracyPercent());
        if (byAccuracy != 0) {
            return byAccuracy;
        }
        return Integer.compare(this.timeSeconds, other.timeSeconds);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %d pts, %.0f%% accuracy, %ds",
                playerName, catName, score, getAccuracyPercent(), timeSeconds);
    }
}
