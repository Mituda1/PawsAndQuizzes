package app;

import javax.swing.SwingUtilities;
import ui.MainFrame;

/** Entry point. it only starts the GUI on the Swing thread */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
