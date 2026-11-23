package model;

/**
 * The choices for the current run: who is playing, with which cat, on which topic.
 * Screens fill it in step by step, and the quiz reads it when the game starts.
 * Every setter checks its value and throws IllegalArgumentException if it is not valid.
 */
public class GameSession {

    private String playerName = "";
    private Cat cat;
    private String category;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.trim().length() > PlayerScore.MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Name must be " + PlayerScore.MAX_NAME_LENGTH + " characters or fewer");
        }
        this.playerName = name.trim();
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        if (cat == null) {
            throw new IllegalArgumentException("Pick a cat first");
        }
        this.cat = cat;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Pick a category first");
        }
        this.category = category.trim();
    }

    public boolean isReady() {
        return !playerName.isEmpty() && cat != null && category != null;
    }

    public void reset() {
        playerName = "";
        cat = null;
        category = null;
    }

    public String summary() {
        return "Player: " + (playerName.isEmpty() ? "-" : playerName)
                + "  |  Cat: " + (cat == null ? "-" : cat.getName())
                + "  |  Topic: " + (category == null ? "-" : category);
    }
}
