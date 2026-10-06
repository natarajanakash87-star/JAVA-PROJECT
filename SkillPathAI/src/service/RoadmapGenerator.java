package service;

import model.*;

import java.util.*;

/**
 * The heart of SkillPath AI's intelligence pipeline. Turns a student's
 * verified skill state, prioritized skill gaps, and dependency graph
 * into a week-by-week roadmap sized to their available learning time.
 *
 * This is intentionally NOT a lookup table of fixed roadmaps per career:
 * every number here (weeks, hours, which skill comes first) is derived
 * from the student's actual verified skill levels and gaps, so two
 * students targeting the same career can receive very different plans.
 */
public class RoadmapGenerator {

    private static final int LEARNING_DAYS_PER_WEEK = 6;
    private static final Random random = new Random();

    private final DependencyEngine dependencyEngine;

    // Baseline hours required to take a skill from 0 to fully market-ready (100%).
    // The actual hours scheduled for a student are this value scaled by their
    // remaining gap percentage, so someone already strong in a skill needs far less time.
    private static final Map<String, Double> BASE_HOURS = new HashMap<>();
    static {
        BASE_HOURS.put("java", 50.0);
        BASE_HOURS.put("python", 45.0);
        BASE_HOURS.put("sql", 30.0);
        BASE_HOURS.put("git", 12.0);
        BASE_HOURS.put("html", 15.0);
        BASE_HOURS.put("css", 18.0);
        BASE_HOURS.put("javascript", 40.0);
        BASE_HOURS.put("react", 35.0);
        BASE_HOURS.put("spring boot", 40.0);
        BASE_HOURS.put("rest api", 20.0);
        BASE_HOURS.put("docker", 20.0);
        BASE_HOURS.put("aws", 30.0);
        BASE_HOURS.put("kubernetes", 30.0);
        BASE_HOURS.put("machine learning", 55.0);
        BASE_HOURS.put("data analysis", 35.0);
        BASE_HOURS.put("pandas", 20.0);
        BASE_HOURS.put("tensorflow", 35.0);
        BASE_HOURS.put("network security", 30.0);
        BASE_HOURS.put("cybersecurity", 35.0);
        BASE_HOURS.put("penetration testing", 30.0);
        BASE_HOURS.put("ci/cd", 20.0);
        BASE_HOURS.put("node.js", 30.0);
    }

    private static final Map<String, List<String>> TOPICS = new HashMap<>();
    static {
        TOPICS.put("java", Arrays.asList("OOP", "Collections", "Exception Handling", "Generics", "Multithreading"));
        TOPICS.put("sql", Arrays.asList("SELECT", "JOIN", "GROUP BY", "Subqueries", "Indexing"));
        TOPICS.put("spring boot", Arrays.asList("Spring fundamentals", "REST API", "Controllers", "Services", "Spring Data JPA"));
        TOPICS.put("rest api", Arrays.asList("HTTP methods", "Status codes", "Request/Response design", "Versioning"));
        TOPICS.put("docker", Arrays.asList("Images vs Containers", "Dockerfile", "Volumes", "docker-compose"));
        TOPICS.put("aws", Arrays.asList("EC2", "S3", "IAM", "Lambda"));
        TOPICS.put("git", Arrays.asList("Branching", "Merging", "Rebasing", "Pull Requests"));
        TOPICS.put("python", Arrays.asList("Syntax & Data Types", "Functions", "OOP in Python", "File Handling"));
        TOPICS.put("machine learning", Arrays.asList("Supervised Learning", "Model Evaluation", "Feature Engineering"));
        TOPICS.put("data analysis", Arrays.asList("Data Cleaning", "Pandas Basics", "Visualization", "Statistics"));
        TOPICS.put("react", Arrays.asList("Components", "State & Props", "Hooks", "Routing"));
        TOPICS.put("javascript", Arrays.asList("ES6+ Syntax", "DOM Manipulation", "Async/Await", "Promises"));
        TOPICS.put("html", Arrays.asList("Semantic Elements", "Forms", "Accessibility"));
        TOPICS.put("css", Arrays.asList("Flexbox", "Grid", "Responsive Design"));
        TOPICS.put("network security", Arrays.asList("Firewalls", "VPNs", "Threat Modeling"));
        TOPICS.put("cybersecurity", Arrays.asList("Security Fundamentals", "Risk Assessment", "Incident Response"));
    }

    public RoadmapGenerator(DependencyEngine dependencyEngine) {
        this.dependencyEngine = dependencyEngine;
    }

    /**
     * Generates a complete roadmap.
     *
     * @param student        the student (used for id, career, hours/day)
     * @param gaps           prioritized skill gaps (already sorted by priority score)
     * @param roadmapIdSeed  an id to assign to the generated roadmap
     */
    public Roadmap generate(Student student, List<SkillGap> gaps, int roadmapIdSeed) {
        double weeklyHours = Math.max(1, student.getHoursPerDay()) * LEARNING_DAYS_PER_WEEK;

        // Only schedule skills that actually need work (gap > 5%), highest priority first.
        List<SkillGap> toSchedule = new ArrayList<>();
        for (SkillGap g : gaps) {
            if (g.getGap() > 5) toSchedule.add(g);
        }
        if (toSchedule.isEmpty()) {
            // Nothing to learn -- student is already market ready. Still return an
            // empty-but-valid roadmap rather than crashing.
            return new Roadmap(roadmapIdSeed, student.getStudentId(), student.getTargetCareer(), new ArrayList<>());
        }

        List<String> targetSkillNames = new ArrayList<>();
        for (SkillGap g : toSchedule) targetSkillNames.add(g.getSkill());

        // Respect prerequisites: reorder using topological sort over the target set.
        List<String> orderedSkills = dependencyEngine.topologicalOrder(targetSkillNames);

        // Re-attach gap data to the (possibly reordered / prerequisite-expanded) skill list.
        Map<String, SkillGap> gapBySkill = new HashMap<>();
        for (SkillGap g : toSchedule) gapBySkill.put(g.getSkill().toLowerCase(), g);

        List<WeeklyPlan> weeklyPlans = new ArrayList<>();
        int weekCounter = 1;

        for (String skill : orderedSkills) {
            SkillGap gap = gapBySkill.get(skill.toLowerCase());
            double gapPercent = gap != null ? gap.getGap() : 40.0; // default assumption for prerequisite-only skills
            double baseHours = BASE_HOURS.getOrDefault(skill.toLowerCase(), 20.0);
            double neededHours = baseHours * (gapPercent / 100.0);
            neededHours = Math.max(neededHours, weeklyHours * 0.4); // ensure at least a meaningful chunk of time

            int weeksForSkill = (int) Math.ceil(neededHours / weeklyHours);
            weeksForSkill = Math.max(1, weeksForSkill);

            List<String> topics = new ArrayList<>(TOPICS.getOrDefault(skill.toLowerCase(),
                    Arrays.asList("Fundamentals", "Hands-on Practice", "Applied Project Work")));

            double remainingHours = neededHours;
            for (int w = 0; w < weeksForSkill; w++) {
                double hoursThisWeek = Math.min(weeklyHours, remainingHours <= 0 ? weeklyHours : remainingHours);
                remainingHours -= hoursThisWeek;

                List<String> weekTopics = new ArrayList<>();
                int topicsPerWeek = Math.max(1, (int) Math.ceil((double) topics.size() / weeksForSkill));
                int start = w * topicsPerWeek;
                for (int t = start; t < Math.min(topics.size(), start + topicsPerWeek); t++) {
                    weekTopics.add(topics.get(t));
                }
                if (weekTopics.isEmpty()) weekTopics.add("Review & Practice");

                List<String> tasks = new ArrayList<>();
                int problems = 3 + random.nextInt(4); // 3-6 practice problems, varies per week
                tasks.add(problems + " coding/practice problems");
                if (w == weeksForSkill - 1) {
                    tasks.add("Mini project applying " + skill);
                }

                String project = (w == weeksForSkill - 1) ? ("Applied exercise: " + skill) : "";

                weeklyPlans.add(new WeeklyPlan(weekCounter++, capitalize(skill), hoursThisWeek, weekTopics, tasks, project));
            }
        }

        return new Roadmap(roadmapIdSeed, student.getStudentId(), student.getTargetCareer(), weeklyPlans);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public double weeklyHoursFor(Student student) {
        return Math.max(1, student.getHoursPerDay()) * LEARNING_DAYS_PER_WEEK;
    }
}
