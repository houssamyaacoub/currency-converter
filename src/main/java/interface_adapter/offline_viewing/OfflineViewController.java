package interface_adapter.offline_viewing;

import use_case.offline_viewing.OfflineViewInputBoundary;

/**
 * Thin controller for offline viewing.
 * Used by ConvertView to trigger loading of cached data.
 */
public class OfflineViewController {

    private final OfflineViewInputBoundary interactor;

    public OfflineViewController(OfflineViewInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadOfflineRates() {
        interactor.execute();
    }
}
