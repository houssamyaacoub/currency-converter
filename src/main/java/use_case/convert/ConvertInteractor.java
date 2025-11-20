package use_case.convert;

import data_access.ApiConversionResult;

public class ConvertInteractor implements ConvertInputBoundary {

    private final ConvertDataAccessInterface convertDataAccessInterface;
    private final ConvertOutputBoundary convertOutputBoundary;

    public ConvertInteractor(ConvertDataAccessInterface convertDataAccessInterface,
                             ConvertOutputBoundary convertOutputBoundary) {
        this.convertDataAccessInterface = convertDataAccessInterface;
        this.convertOutputBoundary = convertOutputBoundary;
    }

    @Override
    public void execute(ConvertInputData convertInputData) {
        double amount = convertInputData.getAmount();
        String from = convertInputData.getFromCurrency();
        String to = convertInputData.getToCurrency();

        if (amount < 0) {
            convertOutputBoundary.prepareFailView("Amount must be non-negative.");
            return;
        }
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            convertOutputBoundary.prepareFailView("Both currencies must be selected.");
            return;
        }

        try {
            ApiConversionResult result =
                    convertDataAccessInterface.getConversion(from, to, amount);

            ConvertOutputData outputData = new ConvertOutputData(
                    amount,
                    from,
                    to,
                    result.getRate(),
                    result.getResult(),
                    result.getTimestamp()
            );

            convertOutputBoundary.prepareSuccessView(outputData);

        } catch (RuntimeException ex) {
            convertOutputBoundary.prepareFailView("Conversion failed: " + ex.getMessage());
        }
    }
}
