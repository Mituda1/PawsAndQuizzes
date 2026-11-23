package model;

import java.util.ArrayList;
import java.util.List;

/** The kinds of question the game supports, with the label shown on screen. */
public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True or False"),
    IDENTIFICATION("Identification");

    private final String label;

    QuestionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static QuestionType fromLabel(String text) {
        for (QuestionType type : values()) {
            if (type.label.equalsIgnoreCase(text == null ? "" : text.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + text);
    }

    /** All labels in order, for dropdown-style buttons. */
    public static List<String> labels() {
        List<String> labels = new ArrayList<>();
        for (QuestionType type : values()) {
            labels.add(type.label);
        }
        return labels;
    }

    @Override
    public String toString() {
        return label;
    }
}
