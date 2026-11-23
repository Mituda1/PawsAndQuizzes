package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.PlayerScore;

/** Keeps every finished run, best first. Lives in memory for the current session. */
public class Leaderboard {

    private final List<PlayerScore> scores = new ArrayList<>();

    public void add(PlayerScore score) {
        if (score == null) {
            throw new IllegalArgumentException("Score cannot be empty");
        }
        scores.add(score);
        Collections.sort(scores);   // PlayerScore knows how to rank itself
    }

    /** Best n runs. If there are fewer than n, returns what exists. */
    public List<PlayerScore> getTop(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of entries cannot be negative");
        }
        return new ArrayList<>(scores.subList(0, Math.min(n, scores.size())));
    }

    public List<PlayerScore> getAll() {
        return new ArrayList<>(scores);
    }

    /** 1 for the best run. Returns -1 if the score is not on the board. */
    public int rankOf(PlayerScore score) {
        int index = scores.indexOf(score);
        return index < 0 ? -1 : index + 1;
    }

    public int size() {
        return scores.size();
    }

    public void clear() {
        scores.clear();
    }
}
