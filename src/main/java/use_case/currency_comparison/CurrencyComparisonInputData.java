package use_case.currency_comparison;

import java.util.List;

public class CurrencyComparisonInputData {

    private final String baseCurrency;
    private final List<String> targetCurrencies;

    public CurrencyComparisonInputData(String baseCurrency, List<String> targetCurrencies) {
        this.baseCurrency = baseCurrency;
        this.targetCurrencies = targetCurrencies;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public List<String> getTargetCurrencies() {
        return targetCurrencies;
    }
}
