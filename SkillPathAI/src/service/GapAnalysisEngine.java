package service;

import model.SkillGap;
import model.StudentSkill;

import java.util.*;

/**
 * Compares a student's verified skill levels against real-time (or demo)
 * job-market demand to compute gaps and prioritize what to learn next.
 *
 * Priority Score = Market Demand x Skill Gap x Dependency Importance
 */
public class GapAnalysisEngine {

    private final DependencyEngine dependencyEngine;

    public GapAnalysisEngine(DependencyEngine dependencyEngine) {
        this.dependencyEngine = dependencyEngine;
    }

    public List<SkillGap> analyze(List<StudentSkill> verifiedSkills, Map<String, Double> marketDemand) {
        Map<String, StudentSkill> skillMap = new HashMap<>();
        for (StudentSkill s : verifiedSkills) {
            skillMap.put(s.getSkillName().toLowerCase(), s);
        }

        List<SkillGap> gaps = new ArrayList<>();
        for (Map.Entry<String, Double> entry : marketDemand.entrySet()) {
            String skillName = entry.getKey();
            double demand = entry.getValue();
            StudentSkill studentSkill = skillMap.get(skillName.toLowerCase());

            double skillLevel = studentSkill == null ? 0 : studentSkill.numericLevel();
            String verifiedLevel = studentSkill == null ? "Unverified" :
                    (studentSkill.isVerified() ? studentSkill.getVerifiedLevel() : "Unverified (self-reported only)");

            SkillGap gap = new SkillGap(skillName, demand, verifiedLevel, skillLevel);
            double importance = dependencyEngine.dependencyImportance(skillName);
            // Normalize to keep scores in a readable 0-100-ish range.
            double priority = (demand / 100.0) * gap.getGap() * importance;
            gap.setPriorityScore(priority);
            gaps.add(gap);
        }

        // Highest priority first.
        gaps.sort((a, b) -> Double.compare(b.getPriorityScore(), a.getPriorityScore()));
        return gaps;
    }

    /** Builds a max-priority queue for consumers that want to pull top gaps one at a time. */
    public PriorityQueue<SkillGap> priorityQueue(List<SkillGap> gaps) {
        PriorityQueue<SkillGap> pq = new PriorityQueue<>(
                Comparator.comparingDouble(SkillGap::getPriorityScore).reversed());
        pq.addAll(gaps);
        return pq;
    }

    public List<SkillGap> criticalGaps(List<SkillGap> gaps) {
        List<SkillGap> critical = new ArrayList<>();
        for (SkillGap g : gaps) {
            if ("Critical".equals(g.getStatus())) critical.add(g);
        }
        return critical;
    }
}
