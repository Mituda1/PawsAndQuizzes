package manager;

import model.Cat;
import model.Question;

/**
 * Strategy pattern: the quiz engine asks this interface for points and never cares how they are worked out.
 * To try a different scoring rule, write a new class that implements it.
 */
public interface ScoringStrategy {

    /**
     * @param secondsLeft      seconds still on the clock when the player answered
     * @param timeLimitSeconds the full time for this question
     * @param streak           right answers in a row, including this one
     */
    int calculate(Question question, int secondsLeft, int timeLimitSeconds, Cat cat, int streak);
}
