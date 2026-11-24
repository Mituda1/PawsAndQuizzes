package manager;

import model.Cat;
import model.Question;

/**
 * Base points for the difficulty, plus up to 50% more for answering fast (multiplied by the cat's
 * speed perk), plus 25 for every extra right answer in a row (up to 100).
 */
public class SpeedBonusScoring implements ScoringStrategy {

    private static final double MAX_SPEED_BONUS = 0.5;
    private static final int COMBO_STEP = 25;
    private static final int COMBO_CAP = 100;

    @Override
    public int calculate(Question question, int secondsLeft, int timeLimitSeconds, Cat cat, int streak) {
        int base = question.getBasePoints();
        double timeRatio = timeLimitSeconds <= 0
                ? 0 : Math.max(0.0, Math.min(1.0, secondsLeft / (double) timeLimitSeconds));
        int speedBonus = (int) Math.round(base * MAX_SPEED_BONUS * timeRatio * cat.getSpeedBonusMultiplier());
        int comboBonus = Math.min(COMBO_CAP, COMBO_STEP * Math.max(0, streak - 1));
        return base + speedBonus + comboBonus;
    }
}
