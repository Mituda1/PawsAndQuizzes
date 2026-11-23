package ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import manager.QuestionBank;
import model.GameSession;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.CategoryCard;
import ui.components.PixelButton;
import ui.theme.PixelTheme;

/**
 * Screen 3: the player picks a topic. Question counts come live from the QuestionBank,
 * so they update by themselves when questions are added or deleted.
 */
public class CategorySelectPanel extends BaseScreenPanel implements Refreshable {

    // name, icon, description (the colors are in COLORS, in the same order)
    private static final String[][] TOPICS = {
        {"Java OOP", "{ }", "Classes, objects and little lightbulb moments."},
        {"General Knowledge", "?", "A little bit of everything. A whole lot of fun."},
        {"Science", "+", "Small discoveries. Big possibilities."}
    };
    private static final Color[] COLORS = {PixelTheme.PEACH, PixelTheme.YELLOW, PixelTheme.MINT};

    private final Navigator navigator;
    private final GameSession session;
    private final QuestionBank bank;
    private final List<CategoryCard> cards = new ArrayList<>();
    private final PixelButton startButton = new PixelButton("Start", PixelTheme.MINT);

    private String selectedCategory;

    public CategorySelectPanel(Navigator navigator, GameSession session, QuestionBank bank) {
        super("CATEGORY SELECT", "02 / 02");
        this.navigator = navigator;
        this.session = session;
        this.bank = bank;

        int x = MARGIN;
        for (int i = 0; i < TOPICS.length; i++) {
            String name = TOPICS[i][0];
            CategoryCard card = new CategoryCard(name, TOPICS[i][1], TOPICS[i][2], COLORS[i],
                    bank.filter(name, null).size());
            card.setOnSelect(() -> pick(card));
            cards.add(card);
            place(card, x, 232, 360, 300);
            x += 360 + 36;
        }

        PixelButton back = new PixelButton("Back", PixelTheme.CREAM);
        back.setCenterText(true);
        back.addActionListener(e -> navigator.goTo(Screen.PLAYER_SETUP));
        place(back, MARGIN, 636, 176, 48);

        startButton.setCenterText(true);
        startButton.setEnabled(false);
        startButton.addActionListener(e -> onStart());
        place(startButton, MARGIN + CONTENT_WIDTH - 176, 636, 176, 48);
    }

    private void pick(CategoryCard picked) {
        for (CategoryCard card : cards) {
            card.setSelected(card == picked);
        }
        selectedCategory = picked.getName();
        startButton.setEnabled(true);
    }

    private void onStart() {
        if (bank.filter(selectedCategory, null).isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "There are no questions in " + selectedCategory + " yet.\nAdd some in the Question Library.",
                    "Empty topic", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            session.setCategory(selectedCategory);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Almost there", JOptionPane.WARNING_MESSAGE);
            return;
        }
        navigator.goTo(Screen.GAMEPLAY);
    }

    @Override
    public void refresh() {
        selectedCategory = session.getCategory();
        for (CategoryCard card : cards) {
            card.setQuestionCount(bank.filter(card.getName(), null).size());
            card.setSelected(card.getName().equals(selectedCategory));
        }
        startButton.setEnabled(selectedCategory != null);
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        PixelTheme.drawText(g2, "WHAT MAKES YOU CURIOUS?", MARGIN, 160, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        PixelTheme.drawText(g2, "Pick a topic. The living room is waiting.", MARGIN, 204,
                PixelTheme.body(24f), PixelTheme.BROWN);

        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);
        PixelTheme.drawText(g2, "One topic, six cozy waves.", 270, 666, PixelTheme.body(18f), PixelTheme.BROWN);
    }
}
