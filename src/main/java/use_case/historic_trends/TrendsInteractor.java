package use_case.historic_trends;

import use_case.convert.ExchangeRateDataAccessInterface;
import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TrendsInteractor implements TrendsInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccessObject;
    private final TrendsOutputBoundary trendsPresenter;
    private final CurrencyRepository currencyRepository;

    public TrendsInteractor(ExchangeRateDataAccessInterface dataAccessObject,
                            CurrencyRepository currencyRepository,
                            TrendsOutputBoundary trendsPresenter) {
        this.dataAccessObject = dataAccessObject;
        this.currencyRepository = currencyRepository;
        this.trendsPresenter = trendsPresenter;
    }

    @Override
    public void execute(TrendsInputData inputData) {
        String baseName = inputData.getBaseCurrency();
        List<String> targetNames = inputData.getTargetCurrencies();
        String period = inputData.getTimePeriod();

        LocalDate end = LocalDate.now();
        LocalDate start = switch (period) {
            case "1 month" -> end.minusMonths(1);
            case "6 months" -> end.minusMonths(6);
            case "1 year" -> end.minusYears(1);
            default -> end.minusWeeks(1);
        };

        Currency base = currencyRepository.getByName(baseName);

        ArrayList<TrendsOutputData.SeriesData> seriesList = new ArrayList<>();

        for (String targetName : targetNames) {
            try {
                Currency target = currencyRepository.getByName(targetName);
                List<CurrencyConversion> conversions =
                        dataAccessObject.getHistoricalRates(base, target, start, end);

                if (conversions.isEmpty()) {
                    continue;
                }

                ArrayList<LocalDate> dates = new ArrayList<>();
                ArrayList<Double> percents = new ArrayList<>();

                double firstRate = conversions.get(0).getRate();

                for (CurrencyConversion conversion : conversions) {
                    LocalDate date = conversion.getTimeStamp()
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate();
                    double rate = conversion.getRate();
                    double pct = (rate / firstRate - 1.0) * 100.0;

                    dates.add(date);
                    percents.add(pct);
                }

                TrendsOutputData.SeriesData seriesData =
                        new TrendsOutputData.SeriesData(targetName, dates, percents);
                seriesList.add(seriesData);

                System.out.println("Fetched " + conversions.size() + " points for "
                        + baseName + " -> " + targetName);
            } catch (Exception e) {
                System.out.println("Skipping target " + targetName + " due to error: " + e.getMessage());
            }
        }

        if (seriesList.isEmpty()) {
            trendsPresenter.prepareFailView("No data available for selected currencies.");
        } else {
            TrendsOutputData output =
                    new TrendsOutputData(baseName, seriesList, false);
            trendsPresenter.prepareSuccessView(output);
        }
    }

    @Override
    public void switchToHomeView() {
        trendsPresenter.prepareHomeView();
    }
}
