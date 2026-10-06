package service;

import model.Progress;
import model.Roadmap;
import model.WeeklyPlan;
import util.FileManager;

import java.util.List;

/**
 * Tracks per-skill learning progress for a student and evaluates whether
 * they are struggling, on track, or ahead -- feeding directly into the
 * dynamic roadmap regeneration logic.
 */
public class ProgressTracker {

    public enum Pace { STRUGGLING, ON_TRACK, AHEAD }

    public void recordProgress(Progress progress) {
        FileManager.saveProgress(progress);
    }

    public List<Progress> getProgress(int studentId) {
        return FileManager.loadProgress(studentId);
    }

    public double overallProgress(int studentId) {
        List<Progress> all = getProgress(studentId);
        if (all.isEmpty()) return 0;
        double sum = 0;
        for (Progress p : all) sum += p.getPercentage();
        return sum / all.size();
    }

    /**
     * Compares actual hours spent against the roadmap's expected hours for a
     * skill to classify the student's pace. Used to trigger roadmap
     * adjustments (extra practice hours, or shortening the timeline).
     */
    public Pace evaluatePace(Progress progress, Roadmap roadmap) {
        if (roadmap == null) return Pace.ON_TRACK;
        double expectedHours = 0;
        for (WeeklyPlan w : roadmap.getWeeklyPlans()) {
            if (w.getSkill().equalsIgnoreCase(progress.getSkill())) {
                expectedHours += w.getHours();
            }
        }
        if (expectedHours <= 0) return Pace.ON_TRACK;

        double expectedProgressForHours = Math.min(100, (progress.getHoursSpent() / expectedHours) * 100.0);
        double delta = progress.getPercentage() - expectedProgressForHours;

        if (delta < -15) return Pace.STRUGGLING;
        if (delta > 15) return Pace.AHEAD;
        return Pace.ON_TRACK;
    }

    /** Marks all weekly plans for a fully completed skill as done. */
    public void markSkillCompleted(Roadmap roadmap, String skill) {
        if (roadmap == null) return;
        for (WeeklyPlan w : roadmap.getWeeklyPlans()) {
            if (w.getSkill().equalsIgnoreCase(skill)) {
                w.setCompleted(true);
            }
        }
    }
}
