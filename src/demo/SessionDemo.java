package demo;

import java.util.List;
import manager.CatFactory;
import model.Cat;
import model.GameSession;

/**
 * TEMPORARY test program. Right-click and choose "Run File" (Shift+F6).
 * Delete the whole demo package before you submit.
 */
public class SessionDemo {

    public static void main(String[] args) {
        System.out.println("=== Cats (polymorphism: same calls, different answers) ===");
        List<Cat> cats = CatFactory.createAll();
        for (Cat cat : cats) {
            System.out.println(cat + "  perk: " + cat.getPerkDescription());
            System.out.println("   +seconds: " + cat.getBonusSeconds()
                    + ", speed multiplier: " + cat.getSpeedBonusMultiplier()
                    + ", free skips: " + cat.getFreeSkips());
        }

        System.out.println();
        System.out.println("=== GameSession validation ===");
        GameSession session = new GameSession();
        tryBad("empty name", () -> session.setPlayerName("   "));
        tryBad("name too long", () -> session.setPlayerName("ABCDEFGHIJKLM"));
        tryBad("no cat", () -> session.setCat(null));
        tryBad("no category", () -> session.setCategory(""));

        System.out.println();
        System.out.println("=== A valid run ===");
        System.out.println("Ready before setup? " + session.isReady());
        session.setPlayerName("  Mochi ");
        session.setCat(CatFactory.create("luna"));
        session.setCategory("Java OOP");
        System.out.println(session.summary());
        System.out.println("Ready after setup?  " + session.isReady());
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
