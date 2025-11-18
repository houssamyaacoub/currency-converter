package use_case.historic_trends;

public class TrendsOutputData {
    private final String baseCurrency;
    private final String targetCurrency;
    private final boolean useCaseFailed;

    public TrendsOutputData(String baseCurrency, String targetCurrency, boolean useCaseFailed) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.useCaseFailed = useCaseFailed;
    }

    public String getBaseCurrency() { return baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public boolean isUseCaseFailed() { return useCaseFailed; }
}