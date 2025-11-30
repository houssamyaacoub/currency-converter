package interface_adapter.load_currencies;

import use_case.load_currencies.LoadCurrenciesInputBoundary;

/**
 * Controller responsible for triggering the Load Currencies Use Case.
 */
public class LoadCurrenciesController {

    private final LoadCurrenciesInputBoundary loadCurrenciesUseCase;

    public LoadCurrenciesController(LoadCurrenciesInputBoundary loadCurrenciesUseCase) {
        this.loadCurrenciesUseCase = loadCurrenciesUseCase;
    }

    /**
     * Executes the initial data load command.
     */
    public void execute() {
        // We call the Interactor. It doesn't need input, but we call the boundary method.
        loadCurrenciesUseCase.execute();
    }
}