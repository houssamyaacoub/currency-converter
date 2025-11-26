package use_case.historic_trends;

import java.util.List;

public class TrendsInputData {
    private final String baseCurrency;
    private final List<String> targetCurrencies;
    private final String timePeriod;

    public TrendsInputData(String baseCurrency, List<String> targetCurrencies, String timePeriod) {
        this.baseCurrency = baseCurrency;
        this.targetCurrencies = targetCurrencies;
        this.timePeriod = timePeriod;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public List<String> getTargetCurrencies() {
        return targetCurrencies;
    }

    public String getTimePeriod() {
        return timePeriod;
    }
}
