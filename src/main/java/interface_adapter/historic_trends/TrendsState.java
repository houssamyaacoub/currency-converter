package interface_adapter.historic_trends;

import java.time.LocalDate;
import java.util.ArrayList;

public class TrendsState {

    public static class SeriesData {
        private final String targetCurrency;
        private final ArrayList<LocalDate> dates;
        private final ArrayList<Double> percents;

        public SeriesData(String targetCurrency, ArrayList<LocalDate> dates, ArrayList<Double> percents) {
            this.targetCurrency = targetCurrency;
            this.dates = dates;
            this.percents = percents;
        }

        // Getters needed by the View to build the chart
        public String getTargetCurrency() { return targetCurrency; }
        public ArrayList<LocalDate> getDates() { return dates; }
        public ArrayList<Double> getPercents() { return percents; }
    }

    private String baseCurrency = "USD";
    private String targetCurrency = "CAD";

    // Sample data holders
    private ArrayList<LocalDate> dates = new ArrayList<>();
    private ArrayList<Double> rates = new ArrayList<>();
    private ArrayList<TrendsState.SeriesData> seriesList = new ArrayList<>();
    private String[] currencyCodes = new String[]{};

    private String error = null;

    public TrendsState(TrendsState copy) {
        this.baseCurrency = copy.baseCurrency;
        this.targetCurrency = copy.targetCurrency;
        this.dates = copy.dates;
        this.rates = copy.rates;
        this.currencyCodes = copy.currencyCodes;
        this.error = copy.error;
    }


    public TrendsState() {}

    // Getters and Setters
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public ArrayList<LocalDate> getDates() { return dates; }
    public ArrayList<Double> getRates() { return rates; }

    public ArrayList<SeriesData> getSeriesList() { return seriesList; }
    public void setSeriesList(ArrayList<SeriesData> seriesList) { this.seriesList = seriesList; }

    public void setPair(String base, String target) {
        this.baseCurrency = base;
        this.targetCurrency = target;
    }

    public String[] getCurrencyCodes() { return currencyCodes; }
    public void setCurrencyCodes(String[] currencyCodes) { this.currencyCodes = currencyCodes; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public void setData(ArrayList<LocalDate> dates, ArrayList<Double> rates) {
        this.dates = dates;
        this.rates = rates;
    }

}