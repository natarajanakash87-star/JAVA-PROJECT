package service;

import data.QuestionBank;
import model.InterviewQuestion;

import java.util.*;

/**
 * Generates and evaluates interview-style questions, prioritizing the
 * student's current weak areas (their critical/gap skills) rather than
 * a fixed generic question bank.
 */
public class InterviewEngine {

    private final Map<String, List<InterviewQuestion>> bank = new HashMap<>();

    /** Minimum keyword coverage (%) for an answer to count as "correct" in the result score. */
    public static final int PASS_PERCENT = 50;

    public InterviewEngine() {
        buildBank();
        loadSkillQuestionBank();
    }

    /** Adds the full per-skill question bank (see data.QuestionBank) to the existing bank. */
    private void loadSkillQuestionBank() {
        for (InterviewQuestion q : QuestionBank.getAllQuestions()) {
            bank.computeIfAbsent(q.getTopic().toLowerCase(), k -> new ArrayList<>()).add(q);
        }
    }

    private void add(String topic, String question, String keywords) {
        bank.computeIfAbsent(topic.toLowerCase(), k -> new ArrayList<>())
                .add(new InterviewQuestion(topic, question, keywords));
    }

    private void buildBank() {
        add("SQL", "What is the difference between INNER JOIN and LEFT JOIN?",
                "inner,left,match,unmatched,null");
        add("SQL", "What is a primary key and why is it important?",
                "unique,identifier,not null,primary key");
        add("SQL", "Explain normalization and why it matters.",
                "redundancy,normal form,dependency,consistency");

        add("Java", "What is the difference between an abstract class and an interface?",
                "abstract,interface,multiple inheritance,implementation");
        add("Java", "Explain the difference between == and .equals() in Java.",
                "reference,value,equals,object");
        add("Java", "What is garbage collection in Java?",
                "memory,heap,unreachable,automatic");

        add("Spring Boot", "What is dependency injection and how does Spring Boot support it?",
                "autowired,ioc,container,bean");
        add("Spring Boot", "What is the purpose of application.properties/application.yml?",
                "configuration,externalized,properties");
        add("Spring Boot", "What is the difference between @Component, @Service, and @Repository?",
                "stereotype,bean,layer,semantic");

        add("REST API", "What makes an API RESTful?",
                "stateless,resource,http methods,uniform interface");
        add("REST API", "What is the difference between PUT and PATCH?",
                "full update,partial update,idempotent");

        add("Docker", "What is the difference between a Docker image and a container?",
                "blueprint,running instance,layer");

        add("General", "Tell me about a project where you had to learn something quickly.",
                "learning,adapt,project,challenge");
    }

    public List<InterviewQuestion> getQuestions(String topic) {
        return bank.getOrDefault(topic.toLowerCase(), bank.get("general"));
    }

    public Set<String> availableTopics() {
        return bank.keySet();
    }

    /** Skills offered in the "Practice by Skill" menu (in display order). */
    public List<String> getPracticeSkills() {
        return QuestionBank.SKILLS;
    }

    /** How many questions exist for a skill (optionally for one difficulty; null = all). */
    public int countQuestions(String skill, String difficulty) {
        int count = 0;
        for (InterviewQuestion q : bank.getOrDefault(skill.toLowerCase(), new ArrayList<>())) {
            if (difficulty == null || q.getDifficulty().equalsIgnoreCase(difficulty)) count++;
        }
        return count;
    }

    /**
     * Randomly picks up to 'count' questions for a skill.
     *  - difficulty = Basic / Intermediate / Advanced -> only that level.
     *  - difficulty = null -> a mixed set, ordered basic -> intermediate -> advanced.
     * The same questions are not repeated within one session.
     */
    public List<InterviewQuestion> pickQuestions(String skill, String difficulty, int count) {
        if (difficulty != null) {
            return randomSubset(questionsOf(skill, difficulty), count);
        }
        // Mixed: split the count across levels, e.g. 5 -> 2 basic, 2 intermediate, 1 advanced
        int basicCount = count / 3 + (count % 3 > 0 ? 1 : 0);
        int advCount = count / 3;
        int interCount = count - basicCount - advCount;
        List<InterviewQuestion> picked = new ArrayList<>();
        picked.addAll(randomSubset(questionsOf(skill, InterviewQuestion.BASIC), basicCount));
        picked.addAll(randomSubset(questionsOf(skill, InterviewQuestion.INTERMEDIATE), interCount));
        picked.addAll(randomSubset(questionsOf(skill, InterviewQuestion.ADVANCED), advCount));
        return picked;
    }

    private List<InterviewQuestion> questionsOf(String skill, String difficulty) {
        List<InterviewQuestion> result = new ArrayList<>();
        for (InterviewQuestion q : bank.getOrDefault(skill.toLowerCase(), new ArrayList<>())) {
            if (q.getDifficulty().equalsIgnoreCase(difficulty)) result.add(q);
        }
        return result;
    }

    private List<InterviewQuestion> randomSubset(List<InterviewQuestion> list, int count) {
        List<InterviewQuestion> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(count, copy.size())));
    }

    /** Percentage (0-100) of the expected keywords found in the answer. */
    public double coveragePercent(InterviewQuestion question, String answer) {
        String[] keywords = question.getIdealAnswerKeywords().split(",");
        String lowerAnswer = answer.toLowerCase();
        int matched = 0;
        for (String k : keywords) {
            if (lowerAnswer.contains(k.trim())) matched++;
        }
        return keywords.length == 0 ? 0 : (matched * 100.0) / keywords.length;
    }

    /** An answer counts as correct when it covers at least PASS_PERCENT of the keywords. */
    public boolean isCorrect(InterviewQuestion question, String answer) {
        return coveragePercent(question, answer) >= PASS_PERCENT;
    }

    /**
     * Lightweight keyword-based feedback -- checks how many of the expected
     * keywords appear in the student's answer, which is enough for a
     * console-based practice tool without needing a full LLM call.
     */
    public String evaluateAnswer(InterviewQuestion question, String answer) {
        double score = coveragePercent(question, answer);

        StringBuilder feedback = new StringBuilder();
        if (score >= 70) {
            feedback.append("Strong answer! You covered the key concepts well.");
        } else if (score >= 35) {
            feedback.append("Decent start, but you're missing some key points. ");
            feedback.append("Consider mentioning: ").append(question.getIdealAnswerKeywords().replace(",", ", "));
        } else {
            feedback.append("This answer needs more depth. Review this topic and try to include concepts like: ");
            feedback.append(question.getIdealAnswerKeywords().replace(",", ", "));
        }
        feedback.append(String.format("\nKeyword coverage: %.0f%%", score));
        return feedback.toString();
    }
}
