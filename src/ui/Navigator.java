package ui;

/**
 * screens talk to the frame only through this interface, so a screen never
 * needs to know how the frame is built low coupling
 */
public interface Navigator {
    void goTo(Screen screen);

    void exitGame();
}
