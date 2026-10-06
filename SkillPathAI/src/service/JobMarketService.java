package service;

import model.JobPosting;

import java.util.List;
import java.util.Map;

/**
 * Abstraction over a source of job-market data. A real implementation
 * could call a live job-postings API; {@link MockJobMarketService} is
 * used whenever no such API/network access is available, and clearly
 * labels its output as simulated data.
 */
public interface JobMarketService {

    List<JobPosting> fetchJobs(String career);

    Map<String, Double> calculateSkillDemand(List<JobPosting> jobs);

    /** Whether the data returned by this service is live/real ("false" = simulated/demo). */
    boolean isLiveData();
}
