package use_case.compare_currencies;

import java.util.List;

// What the presenter needs to show the graph
public class CompareCurrenciesOutputData {
    private final String baseCurrencyName;
    private final List<String> targetCurrencyNames;
    private final List<Double> rates; // units of target per 1 base

    public CompareCurrenciesOutputData(String baseCurrencyName,
                                       List<String> targetCurrencyNames,
                                       List<Double> rates) {
        this.baseCurrencyName = baseCurrencyName;
        this.targetCurrencyNames = targetCurrencyNames;
        this.rates = rates;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public List<String> getTargetCurrencyNames() {
        return targetCurrencyNames;
    }

    public List<Double> getRates() {
        return rates;
    }
}
