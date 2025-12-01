package data_access.offline_viewing;

import data_access.RateCache;
import entity.OfflineRate;

import java.time.Instant;
import java.util.Map;

public class OfflineRateStrategyImpl implements OfflineRateStrategy {

    @Override
    public OfflineRate loadRates() throws Exception {
        Map<String, Double> rates = RateCache.loadRates();
        Instant ts = RateCache.loadTimestamp();

        if (rates == null || rates.isEmpty() || ts == null) {
            throw new Exception("No cached offline data found.");
        }

        return new OfflineRate(rates, ts);
    }
}
