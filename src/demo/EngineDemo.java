package demo;

import java.util.Random;
import manager.CatFactory;
import manager.Leaderboard;
import manager.QuestionBank;
import manager.QuizEngine;
import manager.SampleQuestions;
import manager.SpeedBonusScoring;
import model.AnswerResult;
import model.GameSession;
import model.PlayerScore;
import model.Question;

/**
 * TEMPORARY test program. Right-click and choose "Run File" (Shift+F6).
 * Delete the whole demo package before you submit.
 */
public class EngineDemo {

    public static void main(String[] args) {
        QuestionBank bank = new QuestionBank();
        SampleQuestions.loadInto(bank);
        Leaderboard board = new Leaderboard();

        System.out.println("=== Run 1: Midnight (1.5x speed bonus). Wave 4 is answered wrong ===");
        QuizEngine engine = newEngine(bank, "Mochi", "midnight", "Java OOP");
        while (!engine.isFinished()) {
            Question q = engine.currentQuestion();
            int limit = engine.currentTimeLimit();
            System.out.println("Wave " + engine.getWave() + "/" + engine.getTotalWaves()
                    + " [" + q.getDifficulty() + ", " + q.getTypeName() + ", " + limit + "s] " + q.getText());
            System.out.println("   options shown: " + engine.currentChoices());

            String answer = engine.getWave() == 4 ? "banana" : q.getCorrectAnswerText();
            AnswerResult r = engine.submitAnswer(answer, limit / 2);   // pretend half the time was left
            System.out.println("   " + (r.isCorrect() ? "CORRECT +" + r.getPoints() : "WRONG")
                    + "  hearts=" + r.getHeartsLeft() + "  streak=" + r.getStreak()
                    + "  | " + r.getExplanation());
            engine.advance();
        }
        PlayerScore first = engine.finish();
        board.add(first);
        System.out.println("Final: " + first);

        System.out.println();
        System.out.println("=== Run 2: Luna (1 free skip). Skip wave 1, then let time run out until hearts are gone ===");
        engine = newEngine(bank, "Bean", "luna", "Science");
        System.out.println("Can skip? " + engine.canSkip());
        engine.skip();
        System.out.println("After skip: wave " + engine.getWave() + ", can skip again? " + engine.canSkip());
        while (!engine.isFinished()) {
            AnswerResult r = engine.timeUp();
            System.out.println("   time up on wave " + engine.getWave() + ", hearts left " + r.getHeartsLeft());
            engine.advance();
        }
        PlayerScore second = engine.finish();
        board.add(second);
        System.out.println("Game over? " + engine.isGameOver() + "  Final: " + second);

        System.out.println();
        System.out.println("=== Wrong use is rejected ===");
        try {
            engine.submitAnswer("x", 5);
            System.out.println("PROBLEM: answered after the run ended");
        } catch (IllegalStateException e) {
            System.out.println("OK, rejected: " + e.getMessage());
        }
        try {
            new QuizEngine(bank).start(new GameSession());
            System.out.println("PROBLEM: started without a name, cat and topic");
        } catch (IllegalArgumentException e) {
            System.out.println("OK, rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Leaderboard ===");
        int rank = 1;
        for (PlayerScore s : board.getAll()) {
            System.out.println(rank++ + ". " + s);
        }
    }

    private static QuizEngine newEngine(QuestionBank bank, String name, String cat, String category) {
        GameSession session = new GameSession();
        session.setPlayerName(name);
        session.setCat(CatFactory.create(cat));
        session.setCategory(category);
        QuizEngine engine = new QuizEngine(bank, new SpeedBonusScoring(), new Random(7));
        engine.start(session);
        return engine;
    }
}
