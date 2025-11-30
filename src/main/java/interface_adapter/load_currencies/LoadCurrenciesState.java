package interface_adapter.load_currencies;


/**
 * Tracks the status of the asynchronous currency list loading operation.
 */
public class LoadCurrenciesState {
    private boolean loading = true;
    private String loadError = null;

    public LoadCurrenciesState() {}

    // --- Getters ---
    public boolean isLoading() { return loading; }
    public String getLoadError() { return loadError; }

    // --- Setters ---
    public void setLoading(boolean loading) { this.loading = loading; }
    public void setLoadError(String loadError) { this.loadError = loadError; }
}