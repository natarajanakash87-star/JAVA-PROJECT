package service;

import model.JobPosting;

import java.util.*;

/**
 * Simulated job-market data source, used whenever the live API
 * ({@link LiveJobMarketService}) is unreachable. Generates a
 * career-specific set of postings with randomized-but-realistic skill
 * distributions so demand percentages are computed dynamically rather
 * than hardcoded per career.
 */
public class MockJobMarketService implements JobMarketService {

    private final Random random = new Random();

    // Baseline skill sets per career; individual postings randomly include
    // a subset of these plus small random noise, so totals are computed,
    // not fixed constants.
    private static final Map<String, List<String>> CAREER_SKILLS = new HashMap<>();
    static {
        CAREER_SKILLS.put("software developer", Arrays.asList(
                "Java", "SQL", "Git", "Spring Boot", "REST API", "Docker", "AWS", "React"));
        CAREER_SKILLS.put("full stack developer", Arrays.asList(
                "JavaScript", "React", "Node.js", "SQL", "HTML", "CSS", "Git", "REST API", "MongoDB"));
        CAREER_SKILLS.put("data scientist", Arrays.asList(
                "Python", "SQL", "Machine Learning", "Pandas", "NumPy", "TensorFlow", "Data Analysis", "Statistics"));
        CAREER_SKILLS.put("data analyst", Arrays.asList(
                "SQL", "Excel", "Python", "Tableau", "Power BI", "Data Analysis", "Statistics"));
        CAREER_SKILLS.put("ai/ml engineer", Arrays.asList(
                "Python", "Machine Learning", "TensorFlow", "PyTorch", "SQL", "Docker", "AWS", "Data Analysis"));
        CAREER_SKILLS.put("cybersecurity analyst", Arrays.asList(
                "Network Security", "Linux", "Python", "Cybersecurity", "Penetration Testing", "SQL", "Cloud Security"));
        CAREER_SKILLS.put("cloud engineer", Arrays.asList(
                "AWS", "Azure", "Docker", "Kubernetes", "Terraform", "Linux", "CI/CD", "Python"));
        CAREER_SKILLS.put("devops engineer", Arrays.asList(
                "Docker", "Kubernetes", "Jenkins", "CI/CD", "AWS", "Linux", "Terraform", "Git", "Ansible"));
    }

    private static final List<String> DEFAULT_SKILLS = Arrays.asList(
            "Java", "Python", "SQL", "Git", "Communication", "Problem Solving");

    @Override
    public List<JobPosting> fetchJobs(String career) {
        List<String> baseline = CAREER_SKILLS.getOrDefault(career.toLowerCase(), DEFAULT_SKILLS);
        List<JobPosting> jobs = new ArrayList<>();
        int totalPostings = 500; // simulated volume, matches spec's sample display

        String[] companies = {"TechNova", "ByteWorks", "CloudSpring", "CodeCraft", "DataForge",
                "NexaSoft", "PixelWave", "InfraCore", "AlgoStack", "DevNest"};

        for (int i = 0; i < totalPostings; i++) {
            List<String> postingSkills = new ArrayList<>();
            for (String skill : baseline) {
                // Each skill appears in this posting with a probability derived from
                // its position (earlier = more "core" = more frequently demanded),
                // plus randomness so results aren't a fixed constant.
                double baseProb = 0.9 - (baseline.indexOf(skill) * 0.08);
                baseProb = Math.max(0.15, baseProb);
                if (random.nextDouble() < baseProb) {
                    postingSkills.add(skill);
                }
            }
            if (postingSkills.isEmpty()) postingSkills.add(baseline.get(0));
            String company = companies[random.nextInt(companies.length)];
            jobs.add(new JobPosting(career + " (Posting " + (i + 1) + ")", company,
                    "Simulated job posting for demo purposes.", postingSkills));
        }
        return jobs;
    }

    @Override
    public Map<String, Double> calculateSkillDemand(List<JobPosting> jobs) {
        Map<String, Integer> counts = new HashMap<>();
        int total = jobs.size();
        for (JobPosting job : jobs) {
            for (String skill : new HashSet<>(job.getSkills())) {
                counts.merge(skill, 1, Integer::sum);
            }
        }
        Map<String, Double> demand = new HashMap<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            demand.put(e.getKey(), total == 0 ? 0 : (e.getValue() * 100.0) / total);
        }
        return demand;
    }

    @Override
    public boolean isLiveData() {
        return false;
    }
}
