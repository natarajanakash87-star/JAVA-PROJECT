package model;

import java.util.List;
import java.util.Map;

/**
 * Represents a recommended hands-on project chosen because it covers
 * the student's current skill gaps.
 */
public class ProjectRecommendation {
    private String name;
    private List<String> skillsCovered;
    private Map<String, String> gapCoverage; // skill -> High/Medium/Low
    private double relevanceScore;

    public ProjectRecommendation(String name, List<String> skillsCovered,
                                  Map<String, String> gapCoverage, double relevanceScore) {
        this.name = name;
        this.skillsCovered = skillsCovered;
        this.gapCoverage = gapCoverage;
        this.relevanceScore = relevanceScore;
    }

    public String getName() { return name; }
    public List<String> getSkillsCovered() { return skillsCovered; }
    public Map<String, String> getGapCoverage() { return gapCoverage; }
    public double getRelevanceScore() { return relevanceScore; }
}
