package interface_adapter.logged_in; // Or interface_adapter.home

import use_case.convert.ConvertInputBoundary;
import use_case.convert.ConvertInputData;

public class ConvertController {

    // We rely on the Interface, not the specific Interactor class
    final ConvertInputBoundary userConvertUseCaseInteractor;

    public ConvertController(ConvertInputBoundary userConvertUseCaseInteractor) {
        this.userConvertUseCaseInteractor = userConvertUseCaseInteractor;
    }

    /**
     * The View calls this method when the button is clicked.
     */
    public void execute(String amount, String fromCurrency, String toCurrency) {
        // 1. Handle basic data conversion (String -> Double)
        // Ideally, you validate this here or in the InputData.
        double amountValue = 0.0;
        try {
            amountValue = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            // If the user typed "abc", we define a default or handle the error.
            // For now, sending 0.0 allows the Interactor to catch the "invalid amount" logic.
            amountValue = 0.0;
        }

        // 2. Bundle the data into an InputData object
        ConvertInputData inputData = new ConvertInputData(amountValue, fromCurrency, toCurrency);

        // 3. Pass the baton to the Interactor
        userConvertUseCaseInteractor.execute(inputData);
    }
}