package ui.screens;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import manager.Leaderboard;
import manager.QuizEngine;
import model.AnswerResult;
import model.Cat;
import model.Difficulty;
import model.GameSession;
import model.PlayerScore;
import model.Question;
import model.QuestionType;
import ui.Navigator;
import ui.Refreshable;
import ui.Screen;
import ui.components.PixelButton;
import ui.components.PixelIcons;
import ui.components.PixelTextField;
import ui.theme.PixelTheme;

/**
 * The quiz page. It shows the question, runs the countdown, and shows the Correct / Wrong feedback.
 * All the rules (grading, points, hearts, which question comes next) are in QuizEngine.
 * This class only draws, counts the seconds, and forwards clicks.
 */
public class QuizPanel extends BaseScreenPanel implements Refreshable {

    private static final int TICK_MS = 100;
    private static final int RIGHT_COLUMN_X = 647;

    private final Navigator navigator;
    private final GameSession session;
    private final QuizEngine engine;
    private final Leaderboard leaderboard;

    private final PixelButton[] answerButtons = new PixelButton[4];
    private final PixelTextField typedField = new PixelTextField(60);
    private final PixelButton submitButton = new PixelButton("Submit", PixelTheme.MINT);
    private final PixelButton continueButton = new PixelButton("Continue", PixelTheme.MINT);
    private final PixelButton skipButton = new PixelButton("Skip", PixelTheme.YELLOW);
    private final Timer timer = new Timer(TICK_MS, e -> onTick());

    private List<String> choices = new ArrayList<>();
    private int limitMillis = 1;
    private int remainingMillis = 1;
    private AnswerResult feedback;          // null while the player is still answering

    public QuizPanel(Navigator navigator, GameSession session, QuizEngine engine, Leaderboard leaderboard) {
        super("SHIELD CHALLENGE", "");
        this.navigator = navigator;
        this.session = session;
        this.engine = engine;
        this.leaderboard = leaderboard;

        for (int i = 0; i < answerButtons.length; i++) {
            PixelButton button = new PixelButton("", PixelTheme.CREAM);
            button.setFont(PixelTheme.body(24f));
            button.setTextPadding(28);
            int choiceIndex = i;
            button.addActionListener(e -> onChoice(choiceIndex));
            answerButtons[i] = place(button, MARGIN, 392, 568, 64);
        }

        place(typedField, MARGIN, 400, 900, 64);
        typedField.setPlaceholder("Type your answer...");
        typedField.addActionListener(e -> onSubmitTyped());       // Enter key
        submitButton.setCenterText(true);
        submitButton.addActionListener(e -> onSubmitTyped());
        place(submitButton, 983, 400, 232, 64);

        PixelButton quit = new PixelButton("Quit", PixelTheme.PEACH);
        quit.setCenterText(true);
        quit.addActionListener(e -> onQuit());
        place(quit, MARGIN, 636, 176, 48);

        skipButton.setCenterText(true);
        skipButton.addActionListener(e -> onSkip());
        place(skipButton, 251, 636, 224, 48);

        continueButton.setCenterText(true);
        continueButton.addActionListener(e -> onContinue());
        place(continueButton, MARGIN + CONTENT_WIDTH - 224, 636, 224, 48);

        // The countdown must never keep running while another screen is showing.
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                timer.stop();
            }
        });
    }

    // ----- flow -----

    /** Called every time this screen is shown: starts a fresh run. */
    @Override
    public void refresh() {
        try {
            engine.start(session);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot start", JOptionPane.WARNING_MESSAGE);
            navigator.goTo(Screen.CATEGORY_SELECT);
            return;
        }
        showQuestion();
    }

    private void showQuestion() {
        Question q = engine.currentQuestion();
        choices = new ArrayList<>(engine.currentChoices());
        feedback = null;
        setStepText("WAVE " + engine.getWave() + " / " + engine.getTotalWaves());

        boolean typed = q.getType() == QuestionType.IDENTIFICATION;
        boolean trueFalse = q.getType() == QuestionType.TRUE_FALSE;
        for (int i = 0; i < answerButtons.length; i++) {
            PixelButton button = answerButtons[i];
            boolean show = !typed && i < choices.size();
            button.setVisible(show);
            if (!show) {
                continue;
            }
            if (trueFalse) {
                button.setText(choices.get(i));
                button.setCenterText(true);
                setDesignBounds(button, i == 0 ? MARGIN : RIGHT_COLUMN_X, 400, 568, 88);
            } else {
                button.setText((char) ('A' + i) + "   " + choices.get(i));
                button.setCenterText(false);
                setDesignBounds(button, i % 2 == 0 ? MARGIN : RIGHT_COLUMN_X, 392 + (i / 2) * 84, 568, 64);
            }
        }
        typedField.setVisible(typed);
        submitButton.setVisible(typed);
        typedField.setText("");
        if (typed) {
            SwingUtilities.invokeLater(typedField::requestFocusInWindow);
        }

        continueButton.setVisible(false);
        skipButton.setVisible(engine.getCat().getFreeSkips() > 0);   // only cats with a skip perk
        skipButton.setEnabled(engine.canSkip());
        skipButton.setText(engine.getSkipsLeft() > 0 ? "Skip (" + engine.getSkipsLeft() + ")" : "Skip");

        limitMillis = engine.currentTimeLimit() * 1000;
        remainingMillis = limitMillis;
        timer.restart();
        repaint();
    }

    private void onTick() {
        remainingMillis -= TICK_MS;
        if (remainingMillis <= 0) {
            remainingMillis = 0;
            timer.stop();
            feedback = engine.timeUp();
            showFeedback();
        } else {
            repaint();
        }
    }

    private void onChoice(int index) {
        submit(choices.get(index));
    }

    private void onSubmitTyped() {
        if (!typedField.isVisible() || feedback != null) {
            return;
        }
        if (typedField.getText().trim().isEmpty()) {
            typedField.setError(true);
            JOptionPane.showMessageDialog(this, "Type an answer first.", "Almost there", JOptionPane.WARNING_MESSAGE);
            typedField.setError(false);
            return;
        }
        submit(typedField.getText());
    }

    private void submit(String answer) {
        timer.stop();
        int secondsLeft = (int) Math.ceil(remainingMillis / 1000.0);
        feedback = engine.submitAnswer(answer, secondsLeft);
        showFeedback();
    }

    private void showFeedback() {
        for (PixelButton button : answerButtons) {
            button.setVisible(false);
        }
        typedField.setVisible(false);
        submitButton.setVisible(false);
        skipButton.setVisible(false);
        continueButton.setText(engine.isLastWave() || engine.getHearts() <= 0 ? "See results" : "Continue");
        continueButton.setVisible(true);
        repaint();
    }

    private void onContinue() {
        engine.advance();
        nextOrFinish();
    }

    private void onSkip() {
        if (engine.canSkip()) {
            timer.stop();
            engine.skip();
            nextOrFinish();
        }
    }

    private void nextOrFinish() {
        if (engine.isFinished()) {
            PlayerScore score = engine.finish();
            leaderboard.add(score);
            navigator.goTo(Screen.RESULTS);
        } else {
            showQuestion();
        }
    }

    private void onQuit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Leave this run? Your progress will be lost.", "Quit run", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            timer.stop();
            navigator.goTo(Screen.MAIN_MENU);
        }
    }

    // ----- drawing -----

    @Override
    protected void paintContent(Graphics2D g2) {
        PixelTheme.drawText(g2, "A LITTLE BRAIN STRETCH", MARGIN, 142, PixelTheme.heading(32f), PixelTheme.OUTLINE);
        g2.setColor(PixelTheme.OUTLINE);
        g2.fillRect(MARGIN, 616, CONTENT_WIDTH, 2);

        Question q = engine.currentQuestion();
        if (q == null) {
            return;
        }
        drawHud(g2);
        drawInfoBar(g2, q);
        drawTimer(g2);
        drawQuestion(g2, q);
        if (feedback != null) {
            drawFeedback(g2);
        } else {
            g2.setFont(PixelTheme.body(18f));
            int hintWidth = g2.getFontMetrics().stringWidth("Right answers pop shields.");
            PixelTheme.drawText(g2, "Right answers pop shields.", MARGIN + CONTENT_WIDTH - hintWidth, 666,
                    PixelTheme.body(18f), PixelTheme.BROWN);
        }
    }

    private void drawHud(Graphics2D g2) {
        String scoreText = String.format("SCORE %06d", engine.getScore());
        g2.setFont(PixelTheme.heading(16f));
        int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
        int scoreX = MARGIN + CONTENT_WIDTH - scoreWidth;
        PixelTheme.drawText(g2, scoreText, scoreX, 142, PixelTheme.heading(16f), PixelTheme.OUTLINE);

        int pixel = 4;
        int heartWidth = PixelIcons.heartWidth(pixel);
        int x = scoreX - 28 - QuizEngine.STARTING_HEARTS * (heartWidth + 8);
        for (int i = 0; i < QuizEngine.STARTING_HEARTS; i++) {
            PixelIcons.drawHeart(g2, x + i * (heartWidth + 8), 120, pixel, i < engine.getHearts());
        }
    }

    private void drawInfoBar(Graphics2D g2, Question q) {
        PixelTheme.drawBox(g2, MARGIN, 164, CONTENT_WIDTH, 40, PixelTheme.PEACH, PixelTheme.OUTLINE, 2);
        g2.setFont(PixelTheme.heading(12f));
        FontMetrics fm = g2.getFontMetrics();

        int x = MARGIN + 16;
        PixelTheme.drawText(g2, q.getCategory(), x, 189, PixelTheme.heading(12f), PixelTheme.OUTLINE);
        x += fm.stringWidth(q.getCategory()) + 24;

        x = chip(g2, x, "+" + q.getBasePoints() + " PTS", PixelTheme.YELLOW) + 12;
        chip(g2, x, q.getDifficulty().getLabel().toUpperCase(), difficultyColor(q.getDifficulty()));

        Cat cat = engine.getCat();
        String perk = cat.getName().toUpperCase() + "  " + cat.getPerkDescription().toUpperCase();
        PixelTheme.drawText(g2, perk, MARGIN + CONTENT_WIDTH - 16 - fm.stringWidth(perk), 189,
                PixelTheme.heading(12f), PixelTheme.OUTLINE);
    }

    /** Draws a small label chip and returns the x position just after it. */
    private int chip(Graphics2D g2, int x, String text, Color color) {
        g2.setFont(PixelTheme.heading(12f));
        int width = g2.getFontMetrics().stringWidth(text) + 20;
        PixelTheme.drawBox(g2, x, 172, width, 24, color, PixelTheme.OUTLINE, 2);
        PixelTheme.drawText(g2, text, x + 10, 189, PixelTheme.heading(12f), PixelTheme.OUTLINE);
        return x + width;
    }

    private static Color difficultyColor(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return PixelTheme.MINT;
            case MEDIUM: return PixelTheme.YELLOW;
            default: return PixelTheme.RED;
        }
    }

    private void drawTimer(Graphics2D g2) {
        int secondsLeft = (int) Math.ceil(remainingMillis / 1000.0);
        PixelTheme.drawText(g2, "TIME " + secondsLeft + "s", MARGIN, 240, PixelTheme.heading(12f), PixelTheme.OUTLINE);

        int barX = 190;
        int barWidth = MARGIN + CONTENT_WIDTH - barX;
        PixelTheme.drawBox(g2, barX, 224, barWidth, 20, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);
        double ratio = remainingMillis / (double) limitMillis;
        Color fill = ratio > 0.5 ? PixelTheme.MINT : (ratio > 0.25 ? PixelTheme.YELLOW : PixelTheme.RED);
        g2.setColor(fill);
        g2.fillRect(barX + 2, 226, (int) ((barWidth - 4) * ratio), 16);
    }

    private void drawQuestion(Graphics2D g2, Question q) {
        PixelTheme.drawBox(g2, MARGIN, 256, CONTENT_WIDTH, 120, PixelTheme.CREAM, PixelTheme.OUTLINE, 2);
        g2.setFont(PixelTheme.body(28f));
        List<String> lines = PixelTheme.wrap(g2.getFontMetrics(), q.getText(), CONTENT_WIDTH - 48);
        g2.setColor(PixelTheme.OUTLINE);
        int y = 304;
        for (int i = 0; i < Math.min(lines.size(), 3); i++) {
            g2.drawString(lines.get(i), MARGIN + 24, y);
            y += 36;
        }
    }

    private void drawFeedback(Graphics2D g2) {
        boolean correct = feedback.isCorrect();
        String banner;
        if (correct) {
            banner = "SHIELD BROKEN!  +" + feedback.getPoints() + " PTS";
        } else if (feedback.isTimedOut()) {
            banner = "TIME'S UP!  -1 HEART";
        } else {
            banner = "OOPS!  -1 HEART";
        }
        if (feedback.getHeartsLeft() <= 0) {
            banner += "  NO HEARTS LEFT";
        }
        PixelTheme.drawBox(g2, MARGIN, 392, CONTENT_WIDTH, 52,
                correct ? PixelTheme.MINT : PixelTheme.RED, PixelTheme.OUTLINE, 2);
        PixelTheme.drawText(g2, banner, MARGIN + 24, 426, PixelTheme.heading(18f), PixelTheme.OUTLINE);
        if (correct && feedback.getStreak() > 1) {
            String combo = "COMBO x" + feedback.getStreak();
            g2.setFont(PixelTheme.heading(14f));
            int width = g2.getFontMetrics().stringWidth(combo);
            PixelTheme.drawText(g2, combo, MARGIN + CONTENT_WIDTH - 24 - width, 424,
                    PixelTheme.heading(14f), PixelTheme.OUTLINE);
        }

        PixelTheme.drawText(g2, "CORRECT ANSWER", MARGIN, 478, PixelTheme.heading(12f), PixelTheme.BROWN);
        PixelTheme.drawText(g2, feedback.getCorrectAnswer(), MARGIN, 512, PixelTheme.body(28f), PixelTheme.OUTLINE);

        g2.setFont(PixelTheme.body(20f));
        List<String> lines = PixelTheme.wrap(g2.getFontMetrics(), feedback.getExplanation(), CONTENT_WIDTH);
        g2.setColor(PixelTheme.BROWN);
        int y = 548;
        for (int i = 0; i < Math.min(lines.size(), 2); i++) {
            g2.drawString(lines.get(i), MARGIN, y);
            y += 28;
        }
    }
}
