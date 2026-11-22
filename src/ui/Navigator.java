package ui;

/**
 * Screens talk to the frame only through this interface, so a screen never
 * needs to know how the frame is built (low coupling, good for your UML).
 */
public interface Navigator {
    void goTo(Screen screen);

    void exitGame();
}
