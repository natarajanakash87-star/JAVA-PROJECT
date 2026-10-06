package model;

import java.time.LocalDate;
import java.util.List;

/**
 * The full personalized roadmap generated for a student toward a target career.
 */
public class Roadmap {
    private int roadmapId;
    private int studentId;
    private String career;
    private int totalWeeks;
    private List<WeeklyPlan> weeklyPlans;
    private LocalDate generatedDate;

    public Roadmap(int roadmapId, int studentId, String career, List<WeeklyPlan> weeklyPlans) {
        this.roadmapId = roadmapId;
        this.studentId = studentId;
        this.career = career;
        this.weeklyPlans = weeklyPlans;
        this.totalWeeks = weeklyPlans.size();
        this.generatedDate = LocalDate.now();
    }

    public int getRoadmapId() { return roadmapId; }
    public int getStudentId() { return studentId; }
    public String getCareer() { return career; }
    public int getTotalWeeks() { return totalWeeks; }
    public void setTotalWeeks(int totalWeeks) { this.totalWeeks = totalWeeks; }
    public List<WeeklyPlan> getWeeklyPlans() { return weeklyPlans; }
    public void setWeeklyPlans(List<WeeklyPlan> weeklyPlans) {
        this.weeklyPlans = weeklyPlans;
        this.totalWeeks = weeklyPlans.size();
    }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }

    public double overallProgress() {
        if (weeklyPlans.isEmpty()) return 0;
        long completed = weeklyPlans.stream().filter(WeeklyPlan::isCompleted).count();
        return (completed * 100.0) / weeklyPlans.size();
    }
}
