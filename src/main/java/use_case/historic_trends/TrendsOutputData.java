package use_case.historic_trends;

import java.time.LocalDate;
import java.util.ArrayList;

public class TrendsOutputData {
    private final String baseCurrency;
    private final ArrayList<SeriesData> seriesList;
    private final boolean useCaseFailed;

    public TrendsOutputData(String baseCurrency,
                            ArrayList<SeriesData> seriesList,
                            boolean useCaseFailed) {
        this.baseCurrency = baseCurrency;
        this.seriesList = seriesList;
        this.useCaseFailed = useCaseFailed;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public ArrayList<SeriesData> getSeriesList() {
        return seriesList;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }

    public static class SeriesData {
        private final String targetCurrency;
        private final ArrayList<LocalDate> dates;
        private final ArrayList<Double> percents;

        public SeriesData(String targetCurrency,
                          ArrayList<LocalDate> dates,
                          ArrayList<Double> percents) {
            this.targetCurrency = targetCurrency;
            this.dates = dates;
            this.percents = percents;
        }

        public String getTargetCurrency() {
            return targetCurrency;
        }

        public ArrayList<LocalDate> getDates() {
            return dates;
        }

        public ArrayList<Double> getPercents() {
            return percents;
        }
    }
}
