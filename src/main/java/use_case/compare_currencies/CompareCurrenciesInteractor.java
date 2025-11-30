package use_case.compare_currencies;

import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

// Core business logic for Use Case 6 (multi-currency compare)
public class CompareCurrenciesInteractor implements CompareCurrenciesInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccess;
    private final CurrencyRepository currencyRepository;
    private final CompareCurrenciesOutputBoundary presenter;

    public CompareCurrenciesInteractor(ExchangeRateDataAccessInterface dataAccess,
                                       CurrencyRepository currencyRepository,
                                       CompareCurrenciesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.currencyRepository = currencyRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(CompareCurrenciesInputData inputData) {
        try {
            String baseName = inputData.getBaseCurrencyName();
            List<String> targets = inputData.getTargetCurrencyNames();

            if (targets == null || targets.isEmpty()) {
                presenter.prepareFailView("Please select at least one target currency.");
                return;
            }

            if (targets.size() > 5) {
                presenter.prepareFailView("You can compare at most 5 currencies.");
                return;
            }

            // Look up the base Currency entity by name (same pattern as ConvertCurrencyInteractor)
            Currency baseCurrency = currencyRepository.getByName(baseName);

            List<String> cleanedTargets = new ArrayList<>();
            List<Double> rates = new ArrayList<>();

            for (String targetName : targets) {
                if (targetName == null || targetName.equals(baseName)) {
                    // Skip invalid or identical entries so they don't clutter the chart
                    continue;
                }

                Currency targetCurrency = currencyRepository.getByName(targetName);

                // Reuse existing DAO: rate is "how many units of target per 1 base"
                CurrencyConversion conversion = dataAccess.getLatestRate(baseCurrency, targetCurrency);
                cleanedTargets.add(targetName);
                rates.add(conversion.getRate());
            }

            if (cleanedTargets.isEmpty()) {
                presenter.prepareFailView("No valid target currencies selected.");
                return;
            }

            CompareCurrenciesOutputData outputData =
                    new CompareCurrenciesOutputData(baseName, cleanedTargets, rates);

            presenter.present(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Error comparing currencies: " + e.getMessage());
        }
    }
}
