package use_case.convert;

import entity.CurrencyConversion;
import entity.Currency;

public class ConvertCurrencyInteractor implements ConvertInputBoundary {

    private final ExchangeRateDataAccessInterface dataAccess;
    private final ConvertOutputBoundary presenter;
    private final CurrencyRepository currencyRepository;

    public ConvertCurrencyInteractor(ExchangeRateDataAccessInterface dataAccess,
                                     ConvertOutputBoundary presenter,
                                     CurrencyRepository currencyRepository) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void execute(ConvertInputData inputData) {
        try {
            // 1. InputData stores full currency names (e.g., "Turkish Lira")
            Currency fromCurrency = currencyRepository.getByName(inputData.getFromCurrency());
            Currency toCurrency   = currencyRepository.getByName(inputData.getToCurrency());

            // 2. DAO gets the latest rate (online or offline from cache).
            //    If offline + pair NOT cached, this will throw.
            CurrencyConversion conversionEntity =
                    dataAccess.getLatestRate(fromCurrency, toCurrency);

            double convertedAmount =
                    conversionEntity.calculateConvertedAmount(inputData.getAmount());

            ConvertOutputData successOutput = new ConvertOutputData(
                    convertedAmount,
                    conversionEntity.getRate(),
                    conversionEntity.getToCurrency().getSymbol(), // for display
                    conversionEntity.getTimeStamp()
            );

            presenter.present(successOutput);

        } catch (Exception e) {
            String message = (e.getMessage() == null || e.getMessage().isEmpty())
                    ? "Conversion failed."
                    : e.getMessage();

            presenter.prepareFailView(message);
        }
    }
}
