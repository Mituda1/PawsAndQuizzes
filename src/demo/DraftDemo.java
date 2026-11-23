package demo;

import java.util.Arrays;
import manager.QuestionBank;
import manager.SampleQuestions;
import model.Difficulty;
import model.Question;
import model.QuestionDraft;
import model.QuestionType;

/**
 * TEMPORARY test program. Right-click and choose "Run File" (Shift+F6).
 * Delete the whole demo package before you submit.
 */
public class DraftDemo {

    public static void main(String[] args) {
        QuestionBank bank = new QuestionBank();
        SampleQuestions.loadInto(bank);

        System.out.println("=== Build a valid multiple-choice question from form text ===");
        QuestionDraft draft = new QuestionDraft();
        draft.setType(QuestionType.MULTIPLE_CHOICE);
        draft.setText("Which planet is closest to the Sun?");
        draft.setCategory("Science");
        draft.setDifficulty(Difficulty.EASY);
        draft.setOptions(Arrays.asList("Venus", "Mercury", "", ""));   // two blanks are skipped
        draft.setCorrectOptionIndex(1);
        Question q = draft.build(bank.getNextId());
        bank.add(q);
        System.out.println(q);
        System.out.println("choices: " + q.getChoices() + "  correct: " + q.getCorrectAnswerText());

        System.out.println();
        System.out.println("=== Edit it: copy into a draft, change one thing, save with the same ID ===");
        QuestionDraft edit = QuestionDraft.from(q);
        edit.setDifficulty(Difficulty.HARD);
        bank.update(edit.build(q.getId()));
        System.out.println(bank.findById(q.getId()).get());

        System.out.println();
        System.out.println("=== Bad forms (these should all be rejected) ===");
        QuestionDraft bad = new QuestionDraft();
        tryBuild("empty text", bad);

        bad.setText("Pick the right one");
        bad.setCategory("Science");
        bad.setOptions(Arrays.asList("Only one", "", "", ""));
        tryBuild("only one option", bad);

        bad.setOptions(Arrays.asList("A", "B", "", ""));
        bad.setCorrectOptionIndex(3);
        tryBuild("correct option left blank", bad);

        QuestionDraft id = new QuestionDraft();
        id.setType(QuestionType.IDENTIFICATION);
        id.setText("Name a planet");
        id.setCategory("Science");
        id.setAcceptedAnswersText(" ,  , ");
        tryBuild("no accepted answers", id);

        id.setCategory(null);
        id.setAcceptedAnswersText("Mars");
        tryBuild("no category", id);

        System.out.println();
        System.out.println("Bank size is now " + bank.size());
    }

    private static void tryBuild(String label, QuestionDraft draft) {
        try {
            draft.build(99);
            System.out.println("PROBLEM: '" + label + "' was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK, rejected " + label + ": " + e.getMessage());
        }
    }
}
