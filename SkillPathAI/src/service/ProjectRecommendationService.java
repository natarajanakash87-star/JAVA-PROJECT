package service;

import model.ProjectRecommendation;
import model.SkillGap;

import java.util.*;

/**
 * Recommends hands-on projects whose required skills best overlap with
 * the student's current skill gaps, so recommendations shift naturally
 * as the student's verified skill state changes.
 */
public class ProjectRecommendationService {

    private static class ProjectTemplate {
        String name;
        List<String> skills;
        ProjectTemplate(String name, List<String> skills) {
            this.name = name;
            this.skills = skills;
        }
    }

    private final List<ProjectTemplate> catalog = new ArrayList<>();

    public ProjectRecommendationService() {
        catalog.add(new ProjectTemplate("Student Management REST API",
                Arrays.asList("Java", "Spring Boot", "REST API", "SQL", "Authentication")));
        catalog.add(new ProjectTemplate("E-Commerce Backend with Payment Simulation",
                Arrays.asList("Java", "Spring Boot", "REST API", "SQL", "Docker")));
        catalog.add(new ProjectTemplate("Personal Portfolio Website",
                Arrays.asList("HTML", "CSS", "JavaScript", "React")));
        catalog.add(new ProjectTemplate("Full Stack Task Manager",
                Arrays.asList("React", "JavaScript", "REST API", "SQL", "Node.js")));
        catalog.add(new ProjectTemplate("Job Market Data Dashboard",
                Arrays.asList("Python", "Data Analysis", "SQL", "Pandas")));
        catalog.add(new ProjectTemplate("Movie Recommendation Engine",
                Arrays.asList("Python", "Machine Learning", "Pandas", "Data Analysis")));
        catalog.add(new ProjectTemplate("Containerized Microservice Deployment",
                Arrays.asList("Docker", "AWS", "REST API", "Spring Boot")));
        catalog.add(new ProjectTemplate("CI/CD Pipeline for a Sample App",
                Arrays.asList("Git", "Docker", "CI/CD", "AWS")));
        catalog.add(new ProjectTemplate("Network Vulnerability Scanner",
                Arrays.asList("Python", "Network Security", "Cybersecurity")));

        catalog.add(new ProjectTemplate("Version-Controlled Static Site",
                Arrays.asList("Git", "HTML", "CSS")));
    }

    /**
     * Scores each project template by how much of its skill list is covered
     * by the student's current critical/gap skills, and returns the
     * top-N most relevant recommendations.
     */
    public List<ProjectRecommendation> recommend(List<SkillGap> gaps, int topN) {
        Map<String, SkillGap> gapMap = new HashMap<>();
        for (SkillGap g : gaps) gapMap.put(g.getSkill().toLowerCase(), g);

        List<ProjectRecommendation> scored = new ArrayList<>();
        for (ProjectTemplate template : catalog) {
            Map<String, String> coverage = new LinkedHashMap<>();
            double relevance = 0;
            int matchedGapSkills = 0;

            for (String skill : template.skills) {
                SkillGap g = gapMap.get(skill.toLowerCase());
                if (g != null && g.getGap() > 5) {
                    matchedGapSkills++;
                    String level = g.getGap() > 40 ? "High" : g.getGap() > 15 ? "Medium" : "Low";
                    coverage.put(skill, level);
                    relevance += g.getPriorityScore();
                }
            }
            if (matchedGapSkills == 0) continue; // irrelevant to this student right now

            scored.add(new ProjectRecommendation(template.name, template.skills, coverage, relevance));
        }

        scored.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        return scored.size() > topN ? scored.subList(0, topN) : scored;
    }
}
