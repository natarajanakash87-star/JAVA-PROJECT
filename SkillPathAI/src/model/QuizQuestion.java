package model;

/**
 * Represents a single multiple-choice quiz question used for skill verification.
 */
public class QuizQuestion {
    private String question;
    private String[] options;
    private int correctAnswer; // 0-based index into options
    private String difficulty; // Beginner, Intermediate, Advanced

    public QuizQuestion(String question, String[] options, int correctAnswer, String difficulty) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswer() { return correctAnswer; }
    public String getDifficulty() { return difficulty; }

    public boolean isCorrect(int chosenIndex) {
        return chosenIndex == correctAnswer;
    }
}
