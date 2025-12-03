package use_case.travel_budget;

import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Interactor for the Travel Budget use case.
 * <br>
 * Implements the application-specific business rules for calculating a
 * travel budget across multiple currencies and converting everything
 * into a single home currency.
 */
public class TravelBudgetInteractor implements TravelBudgetInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccess;
    private final CurrencyRepository currencyRepository;
    private final TravelBudgetOutputBoundary presenter;

    /**
     * Creates a new TravelBudgetInteractor.
     *
     * @param dataAccess          the API interface used to fetch exchange rates
     * @param currencyRepository  the repository that provides Currency objects by name
     * @param presenter           the output boundary that formats and displays the results
     */
    public TravelBudgetInteractor(ExchangeRateDataAccessInterface dataAccess,
                                  CurrencyRepository currencyRepository,
                                  TravelBudgetOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.currencyRepository = currencyRepository;
        this.presenter = presenter;
    }

    /**
     * Executes the travel budget calculation.
     * <br>
     * Steps:
     * 1) Validate the input lists and the home currency.
     * 2) For each item, look up the source currency, fetch the latest rate
     *    to the home currency, and convert the amount.
     * 3) Accumulate the total in the home currency and build a breakdown
     *    line for each item.
     * 4) Send a {@link TravelBudgetOutputData} object to the presenter.
     *
     * @param inputData the input data for the travel budget calculation
     */
    @Override
    public void execute(TravelBudgetInputData inputData) {
        try {
            List<String> names = inputData.getItemCurrencyNames();
            List<Double> amounts = inputData.getAmounts();
            String homeName = inputData.getHomeCurrencyName();

            // Basic validation
            if (names == null || amounts == null ||
                    names.isEmpty() || amounts.isEmpty() ||
                    names.size() != amounts.size()) {
                presenter.prepareFailView("Please provide the same number of currencies and amounts.");
                return;
            }

            if (names.size() > 5) {
                presenter.prepareFailView("You can add at most 5 expense currencies.");
                return;
            }

            Currency homeCurrency = currencyRepository.getByName(homeName);
            if (homeCurrency == null) {
                presenter.prepareFailView("Unknown home currency: " + homeName);
                return;
            }

            double total = 0.0;
            List<String> breakdown = new ArrayList<>();

            for (int i = 0; i < names.size(); i++) {
                String fromName = names.get(i);
                double amount   = amounts.get(i);

                Currency fromCurrency = currencyRepository.getByName(fromName);
                if (fromCurrency == null) {
                    presenter.prepareFailView("Unknown currency: " + fromName);
                    return;
                }

                // Perform API conversion
                CurrencyConversion conversion =
                        dataAccess.getLatestRate(fromCurrency, homeCurrency);

                double converted = conversion.calculateConvertedAmount(amount);
                total += converted;

                // --- Build breakdown line ---
                String line = String.format(
                        Locale.US,
                        "%.2f %s -> %.2f %s",
                        amount,
                        fromCurrency.getName(),
                        converted,
                        homeCurrency.getName()
                );
                breakdown.add(line);

                // to avoid hitting 429 error
                try {
                    Thread.sleep(200);      // 200 ms delay between API calls
                } catch (InterruptedException ignored) {
                    // ignore
                }
            }



            TravelBudgetOutputData output = new TravelBudgetOutputData(
                    homeCurrency.getName(),
                    total,
                    breakdown,
                    false
            );
            presenter.prepareSuccessView(output);

        } catch (Exception e) {
            presenter.prepareFailView("Travel budget error: " + e.getMessage());
        }
    }

    /**
     * Requests that the UI switch back to the home view.
     */
    @Override
    public void switchToHomeView() {
        presenter.prepareHomeView();
    }
}
