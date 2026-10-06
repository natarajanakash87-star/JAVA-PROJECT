package service;

/**
 * Thrown when the live job-market data source cannot be reached or
 * returns an unusable response. Callers are expected to catch this and
 * fall back to {@link MockJobMarketService}.
 */
public class JobMarketException extends RuntimeException {
    public JobMarketException(String message) {
        super(message);
    }
}
