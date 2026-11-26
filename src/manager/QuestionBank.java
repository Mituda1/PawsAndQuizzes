package manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import model.Difficulty;
import model.Question;

/**
 * holds every question in memory (but no database just local) and handles add, edit, delete and search
 * screens call these methods and never touch the list directly
 */
public class QuestionBank {

    /** the three topics the game always offers, even when one of them has no questions yet */
    public static final List<String> DEFAULT_CATEGORIES =
            Arrays.asList("General Knowledge", "Java OOP", "Science");

    private final List<Question> questions = new ArrayList<>();

    // -add / edit / delete --

    /** adds a question. rejects a null question or an ID that is already used */
    public void add(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be empty");
        }
        if (indexOf(question.getId()) >= 0) {
            throw new IllegalArgumentException("Question ID " + question.getId() + " already exists");
        }
        questions.add(question);
    }

    /** replaces the question that has the same ID */
    public void update(Question updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Question cannot be empty");
        }
        int index = indexOf(updated.getId());
        if (index < 0) {
            throw new IllegalArgumentException("Question ID " + updated.getId() + " was not found");
        }
        questions.set(index, updated);
    }

    /** deletes the question with this ID and returns it */
    public Question remove(int id) {
        int index = indexOf(id);
        if (index < 0) {
            throw new IllegalArgumentException("Question ID " + id + " was not found");
        }
        return questions.remove(index);
    }

    // ----- look up -----

    public Optional<Question> findById(int id) {
        int index = indexOf(id);
        return index < 0 ? Optional.empty() : Optional.of(questions.get(index));
    }

    /** a copy of the list, so callers cannot change the bank by accident */
    public List<Question> getAll() {
        return new ArrayList<>(questions);
    }

    public int size() {
        return questions.size();
    }

    /** the next free ID (highest ID + 1). use it when adding a new question in the form */
    public int getNextId() {
        int highest = 0;
        for (Question q : questions) {
            highest = Math.max(highest, q.getId());
        }
        return highest + 1;
    }

    /** every category in use, sorted A to Z, with no repeats */
    public List<String> getCategories() {
        TreeSet<String> sorted = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Question q : questions) {
            sorted.add(q.getCategory());
        }
        return new ArrayList<>(sorted);
    }

    /** the default topics plus any other topic in use, sorted A to Z. Used by the add/edit form */
    public List<String> getAllCategories() {
        TreeSet<String> sorted = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        sorted.addAll(DEFAULT_CATEGORIES);
        for (Question q : questions) {
            sorted.add(q.getCategory());
        }
        return new ArrayList<>(sorted);
    }

    // ----- search and filter -----

    /**
     * search and filter in one call. Pass null or blank for any filter you do not want.
     * keyword matches the question text or the ID. typeName is e.g. "True or False"
     */
    public List<Question> filter(String keyword, String category, Difficulty difficulty, String typeName) {
        List<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (matchesKeyword(q, keyword)
                    && matchesText(q.getCategory(), category)
                    && (difficulty == null || q.getDifficulty() == difficulty)
                    && matchesText(q.getTypeName(), typeName)) {
                result.add(q);
            }
        }
        return result;
    }

    /** overloaded shortcut: only category and difficulty (used when a quiz picks its questions) */
    public List<Question> filter(String category, Difficulty difficulty) {
        return filter(null, category, difficulty, null);
    }

    /** how many questions match, e.g. for the "Questions: 45" label on a category card */
    public int count(String category, Difficulty difficulty) {
        return filter(category, difficulty).size();
    }

    // ----- helpers -----

    private int indexOf(int id) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private static boolean matchesText(String actual, String wanted) {
        return wanted == null || wanted.trim().isEmpty() || actual.equalsIgnoreCase(wanted.trim());
    }

    private static boolean matchesKeyword(Question q, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String word = keyword.trim().toLowerCase();
        return q.getText().toLowerCase().contains(word) || String.valueOf(q.getId()).equals(word);
    }
}
