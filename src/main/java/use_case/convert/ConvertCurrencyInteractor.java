package use_case.convert;


import entity.CurrencyConversion;

import entity.Currency;


public class ConvertCurrencyInteractor implements ConvertInputBoundary {


    private final ExchangeRateDataAccessInterface dataAccess;

    private final ConvertOutputBoundary presenter;

    private final CurrencyRepository currencyRepository;


    public ConvertCurrencyInteractor(

            ExchangeRateDataAccessInterface dataAccess,

            ConvertOutputBoundary presenter,

            CurrencyRepository currencyRepository) {

        this.dataAccess = dataAccess;

        this.presenter = presenter;

        this.currencyRepository = currencyRepository;

    }


    @Override

    public void execute(ConvertInputData inputData) {

        try {

            // 1. Use getByName because InputData contains full names

            // (e.g., "Turkish Lira", not "TRY")

            Currency fromCurrency = currencyRepository.getByName(inputData.getFromCurrency());

            Currency toCurrency = currencyRepository.getByName(inputData.getToCurrency());


            // 2. The rest of the flow remains exactly the same.

            // The DAO will extract the codes from these Currency objects to call the API.

            CurrencyConversion conversionEntity = dataAccess.getLatestRate(fromCurrency, toCurrency);


            double convertedAmount = conversionEntity.calculateConvertedAmount(inputData.getAmount());


            ConvertOutputData successOutput = new ConvertOutputData(

                    convertedAmount,

                    conversionEntity.getRate(),

                    conversionEntity.getToCurrency().getSymbol(), // Use code for consistent display

                    conversionEntity.getTimeStamp()

            );


            presenter.present(successOutput);


        } catch (Exception e) {

            presenter.prepareFailView("Error: " + e.getMessage());

        }

    }

}