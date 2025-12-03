package interface_adapter.convert_currency;

import use_case.convert.ConvertInputBoundary;
import use_case.convert.ConvertInputData;

/**
 * Controller for the Convert Currency Use Case.
 * This component acts as an Interface Adapter, bridging the View and the Use Case Interactor.
 * It receives raw user input (strings), converts it into the appropriate Data Transfer Object
 * (Input Data), and initiates the business logic execution.
 */
public class ConvertController {

    private final ConvertInputBoundary convertUseCase;

    /**
     * Constructs a new ConvertController.
     *
     * @param convertUseCase The Input Boundary for the conversion use case (typically the Interactor).
     */
    public ConvertController(ConvertInputBoundary convertUseCase) {
        this.convertUseCase = convertUseCase;
    }

    /**
     * Executes the currency conversion process based on user input.
     * This method handles the transformation of raw view data into application-specific
     * input data and delegates the logic to the Interactor.
     *
     * @param amountStr        The raw amount string entered by the user (e.g., "100.50").
     * @param fromCurrencyName The display name of the source currency (e.g., "Turkish Lira").
     * @param toCurrencyName   The display name of the target currency (e.g., "US Dollar").
     */
    public void execute(String amountStr, String fromCurrencyName, String toCurrencyName) {

        // --- Input Data Transformation ---
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        }
        catch (NumberFormatException e) {
            // If parsing fails, we pass 0.0. The Interactor is responsible for validating
            // the amount (e.g., checking if it's positive) and handling business rule failures.
            amount = 0.0;
        }

        // Create the Input Data object (DTO)
        // Note: We pass the currency names directly; the Interactor will handle resolving them to entities.
        final ConvertInputData inputData = new ConvertInputData(amount, fromCurrencyName, toCurrencyName);

        // --- Trigger Use Case ---
        convertUseCase.execute(inputData);
    }
}
