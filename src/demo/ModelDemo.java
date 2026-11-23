package demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import model.Difficulty;
import model.IdentificationQuestion;
import model.MultipleChoiceQuestion;
import model.PlayerScore;
import model.Question;
import model.TrueFalseQuestion;

/**
 * TEMPORARY test program. Right-click this file and choose "Run File" (Shift+F6)
 * to check that the model classes work. Delete the whole demo package before you submit.
 */
public class ModelDemo {

    public static void main(String[] args) {
        List<Question> questions = new ArrayList<>();
        questions.add(new MultipleChoiceQuestion(1, "Which keyword makes a class inherit from another?",
                "Java OOP", Difficulty.EASY, "extends is used for class inheritance.",
                Arrays.asList("implements", "extends", "inherits", "super"), 1));
        questions.add(new TrueFalseQuestion(2, "A Java interface can have a constructor.",
                "Java OOP", Difficulty.MEDIUM, "Interfaces cannot have constructors.", false));
        questions.add(new IdentificationQuestion(3, "Which keyword creates a new object?",
                "Java OOP", Difficulty.HARD, "The new keyword allocates the object.", "new"));

        System.out.println("=== Polymorphism: same calls, different question types ===");
        for (Question q : questions) {
            System.out.println(q);
            System.out.println("   choices: " + q.getChoices());
            System.out.println("   correct answer graded as: " + q.checkAnswer(q.getCorrectAnswerText()));
            System.out.println("   wrong answer graded as:   " + q.checkAnswer("banana"));
            System.out.println("   worth " + q.getBasePoints() + " pts, " + q.getTimeLimitSeconds() + "s");
        }

        System.out.println();
        System.out.println("=== Forgiving typed answers ===");
        System.out.println("'  NEW ' accepted? " + questions.get(2).checkAnswer("  NEW "));

        System.out.println();
        System.out.println("=== Validation (these should all be rejected) ===");
        tryBad("empty text", () -> new TrueFalseQuestion(5, "  ", "Science", Difficulty.EASY, "", true));
        tryBad("ID of 0", () -> new TrueFalseQuestion(0, "Water is wet.", "Science", Difficulty.EASY, "", true));
        tryBad("duplicate options", () -> new MultipleChoiceQuestion(6, "Pick one", "Science",
                Difficulty.EASY, "", Arrays.asList("A", "a", "B"), 0));
        tryBad("correct index out of range", () -> new MultipleChoiceQuestion(7, "Pick one", "Science",
                Difficulty.EASY, "", Arrays.asList("A", "B"), 5));
        tryBad("name too long", () -> new PlayerScore("ThisNameIsWayTooLong", "Mango", 100, 1, 1, 10));

        System.out.println();
        System.out.println("=== Leaderboard order ===");
        List<PlayerScore> board = new ArrayList<>();
        board.add(new PlayerScore("Mochi", "Mango", 800, 8, 10, 120));
        board.add(new PlayerScore("Bean", "Luna", 1200, 10, 10, 150));
        board.add(new PlayerScore("Kiko", "Midnight", 800, 8, 10, 95));
        Collections.sort(board);
        int rank = 1;
        for (PlayerScore s : board) {
            System.out.println(rank++ + ". " + s);
        }
    }

    private static void tryBad(String label, Runnable action) {
        try {
            action.run();
            System.out.println("PROBLEM: '" + label + "' was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK, rejected " + label + ": " + e.getMessage());
        }
    }
}
