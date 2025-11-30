package use_case.historic_trends;

import java.time.LocalDate;
import java.util.ArrayList;

public class TrendsOutputData {
    private final String baseCurrency;
    private final String targetCurrency;
    private final ArrayList<LocalDate> dates;
    private final ArrayList<Double> rates;
    private final boolean useCaseFailed;

    public TrendsOutputData(String baseCurrency, String targetCurrency,
                            ArrayList<LocalDate> dates, ArrayList<Double> rates,
                            boolean useCaseFailed) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.dates = dates;
        this.rates = rates;
        this.useCaseFailed = useCaseFailed;
    }

    public String getBaseCurrency() { return baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public ArrayList<LocalDate> getDates() { return dates; }
    public ArrayList<Double> getRates() { return rates; }
}