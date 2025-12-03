package interface_adapter.load_currencies;

/**
 * State object for the Load Currencies Use Case.
 * This class acts as a Data Transfer Object (DTO) that holds the status of the
 * asynchronous currency loading operation. It tracks whether the process is
 * ongoing and stores any error messages that occur, allowing the View to
 * react (e.g., show a loading spinner or an error dialog).
 */
public class LoadCurrenciesState {
    private boolean loading = true;
    private String loadError = null;

    /**
     * Constructs a new LoadCurrenciesState.
     * The initial state assumes loading is active (`loading = true`) and
     * no errors have occurred yet (`loadError = null`).
     */
    public LoadCurrenciesState() {}

    /**
     * Checks if the currency loading operation is currently in progress.
     *
     * @return {@code true} if loading is active, {@code false} otherwise.
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * Retrieves the error message associated with the loading operation, if any.
     *
     * @return A string containing the error message, or {@code null} if no error occurred.
     */
    public String getLoadError() {
        return loadError;
    }

    /**
     * Sets the loading status.
     *
     * @param loading {@code true} to indicate loading is in progress, {@code false} otherwise.
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * Sets the error message for a failed loading operation.
     *
     * @param loadError The error message to display.
     */
    public void setLoadError(String loadError) {
        this.loadError = loadError;
    }
}