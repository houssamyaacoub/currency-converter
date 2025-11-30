package use_case.historic_trends;

public class TrendsInputData {
    private final String baseCurrency;
    private final String targetCurrency;
    private final String timePeriod;

    public TrendsInputData(String baseCurrency, String targetCurrency,  String timePeriod) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.timePeriod = timePeriod;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
    public String getTargetCurrency() {
        return targetCurrency;
    }
    public String getTimePeriod() { return timePeriod; }
}