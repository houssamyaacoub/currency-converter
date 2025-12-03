package use_case.compare_currencies;

/**
 * Input boundary (interface) for the "compare multiple currencies" use case.
 *
 * <p>The controller will call this instead of talking to the interactor directly.
 */
public interface CompareCurrenciesInputBoundary {

    /**
     * Run the compare-currencies use case.
     *
     * @param inputData holds the base currency name and the list of selected target currencies
     */
    void execute(CompareCurrenciesInputData inputData);
}
