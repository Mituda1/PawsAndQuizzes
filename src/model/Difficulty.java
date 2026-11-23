package model;

/** Question difficulty. Each level knows its own base points and answer time. */
public enum Difficulty {
    EASY("Easy", 100, 20),
    MEDIUM("Medium", 200, 25),
    HARD("Hard", 300, 30);

    private final String label;
    private final int basePoints;
    private final int timeLimitSeconds;

    Difficulty(String label, int basePoints, int timeLimitSeconds) {
        this.label = label;
        this.basePoints = basePoints;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getLabel() {
        return label;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    /** Turns text like "hard" or "Hard" into HARD. Useful for dropdowns and forms. */
    public static Difficulty fromLabel(String text) {
        for (Difficulty d : values()) {
            if (d.label.equalsIgnoreCase(text == null ? "" : text.trim())) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown difficulty: " + text);
    }

    @Override
    public String toString() {
        return label;
    }
}
