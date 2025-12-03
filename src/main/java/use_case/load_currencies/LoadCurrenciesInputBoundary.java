package use_case.load_currencies;

/**
 * Input Boundary for the Load Currencies Use Case.
 * This interface defines the contract for triggering the loading of currency data.
 * It is implemented by the {@link LoadCurrenciesInteractor} and called by the
 * {@link interface_adapter.load_currencies.LoadCurrenciesController}.
 */
public interface LoadCurrenciesInputBoundary {

    /**
     * Executes the Load Currencies use case.
     * This method initiates the process of fetching all available currencies
     * from the data access layer and passing them to the presenter.
     */
    void execute();
}