package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import model.AnswerResult;
import model.Cat;
import model.Difficulty;
import model.GameSession;
import model.PlayerScore;
import model.Question;
import model.QuestionType;

/**
 * Runs one quiz from start to finish: picks the questions, shuffles options, grades answers,
 * keeps score, hearts and combo, and builds the final PlayerScore.
 * It has no screen code and no timer. The screen counts the seconds and passes them in,
 * so this class can be tested by itself.
 *
 * The order of use is:  start()  then repeat [ currentQuestion(), submitAnswer(), advance() ]  then finish().
 */
public class QuizEngine {

    public static final int STARTING_HEARTS = 3;

    // two Easy, two Medium, two Hard: the quiz gets harder every two waves
    private static final Difficulty[] WAVE_PLAN = {
        Difficulty.EASY, Difficulty.EASY, Difficulty.MEDIUM, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.HARD
    };

    private enum State { NOT_STARTED, ASKING, FEEDBACK, FINISHED }

    private final QuestionBank bank;
    private final ScoringStrategy scoring;
    private final Random random;

    private State state = State.NOT_STARTED;
    private String playerName = "";
    private Cat cat;
    private List<Question> questions = new ArrayList<>();
    private List<String> currentChoices = new ArrayList<>();
    private int index;
    private int hearts;
    private int score;
    private int streak;
    private int correctCount;
    private int answeredCount;
    private int skipsLeft;
    private int totalSeconds;
    private PlayerScore finalScore;

    public QuizEngine(QuestionBank bank, ScoringStrategy scoring, Random random) {
        this.bank = bank;
        this.scoring = scoring;
        this.random = random;
    }

    /** Normal game: speed-bonus scoring and real randomness. */
    public QuizEngine(QuestionBank bank) {
        this(bank, new SpeedBonusScoring(), new Random());
    }

    // ----- starting -----

    public void start(GameSession session) {
        if (session == null || !session.isReady()) {
            throw new IllegalArgumentException("Pick a name, a cat and a topic first");
        }
        List<Question> picked = pickQuestions(session.getCategory());
        if (picked.isEmpty()) {
            throw new IllegalArgumentException("There are no questions in " + session.getCategory() + " yet");
        }
        questions = picked;
        playerName = session.getPlayerName();
        cat = session.getCat();
        index = 0;
        hearts = STARTING_HEARTS;
        score = 0;
        streak = 0;
        correctCount = 0;
        answeredCount = 0;
        totalSeconds = 0;
        skipsLeft = cat.getFreeSkips();
        finalScore = null;
        prepareCurrent();
        state = State.ASKING;
    }

    private List<Question> pickQuestions(String category) {
        List<Question> picked = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();
        for (Difficulty wanted : WAVE_PLAN) {
            List<Question> pool = unused(bank.filter(category, wanted), usedIds);
            if (pool.isEmpty()) {
                pool = unused(bank.filter(category, null), usedIds);   // not enough of this level: use any
            }
            if (pool.isEmpty()) {
                continue;                                              // the topic ran out: fewer waves
            }
            Question chosen = pool.get(random.nextInt(pool.size()));
            picked.add(chosen);
            usedIds.add(chosen.getId());
        }
        return picked;
    }

    private static List<Question> unused(List<Question> candidates, Set<Integer> usedIds) {
        List<Question> pool = new ArrayList<>();
        for (Question q : candidates) {
            if (!usedIds.contains(q.getId())) {
                pool.add(q);
            }
        }
        return pool;
    }

    private void prepareCurrent() {
        Question q = questions.get(index);
        currentChoices = new ArrayList<>(q.getChoices());
        if (q.getType() == QuestionType.MULTIPLE_CHOICE) {
            Collections.shuffle(currentChoices, random);
        }
    }

    // ----- asking and answering -----

    /** The question being asked (or just answered). Null before start() and after the run ends. */
    public Question currentQuestion() {
        return (state == State.ASKING || state == State.FEEDBACK) ? questions.get(index) : null;
    }

    /** Options in the order to show them: shuffled for multiple choice, fixed for the others. */
    public List<String> currentChoices() {
        return Collections.unmodifiableList(currentChoices);
    }

    /** Seconds allowed for the current question, including the cat's bonus time. */
    public int currentTimeLimit() {
        requireQuestion();
        return questions.get(index).getTimeLimitSeconds() + cat.getBonusSeconds();
    }

    /** Grades the answer. Pass null for answer when time ran out (or call timeUp()). */
    public AnswerResult submitAnswer(String answer, int secondsLeft) {
        requireState(State.ASKING, "No question is waiting for an answer");
        Question q = questions.get(index);
        int limit = currentTimeLimit();
        int left = Math.max(0, Math.min(limit, secondsLeft));
        boolean timedOut = answer == null;
        boolean correct = !timedOut && q.checkAnswer(answer);

        answeredCount++;
        totalSeconds += limit - (timedOut ? 0 : left);

        int points = 0;
        if (correct) {
            correctCount++;
            streak++;
            points = scoring.calculate(q, left, limit, cat, streak);
            score += points;
        } else {
            streak = 0;
            hearts--;
        }
        state = State.FEEDBACK;
        return new AnswerResult(correct, timedOut, points, q.getCorrectAnswerText(),
                q.getExplanation(), hearts, streak);
    }

    public AnswerResult timeUp() {
        return submitAnswer(null, 0);
    }

    public boolean canSkip() {
        return state == State.ASKING && skipsLeft > 0;
    }

    /** Uses one free skip: no points, no lost heart, straight to the next question. */
    public void skip() {
        if (!canSkip()) {
            throw new IllegalStateException("No skips left");
        }
        skipsLeft--;
        moveOn();
    }

    /** Call after the feedback has been shown. */
    public void advance() {
        requireState(State.FEEDBACK, "There is no answer to move on from");
        moveOn();
    }

    private void moveOn() {
        if (hearts <= 0 || index + 1 >= questions.size()) {
            state = State.FINISHED;
        } else {
            index++;
            prepareCurrent();
            state = State.ASKING;
        }
    }

    // ----- finishing -----

    public boolean isFinished() {
        return state == State.FINISHED;
    }

    /** Builds the final score. Safe to call more than once: you always get the same object back. */
    public PlayerScore finish() {
        requireState(State.FINISHED, "The run is not finished yet");
        if (finalScore == null) {
            finalScore = new PlayerScore(playerName, cat.getName(), score, correctCount, answeredCount, totalSeconds);
        }
        return finalScore;
    }

    /** The finished run's score, or null if no run has finished. */
    public PlayerScore getLastScore() {
        return finalScore;
    }

    // ----- numbers for the screens -----

    public int getWave() {
        return index + 1;
    }

    public int getTotalWaves() {
        return questions.size();
    }

    /** True when answering the current question is the last thing left to do in this run. */
    public boolean isLastWave() {
        return index + 1 >= questions.size();
    }

    public int getHearts() {
        return hearts;
    }

    public boolean isGameOver() {
        return state != State.NOT_STARTED && hearts <= 0;
    }

    public int getScore() {
        return score;
    }

    public int getStreak() {
        return streak;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public int getSkipsLeft() {
        return skipsLeft;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public Cat getCat() {
        return cat;
    }

    // ----- checks -----

    private void requireState(State expected, String message) {
        if (state != expected) {
            throw new IllegalStateException(message);
        }
    }

    private void requireQuestion() {
        if (state != State.ASKING && state != State.FEEDBACK) {
            throw new IllegalStateException("There is no current question");
        }
    }
}
