package use_case.historic_trends;

public class TrendsInputData {
    private final String baseCurrency;
    private final String targetCurrency;

    public TrendsInputData(String baseCurrency, String targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }
}