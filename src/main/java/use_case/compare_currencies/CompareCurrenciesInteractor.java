package use_case.compare_currencies;

import java.util.ArrayList;
import java.util.List;

import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

/**
 * Core business logic for Use Case 6: compare multiple currencies.
 *
 * <p>This class:
 * <ul>
 *     <li>Validates the user's selected currencies.</li>
 *     <li>Looks up the Currency entities.</li>
 *     <li>Asks the data access layer for the latest exchange rates.</li>
 *     <li>Builds output data and sends it to the presenter.</li>
 * </ul>
 */
public class CompareCurrenciesInteractor implements CompareCurrenciesInputBoundary {

    /** Maximum number of currencies that can be compared at once. */
    private static final int MAX_TARGETS = 5;

    /** DAO for fetching latest exchange rates. */
    private final ExchangeRateDataAccessInterface dataAccess;

    /** Repository for looking up Currency entities by name. */
    private final CurrencyRepository currencyRepository;

    /** Presenter that formats and forwards the results to the UI layer. */
    private final CompareCurrenciesOutputBoundary presenter;

    /**
     * Creates a new interactor for the compare-currencies use case.
     *
     * @param dataAccess         data access object for exchange rates
     * @param currencyRepository repository used to look up Currency objects
     * @param presenter          output boundary that will handle success or failure
     */
    public CompareCurrenciesInteractor(ExchangeRateDataAccessInterface dataAccess,
                                       CurrencyRepository currencyRepository,
                                       CompareCurrenciesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.currencyRepository = currencyRepository;
        this.presenter = presenter;
    }

    /**
     * Main use-case method. Takes the input data, does validation and lookups,
     * then sends the result (or error message) to the presenter.
     *
     * @param inputData base currency and list of target currencies
     */
    @Override
    public void execute(CompareCurrenciesInputData inputData) {
        try {
            // Pull values out of the input object so they're easier to work with
            final String baseName = inputData.getBaseCurrencyName();
            final List<String> targets = inputData.getTargetCurrencyNames();

            boolean valid = true;

            // Basic sanity checks on the user's selection
            if (targets == null || targets.isEmpty()) {
                presenter.prepareFailView("Please select at least one target currency.");
                valid = false;
            } else if (targets.size() > MAX_TARGETS) {
                presenter.prepareFailView(
                        "You can compare at most " + MAX_TARGETS + " currencies.");
                valid = false;
            }

            if (valid) {
                // Look up the base Currency entity by its name/code
                final Currency baseCurrency = currencyRepository.getByName(baseName);

                // We'll only keep valid / non-duplicate targets in these lists
                final List<String> cleanedTargets = new ArrayList<>();
                final List<Double> rates = new ArrayList<>();

                for (String targetName : targets) {
                    // Skip nulls and "same as base" to avoid silly or broken bars in the chart
                    if (targetName == null || targetName.equals(baseName)) {
                        continue;
                    }

                    // Look up the target currency entity
                    final Currency targetCurrency = currencyRepository.getByName(targetName);

                    // Ask the DAO for the latest rate: "how many units of target per 1 base"
                    final CurrencyConversion conversion =
                            dataAccess.getLatestRate(baseCurrency, targetCurrency);

                    cleanedTargets.add(targetName);
                    rates.add(conversion.getRate());
                }

                // If everything got filtered out, there's nothing useful to show to the user
                if (cleanedTargets.isEmpty()) {
                    presenter.prepareFailView("No valid target currencies selected.");
                } else {
                    // Wrap up the formatted data for the presenter (and then the UI)
                    final CompareCurrenciesOutputData outputData =
                            new CompareCurrenciesOutputData(baseName, cleanedTargets, rates);

                    presenter.present(outputData);
                }
            }
        }
        catch (Exception ex) {
            // Catch any unexpected errors and bubble up a friendly message
            presenter.prepareFailView("Error comparing currencies: " + ex.getMessage());
        }
    }
}
