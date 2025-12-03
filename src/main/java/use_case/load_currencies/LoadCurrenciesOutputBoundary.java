package use_case.load_currencies;

/**
 * The Output Boundary Interface (Port) for the Load Currencies Use Case.
 * This interface is implemented by the Presenter and is called by the Interactor
 * to deliver the results of the currency loading operation (success or failure).
 */
public interface LoadCurrenciesOutputBoundary {

    /**
     * Prepares the success view with the loaded list of currencies.
     *
     * @param outputData The {@link LoadCurrenciesOutputData} containing the list
     * of available currency codes or names.
     */
    void presentSuccessView(LoadCurrenciesOutputData outputData);

    /**
     * Prepares the failure view when the currency list cannot be loaded.
     *
     * @param errorMessage A string explaining why the data fetching failed.
     */
    void prepareFailView(String errorMessage);
}