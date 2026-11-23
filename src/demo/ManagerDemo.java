package demo;

import manager.Leaderboard;
import manager.QuestionBank;
import manager.SampleQuestions;
import model.Difficulty;
import model.PlayerScore;
import model.Question;
import model.TrueFalseQuestion;

/**
 * TEMPORARY test program. Right-click and choose "Run File" (Shift+F6).
 * Delete the whole demo package before you submit.
 */
public class ManagerDemo {

    public static void main(String[] args) {
        QuestionBank bank = new QuestionBank();
        SampleQuestions.loadInto(bank);

        System.out.println("=== Starter bank ===");
        System.out.println("Total questions: " + bank.size());
        for (String category : bank.getCategories()) {
            System.out.println(category + ": " + bank.filter(category, null).size()
                    + " (Easy " + bank.count(category, Difficulty.EASY)
                    + ", Medium " + bank.count(category, Difficulty.MEDIUM)
                    + ", Hard " + bank.count(category, Difficulty.HARD) + ")");
        }

        System.out.println();
        System.out.println("=== Search and filter ===");
        System.out.println("Keyword 'java' : " + bank.filter("java", null, null, null).size() + " found");
        System.out.println("Science + Hard : " + bank.filter("Science", Difficulty.HARD).size() + " found");
        System.out.println("True or False  : " + bank.filter(null, null, null, "True or False").size() + " found");

        System.out.println();
        System.out.println("=== Add, edit, delete ===");
        int newId = bank.getNextId();
        bank.add(new TrueFalseQuestion(newId, "Cats are mammals.", "Science", Difficulty.EASY, "Cats are mammals.", true));
        System.out.println("Added ID " + newId + ", total now " + bank.size());

        bank.update(new TrueFalseQuestion(newId, "Cats are mammals!", "Science", Difficulty.EASY, "Yes.", true));
        System.out.println("Edited: " + bank.findById(newId).get());

        Question removed = bank.remove(newId);
        System.out.println("Deleted: " + removed.getText() + ", total now " + bank.size());

        System.out.println();
        System.out.println("=== Rejected actions ===");
        try {
            bank.add(new TrueFalseQuestion(1, "Duplicate ID test", "Science", Difficulty.EASY, "", true));
            System.out.println("PROBLEM: duplicate ID was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK, rejected: " + e.getMessage());
        }
        try {
            bank.remove(999);
            System.out.println("PROBLEM: missing ID was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK, rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Leaderboard ===");
        Leaderboard board = new Leaderboard();
        board.add(new PlayerScore("Mochi", "Mango", 800, 8, 10, 120));
        board.add(new PlayerScore("Bean", "Luna", 1200, 10, 10, 150));
        board.add(new PlayerScore("Kiko", "Midnight", 900, 9, 10, 95));
        int rank = 1;
        for (PlayerScore s : board.getTop(2)) {
            System.out.println("Top " + rank++ + ": " + s);
        }
        System.out.println("Total entries: " + board.size());
    }
}
