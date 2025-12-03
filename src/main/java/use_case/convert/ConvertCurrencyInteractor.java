package use_case.convert;

import entity.CurrencyConversion;
import entity.Currency;

/**
 * The Interactor for the Convert Currency Use Case.
 * This class implements the core business logic for performing a single currency conversion.
 * It is responsible for:
 * - Resolving currency names (e.g., "Turkish Lira") to {@link Currency} entities (codes).</li>
 * - Fetching the latest exchange rate from the Data Access Layer.</li>
 * - Applying the calculation rule (multiplication) defined in the {@link CurrencyConversion} entity.</li>
 * - Coordinating the result and passing it to the Presenter.</li>
 */
public class ConvertCurrencyInteractor implements ConvertInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccess;
    private final ConvertOutputBoundary presenter;
    private final CurrencyRepository currencyRepository;

    /**
     * Constructs a new ConvertCurrencyInteractor.
     *
     * @param dataAccess The DAO used to fetch the current exchange rate from the external source.
     * @param presenter The output boundary used to update the view with success or failure states.
     * @param currencyRepository The repository used to look up Currency entities by their display name.
     */
    public ConvertCurrencyInteractor(ExchangeRateDataAccessInterface dataAccess,
                                     ConvertOutputBoundary presenter,
                                     CurrencyRepository currencyRepository) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.currencyRepository = currencyRepository;
    }

    /**
     * Executes the single currency conversion use case.
     *
     * @param inputData The input data containing the amount and the display names of the currencies.
     */
    @Override
    public void execute(ConvertInputData inputData) {
        try {
            // 1. Resolve Currency Names to Entities (uses repository for lookup)
            // If the name is not found, the repository will throw an exception caught below.
            Currency fromCurrency = currencyRepository.getByName(inputData.getFromCurrency());
            Currency toCurrency = currencyRepository.getByName(inputData.getToCurrency());

            // 2. Fetch Exchange Rate Entity from the DAO
            // The DAO uses the Codes stored in the Currency entities to build the API request.
            CurrencyConversion conversionEntity =
                    dataAccess.getLatestRate(fromCurrency, toCurrency);

            // 3. Apply Core Calculation Rule (Multiplication)
            double convertedAmount =
                    conversionEntity.calculateConvertedAmount(inputData.getAmount());

            // 4. Prepare Success Output Data
            ConvertOutputData successOutput = new ConvertOutputData(
                    convertedAmount,
                    conversionEntity.getRate(),
                    conversionEntity.getToCurrency().getSymbol(), // Code/Symbol for display consistency
                    conversionEntity.getTimeStamp()
            );

            // 5. Pass result to Presenter
            presenter.present(successOutput);

        } catch (Exception e) {
            // 6. Handle failure (e.g., repository lookup error, API failure, or parsing crash)
            e.printStackTrace(); // Log the full stack trace for debugging

            String message = (e.getMessage() == null || e.getMessage().isEmpty())
                    ? "Conversion failed due to an unknown system error."
                    : e.getMessage();

            presenter.prepareFailView(message);
        }
    }
}