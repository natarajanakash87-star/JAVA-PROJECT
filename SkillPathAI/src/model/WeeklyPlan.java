package model;

import java.util.List;

/**
 * A single week's worth of a personalized roadmap.
 */
public class WeeklyPlan {
    private int weekNumber;
    private String skill;
    private double hours;
    private List<String> topics;
    private List<String> tasks;
    private String project;
    private boolean completed;

    public WeeklyPlan(int weekNumber, String skill, double hours, List<String> topics,
                       List<String> tasks, String project) {
        this.weekNumber = weekNumber;
        this.skill = skill;
        this.hours = hours;
        this.topics = topics;
        this.tasks = tasks;
        this.project = project;
        this.completed = false;
    }

    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public List<String> getTopics() { return topics; }
    public List<String> getTasks() { return tasks; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
