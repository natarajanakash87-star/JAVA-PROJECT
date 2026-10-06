package model;

import java.util.List;

/**
 * Represents the prerequisite chain for a given skill. The roadmap
 * generator must not schedule a skill before its prerequisites are
 * sufficiently verified.
 */
public class SkillDependency {
    private String skill;
    private List<String> prerequisites;

    public SkillDependency(String skill, List<String> prerequisites) {
        this.skill = skill;
        this.prerequisites = prerequisites;
    }

    public String getSkill() { return skill; }
    public List<String> getPrerequisites() { return prerequisites; }
}
