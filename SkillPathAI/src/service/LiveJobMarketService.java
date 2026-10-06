package service;

import model.JobPosting;
import util.ConsoleUI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempts to fetch real, current job postings from the public Remotive
 * job-board API (https://remotive.com/api/remote-jobs) -- no API key
 * required. Since Remotive doesn't return a clean list of "required
 * skills" per posting, this service scans each posting's title,
 * description and tags against a known technical-skill vocabulary to
 * detect which skills are mentioned.
 *
 * Any network failure, timeout, or unexpected response format causes
 * this service to throw {@link JobMarketException}; callers should
 * catch that and fall back to {@link MockJobMarketService}.
 */
public class LiveJobMarketService implements JobMarketService {

    private static final String API_URL = "https://remotive.com/api/remote-jobs?search=";
    private static final int TIMEOUT_SECONDS = 6;

    private static final List<String> SKILL_VOCABULARY = Arrays.asList(
            "Java", "Python", "C++", "C", "JavaScript", "TypeScript", "SQL", "Git",
            "React", "Angular", "Vue", "Spring Boot", "Spring", "Docker", "Kubernetes",
            "AWS", "Azure", "GCP", "REST API", "GraphQL", "HTML", "CSS", "Node.js",
            "MongoDB", "MySQL", "PostgreSQL", "Redis", "CI/CD", "Linux", "Machine Learning",
            "TensorFlow", "PyTorch", "Pandas", "NumPy", "Scikit-learn", "Data Analysis",
            "Excel", "Tableau", "Power BI", "R", "Hadoop", "Spark", "Kafka", "Jenkins",
            "Terraform", "Ansible", "Cybersecurity", "Penetration Testing", "Network Security",
            "Authentication", "Microservices", "Agile", "Scrum", "Unit Testing", "Selenium"
    );

    @Override
    public List<JobPosting> fetchJobs(String career) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();

            String query = java.net.URLEncoder.encode(career, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + query))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new JobMarketException("Job Market API returned status " + response.statusCode());
            }

            List<JobPosting> jobs = parseJobs(response.body());
            if (jobs.isEmpty()) {
                throw new JobMarketException("Job Market API returned no usable postings.");
            }
            return jobs;

        } catch (JobMarketException e) {
            throw e;
        } catch (Exception e) {
            throw new JobMarketException("Unable to connect to Job Market API: " + e.getMessage());
        }
    }

    /**
     * Very lightweight, dependency-free extraction of job objects from the
     * Remotive JSON payload. This avoids requiring an external JSON library
     * so the project compiles with plain javac and no build tool.
     */
    private List<JobPosting> parseJobs(String json) {
        List<JobPosting> jobs = new ArrayList<>();
        // Split on job object boundaries within the "jobs" array.
        Pattern jobPattern = Pattern.compile(
                "\"title\"\\s*:\\s*\"(.*?)\".*?\"company_name\"\\s*:\\s*\"(.*?)\".*?\"description\"\\s*:\\s*\"(.*?)\"",
                Pattern.DOTALL);
        // Because descriptions can be very long/nested HTML, process job-by-job using a simpler split.
        String[] chunks = json.split("\\{\"id\"");
        for (String chunk : chunks) {
            String title = extractField(chunk, "title");
            String company = extractField(chunk, "company_name");
            String description = extractField(chunk, "description");
            String tags = extractArrayField(chunk, "tags");
            if (title == null) continue;

            String haystack = (title + " " + description + " " + tags).toLowerCase();
            List<String> detectedSkills = new ArrayList<>();
            for (String skill : SKILL_VOCABULARY) {
                if (haystack.contains(skill.toLowerCase())) {
                    detectedSkills.add(skill);
                }
            }
            if (!detectedSkills.isEmpty()) {
                jobs.add(new JobPosting(title, company == null ? "Unknown" : company,
                        "(description omitted)", detectedSkills));
            }
        }
        return jobs;
    }

    private String extractField(String chunk, String field) {
        Matcher m = Pattern.compile("\"" + field + "\"\\s*:\\s*\"(.*?)(?<!\\\\)\"", Pattern.DOTALL).matcher(chunk);
        if (m.find()) return m.group(1);
        return null;
    }

    private String extractArrayField(String chunk, String field) {
        Matcher m = Pattern.compile("\"" + field + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(chunk);
        if (m.find()) return m.group(1);
        return "";
    }

    @Override
    public Map<String, Double> calculateSkillDemand(List<JobPosting> jobs) {
        Map<String, Integer> counts = new HashMap<>();
        int total = jobs.size();
        for (JobPosting job : jobs) {
            Set<String> uniqueSkills = new HashSet<>(job.getSkills()); // avoid double counting per posting
            for (String skill : uniqueSkills) {
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
        return true;
    }
}
