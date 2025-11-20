package use_case.currency_comparison;

import java.util.List;
import java.util.Map;

public interface CurrencyComparisonDataAccessInterface {
    Map<String, Double> getLatestRates(String baseCurrency, List<String> targetCurrencies);
}
