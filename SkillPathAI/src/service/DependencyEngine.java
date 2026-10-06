package service;

import model.SkillDependency;

import java.util.*;

/**
 * Maintains the skill dependency graph (which skills require which
 * prerequisites) and provides topological ordering so the roadmap
 * generator never schedules an advanced skill before its foundations.
 */
public class DependencyEngine {

    private final Map<String, SkillDependency> graph = new LinkedHashMap<>();

    public DependencyEngine() {
        buildGraph();
    }

    private void addDep(String skill, String... prereqs) {
        graph.put(normalize(skill), new SkillDependency(skill, Arrays.asList(prereqs)));
    }

    private void buildGraph() {
        addDep("OOP", "Java");
        addDep("Collections", "OOP");
        addDep("Exception Handling", "OOP");
        addDep("Spring", "Collections", "Exception Handling");
        addDep("Spring Boot", "Spring");
        addDep("REST API", "Spring Boot");
        addDep("Database", "SQL");
        addDep("Authentication", "REST API", "Database");
        addDep("Backend Project", "Authentication");

        addDep("SQL", "Database Basics");
        addDep("Database Basics");

        addDep("HTML");
        addDep("CSS", "HTML");
        addDep("JavaScript", "HTML", "CSS");
        addDep("React", "JavaScript");
        addDep("Node.js", "JavaScript");

        addDep("Docker", "Linux Basics");
        addDep("Linux Basics");
        addDep("AWS", "Linux Basics");
        addDep("Kubernetes", "Docker");
        addDep("CI/CD", "Git", "Docker");

        addDep("Python");
        addDep("Data Analysis", "Python", "SQL");
        addDep("Pandas", "Python");
        addDep("Machine Learning", "Python", "Data Analysis");
        addDep("TensorFlow", "Machine Learning");

        addDep("Git");
        addDep("Java");

        addDep("Network Security", "Linux Basics");
        addDep("Cybersecurity", "Network Security");
        addDep("Penetration Testing", "Cybersecurity");
    }

    private String normalize(String s) {
        return s.trim().toLowerCase();
    }

    public List<String> getPrerequisites(String skill) {
        SkillDependency dep = graph.get(normalize(skill));
        return dep == null ? Collections.emptyList() : dep.getPrerequisites();
    }

    /**
     * Given a target set of skills, returns a learning order that respects
     * all known prerequisites (topological sort). Unknown skills (no entry
     * in the graph) are treated as having no prerequisites and are appended
     * in their original relative order.
     */
    public List<String> topologicalOrder(List<String> targetSkills) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String skill : targetSkills) {
            visit(skill, visited, visiting, result, targetSkills);
        }
        return result;
    }

    private void visit(String skill, Set<String> visited, Set<String> visiting,
                        List<String> result, List<String> allowedTargets) {
        String key = normalize(skill);
        if (visited.contains(key) || visiting.contains(key)) return;
        visiting.add(key);

        for (String prereq : getPrerequisites(skill)) {
            // Only recurse into prerequisites that are part of the requested
            // target set (or their own dependencies); we don't want to pull
            // in every foundational skill in the whole graph.
            visit(prereq, visited, visiting, result, allowedTargets);
        }
        visiting.remove(key);
        visited.add(key);
        if (!result.contains(skill)) result.add(skill);
    }

    /** Dependency "importance" weight used in priority scoring: skills with
     * more downstream dependents are considered more foundational/important. */
    public double dependencyImportance(String skill) {
        long dependents = graph.values().stream()
                .filter(d -> d.getPrerequisites().stream().anyMatch(p -> p.equalsIgnoreCase(skill)))
                .count();
        return 1.0 + Math.min(1.0, dependents * 0.25); // ranges roughly 1.0 - 2.0
    }
}
