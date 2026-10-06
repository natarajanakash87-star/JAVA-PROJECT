package model;

/**
 * A single interview-preparation question tied to a skill area.
 *
 * Each question also carries a difficulty level (Basic / Intermediate /
 * Advanced) so the student can practice a skill step by step.
 */
public class InterviewQuestion {

    // Difficulty levels used across the question bank
    public static final String BASIC = "Basic";
    public static final String INTERMEDIATE = "Intermediate";
    public static final String ADVANCED = "Advanced";

    private String topic;
    private String question;
    private String idealAnswerKeywords; // comma separated keywords used for lightweight feedback
    private String difficulty;

    /** Original constructor (kept so existing code still works). Defaults to Intermediate. */
    public InterviewQuestion(String topic, String question, String idealAnswerKeywords) {
        this(topic, question, idealAnswerKeywords, INTERMEDIATE);
    }

    public InterviewQuestion(String topic, String question, String idealAnswerKeywords, String difficulty) {
        this.topic = topic;
        this.question = question;
        this.idealAnswerKeywords = idealAnswerKeywords;
        this.difficulty = difficulty;
    }

    public String getTopic() { return topic; }
    public String getQuestion() { return question; }
    public String getIdealAnswerKeywords() { return idealAnswerKeywords; }
    public String getDifficulty() { return difficulty; }
}
