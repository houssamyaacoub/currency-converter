package use_case.compare_currencies;

/**
 * Output boundary (interface) for the "compare multiple currencies" use case.
 * <p>
 * The interactor talks to this instead of directly updating the UI.
 */
public interface CompareCurrenciesOutputBoundary {

    /**
     * Called when the compare-currencies use case finishes successfully.
     *
     * @param data contains all information needed to show the comparison result
     */
    void present(CompareCurrenciesOutputData data);

    /**
     * Called when something goes wrong (validation error, exception, etc.).
     *
     * @param errorMessage a user-friendly description of what went wrong
     */
    void prepareFailView(String errorMessage);
}
