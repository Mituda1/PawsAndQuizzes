package manager;

import java.util.Arrays;
import model.Difficulty;
import model.IdentificationQuestion;
import model.MultipleChoiceQuestion;
import model.TrueFalseQuestion;

/**
 * Starter questions so the game is playable on day one.
 * Add many more later (aim for 10+ per category per difficulty) and check every answer.
 */
public final class SampleQuestions {

    private static final String OOP = "Java OOP";
    private static final String GK = "General Knowledge";
    private static final String SCI = "Science";

    private SampleQuestions() { }

    /** Fills the given bank with the starter questions. */
    public static void loadInto(QuestionBank bank) {
        loadJavaOop(bank);
        loadGeneralKnowledge(bank);
        loadScience(bank);
    }

    private static void loadJavaOop(QuestionBank bank) {
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "Which keyword is used to inherit from a class?",
                OOP, Difficulty.EASY, "A subclass uses 'extends' to inherit from a parent class.",
                Arrays.asList("extends", "implements", "inherits", "super"), 0));
        bank.add(new TrueFalseQuestion(bank.getNextId(), "In Java, a class can extend more than one class.",
                OOP, Difficulty.EASY, "Java allows single inheritance for classes. Use interfaces for multiple types.", false));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(),
                "Which OOP pillar means keeping fields private and using getters and setters?",
                OOP, Difficulty.MEDIUM, "Encapsulation hides the data and controls access to it.",
                Arrays.asList("Polymorphism", "Encapsulation", "Abstraction", "Inheritance"), 1));
        bank.add(new IdentificationQuestion(bank.getNextId(),
                "Which keyword stops a method from being overridden?",
                OOP, Difficulty.MEDIUM, "A final method cannot be overridden by subclasses.", "final"));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "Which statement about abstract classes is true?",
                OOP, Difficulty.HARD,
                "Abstract classes can have constructors, fields and normal methods, but you cannot create one with new.",
                Arrays.asList("They can have constructors", "They can be created with new",
                        "They cannot contain fields", "Every method must be abstract"), 0));
        bank.add(new IdentificationQuestion(bank.getNextId(),
                "What annotation marks a method that overrides a parent method?",
                OOP, Difficulty.HARD, "@Override makes the compiler check that you really override something.",
                Arrays.asList("@Override", "Override")));
    }

    private static void loadGeneralKnowledge(QuestionBank bank) {
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "What is the capital city of the Philippines?",
                GK, Difficulty.EASY, "Manila is the capital. Quezon City is the most populous city.",
                Arrays.asList("Cebu City", "Manila", "Davao City", "Quezon City"), 1));
        bank.add(new TrueFalseQuestion(bank.getNextId(), "The Philippines has more than 7,000 islands.",
                GK, Difficulty.EASY, "The country has about 7,640 islands.", true));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "Which is the largest ocean on Earth?",
                GK, Difficulty.MEDIUM, "The Pacific Ocean covers about a third of the planet.",
                Arrays.asList("Atlantic", "Indian", "Pacific", "Arctic"), 2));
        bank.add(new IdentificationQuestion(bank.getNextId(), "Who is the national hero of the Philippines?",
                GK, Difficulty.MEDIUM, "Dr. Jose Rizal wrote Noli Me Tangere and El Filibusterismo.",
                Arrays.asList("Jose Rizal", "Rizal", "Dr. Jose Rizal")));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(),
                "In what year did the Philippines declare independence from Spain?",
                GK, Difficulty.HARD, "Independence was declared on June 12, 1898.",
                Arrays.asList("1872", "1898", "1935", "1946"), 1));
        bank.add(new TrueFalseQuestion(bank.getNextId(),
                "The Great Wall of China can be seen with the naked eye from the Moon.",
                GK, Difficulty.HARD, "This is a myth. The wall is far too narrow to see from the Moon.", false));
    }

    private static void loadScience(QuestionBank bank) {
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "Which gas do plants take in for photosynthesis?",
                SCI, Difficulty.EASY, "Plants use carbon dioxide, water and light to make food.",
                Arrays.asList("Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen"), 2));
        bank.add(new TrueFalseQuestion(bank.getNextId(), "Water boils at 100 degrees Celsius at sea level.",
                SCI, Difficulty.EASY, "At normal sea-level pressure, pure water boils at 100 C.", true));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(), "Which planet is known as the Red Planet?",
                SCI, Difficulty.MEDIUM, "Mars looks red because of iron oxide (rust) on its surface.",
                Arrays.asList("Venus", "Mars", "Jupiter", "Mercury"), 1));
        bank.add(new IdentificationQuestion(bank.getNextId(), "What is the chemical symbol for gold?",
                SCI, Difficulty.MEDIUM, "Au comes from the Latin word 'aurum'.", "Au"));
        bank.add(new MultipleChoiceQuestion(bank.getNextId(),
                "What is the approximate speed of light in a vacuum?",
                SCI, Difficulty.HARD, "Light travels about 299,792 kilometers per second.",
                Arrays.asList("3,000 km/s", "30,000 km/s", "300,000 km/s", "3,000,000 km/s"), 2));
        bank.add(new TrueFalseQuestion(bank.getNextId(), "Sound travels faster in space than in air.",
                SCI, Difficulty.HARD, "Sound needs matter to travel, so it cannot travel through the vacuum of space.", false));
    }
}
