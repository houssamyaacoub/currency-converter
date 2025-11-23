package use_case.convert_multiple;

import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public class ConvertMultipleInteractor implements ConvertMultipleInputBoundary {

    private static final int MAX_TARGETS = 5;

    private final ExchangeRateDataAccessInterface dataAccess;
    private final ConvertMultipleOutputBoundary presenter;
    private final CurrencyRepository currencyRepository;

    public ConvertMultipleInteractor(ExchangeRateDataAccessInterface dataAccess,
                                     ConvertMultipleOutputBoundary presenter,
                                     CurrencyRepository currencyRepository) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void execute(ConvertMultipleInputData inputData) {
        double amount = inputData.getAmount();
        String baseName = inputData.getFromCurrencyName();
        List<String> targets = new ArrayList<>(inputData.getTargetCurrencyNames());

        if (amount < 0) {
            presenter.prepareFailView("Amount must be non-negative.");
            return;
        }
        if (baseName == null || baseName.isBlank()) {
            presenter.prepareFailView("Base currency must be selected.");
            return;
        }
        if (targets.isEmpty()) {
            presenter.prepareFailView("At least one target currency must be selected.");
            return;
        }

        boolean limited = false;
        if (targets.size() > MAX_TARGETS) {
            targets = targets.subList(0, MAX_TARGETS);
            limited = true;
        }

        Currency baseCurrency;
        try {
            baseCurrency = currencyRepository.getByName(baseName);
        } catch (Exception e) {
            presenter.prepareFailView("Unknown base currency: " + baseName);
            return;
        }

        List<ConvertMultipleOutputData.ConversionResult> conversions = new ArrayList<>();
        List<String> failedTargets = new ArrayList<>();

        for (String targetName : targets) {
            try {
                Currency targetCurrency = currencyRepository.getByName(targetName);
                CurrencyConversion conversion = dataAccess.getLatestRate(baseCurrency, targetCurrency);
                double convertedAmount = conversion.calculateConvertedAmount(amount);
                ConvertMultipleOutputData.ConversionResult result =
                        new ConvertMultipleOutputData.ConversionResult(
                                targetName,
                                conversion.getToCurrency().getSymbol(),
                                conversion.getRate(),
                                convertedAmount,
                                conversion.getTimeStamp()
                        );
                conversions.add(result);
            } catch (Exception e) {
                failedTargets.add(targetName);
            }
        }

        if (conversions.isEmpty()) {
            presenter.prepareFailView("No valid target currencies could be converted.");
            return;
        }

        ConvertMultipleOutputData outputData =
                new ConvertMultipleOutputData(amount, baseName, limited, conversions, failedTargets);
        presenter.present(outputData);
    }
}
