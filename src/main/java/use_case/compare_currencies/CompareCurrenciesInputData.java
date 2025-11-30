package use_case.compare_currencies;

import java.util.List;

// Simple data holder for "base" + selected targets
public class CompareCurrenciesInputData {
    private final String baseCurrencyName;
    private final List<String> targetCurrencyNames;

    public CompareCurrenciesInputData(String baseCurrencyName, List<String> targetCurrencyNames) {
        this.baseCurrencyName = baseCurrencyName;
        this.targetCurrencyNames = targetCurrencyNames;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public List<String> getTargetCurrencyNames() {
        return targetCurrencyNames;
    }
}
