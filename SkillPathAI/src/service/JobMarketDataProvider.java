package service;

import model.JobPosting;
import util.ConsoleUI;
import util.FileManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Coordinates job-market data retrieval: tries the live API first, and
 * transparently falls back to the mock/demo data source on any failure,
 * per the exception-handling requirements. Results are cached per
 * career for the remainder of the session so repeated menu visits
 * (Job Market Analysis, Gap Analysis) don't refetch every time.
 */
public class JobMarketDataProvider {

    private final JobMarketService liveService = new LiveJobMarketService();
    private final JobMarketService mockService = new MockJobMarketService();

    private String cachedCareer;
    private List<JobPosting> cachedJobs;
    private Map<String, Double> cachedDemand;
    private boolean cachedIsLive;
    private String lastUpdated;

    public Map<String, Double> getSkillDemand(String career) {
        ensureFetched(career);
        return cachedDemand;
    }

    public List<JobPosting> getJobs(String career) {
        ensureFetched(career);
        return cachedJobs;
    }

    public boolean isLiveData(String career) {
        ensureFetched(career);
        return cachedIsLive;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    private void ensureFetched(String career) {
        if (cachedCareer != null && cachedCareer.equalsIgnoreCase(career)) return;

        JobMarketService active;
        List<JobPosting> jobs;
        boolean live;
        try {
            active = liveService;
            jobs = active.fetchJobs(career);
            live = true;
        } catch (JobMarketException e) {
            ConsoleUI.printError("Unable to connect to Job Market API: " + e.getMessage());
            ConsoleUI.printInfo("Switching to demo dataset...");
            active = mockService;
            jobs = active.fetchJobs(career);
            live = false;
        } catch (Exception e) {
            ConsoleUI.printError("Unexpected error contacting Job Market API.");
            ConsoleUI.printInfo("Switching to demo dataset...");
            active = mockService;
            jobs = active.fetchJobs(career);
            live = false;
        }

        cachedCareer = career;
        cachedJobs = jobs;
        cachedDemand = active.calculateSkillDemand(jobs);
        cachedIsLive = live;
        lastUpdated = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Persist a lightweight snapshot for auditability / offline reference.
        FileManager.appendLine("data/job_market.txt",
                career + "|" + (live ? "LIVE" : "DEMO") + "|" + jobs.size() + "|" + lastUpdated);
    }

    /** Forces a refresh on the next call, e.g. after the user asks to re-analyze. */
    public void invalidateCache() {
        cachedCareer = null;
    }
}
