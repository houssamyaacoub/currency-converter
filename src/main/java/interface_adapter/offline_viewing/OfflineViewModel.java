package interface_adapter.offline_viewing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OfflineViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // “Offline Data – Last Updated: …”
    private String statusMessage = "";

    // When the rates were last updated
    private Instant timestamp;

    // Cached rates (e.g., "USD" -> 1.0, "EUR" -> 0.91, etc.)
    private Map<String, Double> rates = new HashMap<>();

    // Getters
    public String getStatusMessage() { return statusMessage; }

    public Instant getTimestamp() { return timestamp; }

    public Map<String, Double> getRates() {
        return Collections.unmodifiableMap(rates);
    }

    // Setters – package-private so only presenter usually calls them
    void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        firePropertyChanged();
    }

    void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        firePropertyChanged();
    }

    void setRates(Map<String, Double> rates) {
        this.rates.clear();
        if (rates != null) {
            this.rates.putAll(rates);
        }
        firePropertyChanged();
    }

    // Observer wiring
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    private void firePropertyChanged() {
        support.firePropertyChange("offlineViewModel", null, this);
    }
}
