package interface_adapter.historic_trends;

import java.time.LocalDate;
import java.util.ArrayList;

public class TrendsState {
    private String baseCurrency = "USD";
    private String targetCurrency = "CAD";

    // Sample data holders
    private ArrayList<LocalDate> dates = new ArrayList<>();
    private ArrayList<Double> rates = new ArrayList<>();

    public TrendsState(TrendsState copy) {
        this.baseCurrency = copy.baseCurrency;
        this.targetCurrency = copy.targetCurrency;
        this.dates = copy.dates;
        this.rates = copy.rates;
    }

    public TrendsState() {}

    // Getters and Setters
    public String getBaseCurrency() { return baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public ArrayList<LocalDate> getDates() { return dates; }
    public ArrayList<Double> getRates() { return rates; }

    public void setPair(String base, String target) {
        this.baseCurrency = base;
        this.targetCurrency = target;
    }

    public void setData(ArrayList<LocalDate> dates, ArrayList<Double> rates) {
        this.dates = dates;
        this.rates = rates;
    }
}
