package model;

/**
 * Holds the outcome of an adaptive quiz session for a given skill.
 */
public class QuizResult {
    private String skill;
    private int totalQuestions;
    private int correctAnswers;
    private double score; // percentage 0-100
    private String verifiedLevel;

    public QuizResult(String skill, int totalQuestions, int correctAnswers, double score, String verifiedLevel) {
        this.skill = skill;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.verifiedLevel = verifiedLevel;
    }

    public String getSkill() { return skill; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public double getScore() { return score; }
    public String getVerifiedLevel() { return verifiedLevel; }

    public String toFileLine(int studentId) {
        return studentId + "|" + skill + "|" + totalQuestions + "|" + correctAnswers + "|" + score + "|" + verifiedLevel;
    }
}
