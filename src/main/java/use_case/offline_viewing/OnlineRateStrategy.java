package use_case.offline_viewing;

import data_access.ExchangeRateHostDAO;
import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * Strategy that uses the online ExchangeRateHostDAO and also updates
 * the local RateCache so OfflineRateStrategy can use it later.
 */
public class OnlineRateStrategy implements RateAccessStrategy {

    private final ExchangeRateHostDAO api;
    private final CurrencyRepository repo; // not currently used, but kept for future

    public OnlineRateStrategy(ExchangeRateHostDAO api, CurrencyRepository repo) {
        this.api = api;
        this.repo = repo;
    }

    private Currency makeCurrency(String code) {
        // Currency(name, symbol). Name isn't used in conversions, only symbol is.
        return new Currency(code, code);
    }

    @Override
    public double convert(String from, String to, double amount) throws ServiceException {
        try {
            Currency fromCur = makeCurrency(from);
            Currency toCur = makeCurrency(to);

            CurrencyConversion conversion = api.getLatestRate(fromCur, toCur);
            double result = conversion.calculateConvertedAmount(amount);

            // Cache this rate for offline use
            Map<String, Double> rates = new HashMap<>();
            rates.put(to, conversion.getRate());
            Instant ts = conversion.getTimeStamp();
            RateCache.save(rates, ts);

            return result;
        } catch (RuntimeException e) {
            throw new ServiceException("Online conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Double> getLatestRates(String base) throws ServiceException {
        try {
            // Minimal implementation: ensures we at least write a timestamp to cache.
            Currency baseCur = makeCurrency(base);
            CurrencyConversion conversion = api.getLatestRate(baseCur, baseCur);

            Map<String, Double> rates = new HashMap<>();
            rates.put(base, 1.0);
            RateCache.save(rates, conversion.getTimeStamp());

            return rates;
        } catch (RuntimeException e) {
            throw new ServiceException("Online latest rates failed: " + e.getMessage(), e);
        }
    }

    @Override
    public SortedMap<LocalDate, Double> getHistoricalRates(
            String from, String to, LocalDate start, LocalDate end) throws ServiceException {
        throw new ServiceException("Online historical fetch not implemented in this strategy.");
    }

    @Override
    public String getModeName() {
        return "Online (API)";
    }
}
