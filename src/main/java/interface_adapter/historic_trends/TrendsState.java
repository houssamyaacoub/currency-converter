package interface_adapter.historic_trends;

import use_case.historic_trends.TrendsOutputData;

import java.util.ArrayList;

public class TrendsState {
    private String baseCurrency = "USD";
    private ArrayList<TrendsOutputData.SeriesData> seriesList = new ArrayList<>();

    public TrendsState(TrendsState copy) {
        this.baseCurrency = copy.baseCurrency;
        this.seriesList = copy.seriesList;
    }

    public TrendsState() {
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public ArrayList<TrendsOutputData.SeriesData> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(ArrayList<TrendsOutputData.SeriesData> seriesList) {
        this.seriesList = seriesList;
    }
}
