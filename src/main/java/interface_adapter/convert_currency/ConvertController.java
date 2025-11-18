package interface_adapter.convert_currency;

import use_case.convert.ConvertInputBoundary;
import use_case.convert.ConvertInputData;

public class ConvertController {

    private final ConvertInputBoundary convertUseCase;

    public ConvertController(ConvertInputBoundary convertUseCase) {
        this.convertUseCase = convertUseCase;
    }

    /**
     * Executes conversion based on live input changes (triggered by DocumentListener/JComboBox).
     * This method packages the raw UI inputs into the InputData object and calls the Interactor.
     * * @param amountStr The raw amount string currently in the text field.
     * @param fromCode The current 'from' currency code.
     * @param toCode The current 'to' currency code.
     */
    public void execute(String amountStr, String fromCode, String toCode) {

        // --- Input Data Transformation ---
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            // Pass 0.0 or a sentinel value; the Interactor must validate and send an error.
            amount = 0.0;
        }

        ConvertInputData inputData = new ConvertInputData(amount, fromCode, toCode);

        // --- Call the Use Case Interactor ---
        convertUseCase.execute(inputData);
    }
}