package use_case.load_currencies;
/**
 * Output Boundary for the Load Currencies Use Case.
 */
public interface LoadCurrenciesOutputBoundary {

    /**
     * Prepares the success view with the list of currencies.
     * @param outputData The list of currency codes/names.
     */
    void presentSuccessView(LoadCurrenciesOutputData outputData);

    /**
     * Prepares the fail view if data fetching fails.
     * @param errorMessage The error message to display.
     */
    void prepareFailView(String errorMessage);
}