package use_case.historic_trends;

import use_case.convert.ExchangeRateDataAccessInterface;
import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * The Interactor for the Historical Trends Use Case.
 * <p>
 * This class implements the business logic for retrieving and processing historical exchange rate data.
 * It coordinates between the Data Access layer (to fetch raw data) and the Presenter (to prepare data for the View).
 * It is responsible for:
 * <ul>
 * <li>Calculating the date range based on the user's selected time period.</li>
 * <li>Resolving currency names to currency codes using the repository.</li>
 * <li>Fetching historical data via the {@link ExchangeRateDataAccessInterface}.</li>
 * <li>Transforming entity data into a format suitable for the View (e.g., converting Instant to LocalDate).</li>
 * </ul>
 */
public class TrendsInteractor implements TrendsInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccessObject;
    private final TrendsOutputBoundary trendsPresenter;
    private final CurrencyRepository currencyRepository;

    /**
     * Constructs a new TrendsInteractor.
     *
     * @param dataAccessObject   The data access object used to fetch historical exchange rates from the API.
     * @param currencyRepository The repository used to look up Currency entities by their display name.
     * @param trendsPresenter    The presenter used to update the view with success or failure states.
     */
    public TrendsInteractor(ExchangeRateDataAccessInterface dataAccessObject,
                            CurrencyRepository currencyRepository,
                            TrendsOutputBoundary trendsPresenter) {
        this.dataAccessObject = dataAccessObject;
        this.currencyRepository = currencyRepository;
        this.trendsPresenter = trendsPresenter;
    }

    /**
     * Executes the Historical Trends Use Case.
     * <p>
     * This method calculates the start and end dates based on the input period,
     * resolves the currency names to entities, fetches the data, and passes the result
     * to the presenter.
     *
     * @param inputData The input data containing the base currency name, target currency name, and time period.
     */
    @Override
    public void execute(TrendsInputData inputData) {
        try {
            // 1. Validate Inputs (Prevent NPEs)
            if (inputData.getTimePeriod() == null) {
                trendsPresenter.prepareFailView("Error: Time period cannot be empty.");
                return;
            }
            if (inputData.getBaseCurrency() == null || inputData.getTargetCurrency() == null) {
                trendsPresenter.prepareFailView("Error: Currency selection cannot be empty.");
                return;
            }

            String baseName = inputData.getBaseCurrency();
            String targetName = inputData.getTargetCurrency();
            String period = inputData.getTimePeriod();

            // 2. Calculate Dates based on period (Business Logic)
            // Safe to switch now because we checked for null above
            LocalDate end = LocalDate.now();
            LocalDate start = switch (period) {
                case "1 month" -> end.minusMonths(1);
                case "6 months" -> end.minusMonths(6);
                case "1 year" -> end.minusYears(1);
                default -> end.minusWeeks(1); // default is 1 week
            };

            // 3. Create Entity shells for the DAO using the repository for lookup
            Currency base = currencyRepository.getByName(baseName);
            Currency target = currencyRepository.getByName(targetName);

            // Validate Repository Results (Prevent "Ghost Currency" Crash)
            if (base == null) {
                trendsPresenter.prepareFailView("Error: Base currency '" + baseName + "' not supported.");
                return;
            }
            if (target == null) {
                trendsPresenter.prepareFailView("Error: Target currency '" + targetName + "' not supported.");
                return;
            }

            // 4. Call DAO and get list of ENTITIES
            List<CurrencyConversion> conversions = dataAccessObject.getHistoricalRates(base, target, start, end);

            // 5. Unpack Entities into primitive lists for the View
            ArrayList<LocalDate> dates = new ArrayList<>();
            ArrayList<Double> rates = new ArrayList<>();

            for (CurrencyConversion conversion : conversions) {
                // Convert Instant -> LocalDate (legacy Date object support for JFreeChart if needed)
                LocalDate date = conversion.getTimeStamp()
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate();
                dates.add(date);
                rates.add(conversion.getRate());
            }

            // 6. Output
            TrendsOutputData output = new TrendsOutputData(baseName, targetName, dates, rates, false);
            trendsPresenter.prepareSuccessView(output);

        } catch (Exception e) {
            // Log the error for debugging purposes
            e.printStackTrace();
            // Present a failure view to the user (Safe fallback for any crash)
            trendsPresenter.prepareFailView("System Error: " + e.getMessage());
        }
    }

    /**
     * Navigates back to the Home View.
     * <p>
     * Delegates the navigation logic to the presenter.
     */
    @Override
    public void switchToHomeView() {
        trendsPresenter.prepareHomeView();
    }

    @Override
    public void executeInitialLoad() {
        trendsPresenter.prepareInitialView();
    }
}