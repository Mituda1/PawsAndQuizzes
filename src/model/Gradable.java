package model;

/**
 * Anything that can be graded. The quiz engine only knows this interface,
 * so it never needs to know which kind of question it is checking.
 */
public interface Gradable {

    /** @return true if the given answer text is correct */
    boolean checkAnswer(String answer);

    /** @return points awarded for a correct answer, before any speed bonus */
    int getBasePoints();
}
