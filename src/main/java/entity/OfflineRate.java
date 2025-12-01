package entity;

import java.time.Instant;
import java.util.Map;

public class OfflineRate {
    private final Map<String, Double> rates;
    private final Instant timestamp;

    public OfflineRate(Map<String, Double> rates, Instant timestamp) {
        this.rates = rates;
        this.timestamp = timestamp;
    }

    public Map<String, Double> getRates() { return rates; }
    public Instant getTimestamp() { return timestamp; }
}
