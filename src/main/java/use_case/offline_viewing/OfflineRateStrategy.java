package use_case.offline_viewing;

import use_case.convert.CurrencyRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

/**
 * Strategy that reads from RateCache instead of calling the online API.
 * This is exactly your Use Case 3: Offline viewing.
 */
public class OfflineRateStrategy implements RateAccessStrategy {

    // Currently unused, but kept for symmetry in case you later want names, etc.
    private final CurrencyRepository repo;

    public OfflineRateStrategy(CurrencyRepository repo) {
        this.repo = repo;
    }

    /**
     * Convert using the cached rate for the target currency.
     */
    @Override
    public double convert(String from, String to, double amount) throws ServiceException {
        Map<String, Double> rates = RateCache.loadRates();
        if (rates == null || rates.isEmpty()) {
            throw new ServiceException("Offline data unavailable.");
        }

        Double rate = rates.get(to);
        if (rate == null) {
            throw new ServiceException("Offline rate not available for " + from + " → " + to);
        }

        return amount * rate;
    }

    /**
     * Return the whole cached map (for offline screen showing many rates).
     */
    @Override
    public Map<String, Double> getLatestRates(String base) throws ServiceException {
        Map<String, Double> rates = RateCache.loadRates();
        if (rates == null || rates.isEmpty()) {
            throw new ServiceException("Offline data unavailable.");
        }
        return rates;
    }

    /**
     * You’re not caching historical data, so this is not supported offline.
     */
    @Override
    public SortedMap<LocalDate, Double> getHistoricalRates(
            String from, String to, LocalDate start, LocalDate end) throws ServiceException {
        throw new ServiceException("Offline historical data not available.");
    }

    @Override
    public String getModeName() {
        return "Offline (Cached)";
    }
}
