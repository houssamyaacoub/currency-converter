package interface_adapter.convert_currency;


import use_case.convert.ConvertInputBoundary;

import use_case.convert.ConvertInputData;


public class ConvertController {


    private final ConvertInputBoundary convertUseCase;


    public ConvertController(ConvertInputBoundary convertUseCase) {

        this.convertUseCase = convertUseCase;

    }


    /**

     * Executes conversion based on user input.

     * This method packages the raw UI inputs (currency names) into the InputData object

     * and calls the Interactor.

     * * @param amountStr        The raw amount string currently in the text field.

     * @param fromCurrencyName The selected 'from' currency Name (e.g., "Turkish Lira").

     * @param toCurrencyName   The selected 'to' currency Name (e.g., "US Dollar").

     */

    public void execute(String amountStr, String fromCurrencyName, String toCurrencyName) {


        // --- Input Data Transformation ---

        double amount;

        try {

            amount = Double.parseDouble(amountStr);

        } catch (NumberFormatException e) {

            // Pass 0.0 or a sentinel value; the Interactor must validate and send an error.

            amount = 0.0;

        }


        // Create input data with NAMES, which the Interactor will use via 'getByName'

        ConvertInputData inputData = new ConvertInputData(amount, fromCurrencyName, toCurrencyName);


        // --- Call the Use Case Interactor ---

        convertUseCase.execute(inputData);

    }

}