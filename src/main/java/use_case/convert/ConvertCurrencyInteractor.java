package use_case.convert;

import entity.CurrencyConversion;
import entity.Currency;

/**
 * The Interactor for the Convert Currency Use Case.
 * Now manages obtaining the full Currency entities before calling the DAO.
 */
public class ConvertCurrencyInteractor implements ConvertInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccess;
    private final ConvertOutputBoundary presenter;
    // full Currency objects (required by the DAO)
    private final CurrencyRepository currencyRepository;

    public ConvertCurrencyInteractor(
            ExchangeRateDataAccessInterface dataAccess,
            ConvertOutputBoundary presenter,
            CurrencyRepository currencyRepository) { // Dependency injected
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void execute(ConvertInputData inputData) {

        // --- Entity Lookup ---
        // Get the full Currency Entities based on the codes from the InputData.
        Currency fromCurrency;
        Currency toCurrency;

        fromCurrency = currencyRepository.getByCode(inputData.getFromCurrency());
        toCurrency = currencyRepository.getByCode(inputData.getToCurrency());

        CurrencyConversion conversionEntity;

        conversionEntity = dataAccess.getLatestRate(fromCurrency, toCurrency);

        // --- Core Business Logic (Call Entity) ---
        double convertedAmount = conversionEntity.calculateConvertedAmount(inputData.getAmount());

        // --- Prepare and Present Output (Main Flow Success) ---
        // Note: conversionEntity now has the full Currency objects.
        ConvertOutputData successOutput = new ConvertOutputData(
                convertedAmount,
                conversionEntity.getRate(),
                conversionEntity.getToCurrency().getName(), // Use the code for output data
                conversionEntity.getTimeStamp()
        );

        presenter.present(successOutput);
    }
}