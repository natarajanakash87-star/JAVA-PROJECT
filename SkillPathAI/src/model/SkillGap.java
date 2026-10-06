package model;

/**
 * Represents the computed gap between a student's verified skill level
 * and what the job market currently demands for that skill.
 */
public class SkillGap implements Comparable<SkillGap> {
    private String skill;
    private double marketDemand;   // 0-100 (percentage of postings requiring it)
    private String verifiedLevel;  // textual level (Beginner/Intermediate/Advanced/Expert/Unverified)
    private double skillLevel;     // 0-100 numeric strength
    private double gap;            // marketDemand - skillLevel, floored at 0
    private double priorityScore;  // demand * gap * dependency importance
    private String status;         // Strong / Gap / Critical

    public SkillGap(String skill, double marketDemand, String verifiedLevel, double skillLevel) {
        this.skill = skill;
        this.marketDemand = marketDemand;
        this.verifiedLevel = verifiedLevel;
        this.skillLevel = skillLevel;
        this.gap = Math.max(0, marketDemand - skillLevel);
        this.status = computeStatus();
    }

    private String computeStatus() {
        if (gap <= 5) return "Strong";
        if (gap <= 30) return "Gap";
        return "Critical";
    }

    public String getSkill() { return skill; }
    public double getMarketDemand() { return marketDemand; }
    public String getVerifiedLevel() { return verifiedLevel; }
    public double getSkillLevel() { return skillLevel; }
    public double getGap() { return gap; }
    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }
    public String getStatus() { return status; }

    @Override
    public int compareTo(SkillGap other) {
        // Higher priority score first (used with PriorityQueue via reversed comparator too)
        return Double.compare(other.priorityScore, this.priorityScore);
    }
}
