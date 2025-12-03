package interface_adapter.load_currencies;

import use_case.load_currencies.LoadCurrenciesInputBoundary;

/**
 * Controller for the Load Currencies Use Case.
 * This component acts as an Interface Adapter, triggering the execution of the
 * Load Currencies Use Case. It is typically invoked during application initialization
 * or when a refresh of the currency list is required by the UI.
 */
public class LoadCurrenciesController {

    private final LoadCurrenciesInputBoundary loadCurrenciesUseCase;

    /**
     * Constructs a new LoadCurrenciesController.
     *
     * @param loadCurrenciesUseCase The Input Boundary for the use case (typically the Interactor).
     */
    public LoadCurrenciesController(LoadCurrenciesInputBoundary loadCurrenciesUseCase) {
        this.loadCurrenciesUseCase = loadCurrenciesUseCase;
    }

    /**
     * Executes the Load Currencies use case.
     * This method triggers the Interactor to fetch the currency data and update
     * the relevant ViewModels.
     */
    public void execute() {
        loadCurrenciesUseCase.execute();
    }
}