package ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Cat;
import model.GameSession;
import model.PlayerScore;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.CatCard;
import ui.components.PixelButton;
import ui.components.PixelTextField;
import ui.theme.PixelTheme;

/**
 * Screen 2: the player types a name and picks a cat.
 * This class only collects input. The rules (empty name, name too long, no cat)
 * live in GameSession, and this screen shows whatever error GameSession reports.
 */
public class PlayerSetupPanel extends BaseScreenPanel implements Refreshable {

    private static final Color[] ART_COLORS = {PixelTheme.YELLOW, PixelTheme.PEACH, PixelTheme.MINT};

    private final Navigator navigator;
    private final GameSession session;
    private final PixelTextField nameField = new PixelTextField(PlayerScore.MAX_NAME_LENGTH);
    private final List<CatCard> cards = new ArrayList<>();

    private Cat selectedCat;
    private String errorMessage;

    public PlayerSetupPanel(Navigator navigator, GameSession session, List<Cat> cats) {
        super("PLAYER SETUP", "01 / 02");
        this.navigator = navigator;
        this.session = session;

        place(nameField, MARGIN, 276, 560, 56);
        nameField.addActionListener(e -> onNext());           // Enter key
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError();
            }
        });

        int x = MARGIN;
        for (int i = 0; i < cats.size(); i++) {
            CatCard card = new CatCard(cats.get(i), ART_COLORS[i % ART_COLORS.length]);
            card.setOnSelect(() -> pick(card));
            cards.add(card);
            place(card, x, 384, 360, 216);
            x += 360 + 36;
        }

        PixelButton back = new PixelButton("Back", PixelTheme.CREAM);
        back.setCenterText(true);
        back.addActionListener(e -> navigator.goTo(Screen.MAIN_MENU));
        place(back, MARGIN, 636, 176, 48);

        PixelButton next = new PixelButton("Next", PixelTheme.MINT);
        next.setCenterText(true);
        next.addActionListener(e -> onNext());
        place(next, MARGIN + CONTENT_WIDTH - 176, 636, 176, 48);
    }

    private void pick(CatCard picked) {
        for (CatCard card : cards) {
            card.setSelected(card == picked);
        }
        selectedCat = picked.getCat();
        clearError();
    }

    private void onNext() {
        clearError();
        try {
            session.setPlayerName(nameField.getText());
        } catch (IllegalArgumentException ex) {
            nameField.setError(true);
            showError(ex.getMessage());
            nameField.requestFocusInWindow();
            return;
        }
        try {
            session.setCat(selectedCat);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }
        navigator.goTo(Screen.CATEGORY_SELECT);
    }

    private void showError(String message) {
        errorMessage = message;
        repaint();
        JOptionPane.showMessageDialog(this, message, "Almost there", JOptionPane.WARNING_MESSAGE);
    }

    private void clearError() {
        errorMessage = null;
        nameField.setError(false);
        repaint();
    }

    @Override
    public void refresh() {
        nameField.setText(session.getPlayerName());
        selectedCat = session.getCat();
        for (CatCard card : cards) {
            card.setSelected(selectedCat != null && card.getCat().getName().equals(selectedCat.getName()));
        }
        clearError();
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        PixelTheme.drawText(g2, "PICK YOUR PURR-SONA", MARGIN, 160, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Every good adventure starts with a name and a cat.", MARGIN, 204,
                PixelTheme.body(24f), PixelTheme.BROWN);

        PixelTheme.drawText(g2, "PLAYER NAME", MARGIN, 266, PixelTheme.heading(12f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Up to " + PlayerScore.MAX_NAME_LENGTH + " characters", 643, 312,
                PixelTheme.body(18f), PixelTheme.BROWN);

        if (errorMessage != null) {
            PixelTheme.drawText(g2, errorMessage, MARGIN, 360, PixelTheme.body(20f), PixelTheme.RED);
        }

        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
    }
}
