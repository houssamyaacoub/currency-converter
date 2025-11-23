package use_case.historic_trends;

import use_case.convert.ExchangeRateDataAccessInterface;
import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
        String targetName = inputData.getTargetCurrency();
        String period = inputData.getTimePeriod();

        // 1. Calculate Dates based on period (Business Logic)
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period) {
            case "1 month" -> end.minusMonths(1);
            case "6 months" -> end.minusMonths(6);
            case "1 year" -> end.minusYears(1);
            default -> end.minusWeeks(1); // default is 1 week
        };

        // 2. Create Entity shells for the DAO
        Currency base = currencyRepository.getByName(baseName);
        Currency target = currencyRepository.getByName(targetName);

        try {
            // 3. Call DAO and get list of ENTITIES
            List<CurrencyConversion> conversions = dataAccessObject.getHistoricalRates(base, target, start, end);

            // 4. Unpack Entities into primitive lists for the View
            // (The View doesn't need the full Conversion object, just X and Y values for the graph)
            ArrayList<LocalDate> dates = new ArrayList<>();
            ArrayList<Double> rates = new ArrayList<>();

            for (CurrencyConversion conversion: conversions) {
                // Convert Instant -> Date (legacy Date object needed for JFreeChart)
                LocalDate date = conversion.getTimeStamp()
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate();
                dates.add(date);
                rates.add(conversion.getRate());
            }

            // 5. Output
            TrendsOutputData output = new TrendsOutputData(baseName, targetName, dates, rates, false);
            trendsPresenter.prepareSuccessView(output);

        } catch (Exception e) {
            e.printStackTrace();
            trendsPresenter.prepareFailView("Error: " + e.getMessage());
        }
    }

    @Override
    public void switchToHomeView() {
        trendsPresenter.prepareHomeView();
    }
}