package use_case.offline_viewing;

import java.time.Instant;
import java.util.Map;

public class OfflineViewOutputData {

    private final Map<String, Double> rates;
    private final Instant timestamp;

    public OfflineViewOutputData(Map<String, Double> rates, Instant timestamp) {
        this.rates = rates;
        this.timestamp = timestamp;
    }

    public Map<String, Double> getRates() { return rates; }
    public Instant getTimestamp() { return timestamp; }
}
